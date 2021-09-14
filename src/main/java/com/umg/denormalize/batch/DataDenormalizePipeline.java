package com.umg.denormalize.batch;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.coders.KvCoder;
import org.apache.beam.sdk.coders.SerializableCoder;
import org.apache.beam.sdk.coders.StringUtf8Coder;
import org.apache.beam.sdk.io.TextIO;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.transforms.Flatten;
import org.apache.beam.sdk.transforms.MapElements;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.transforms.SimpleFunction;
import org.apache.beam.sdk.transforms.join.CoGbkResult;
import org.apache.beam.sdk.transforms.join.CoGroupByKey;
import org.apache.beam.sdk.transforms.join.KeyedPCollectionTuple;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.PCollectionList;
import org.apache.beam.sdk.values.PCollectionTuple;
import org.apache.beam.sdk.values.TupleTag;
import org.apache.beam.sdk.values.TupleTagList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.aug.denormalize.model.ErrorRecord;
import com.aug.denormalize.model.StreamTrack;
import com.aug.denormalize.model.Streamz;
import com.aug.denormalize.model.Tracks;
import com.aug.denormalize.model.Users;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.umg.denormalize.options.DataDenormalizeOptions;
import com.umg.denormalize.transform.ConvertCbkResultToStreamTrack;
import com.umg.denormalize.transform.ConvertCbkResultToStreamsDenorm;
import com.umg.denormalize.transform.ConvertJsonToUserObeject;
import com.umg.denormalize.transform.ConvertJsontoStreamObeject;
import com.umg.denormalize.transform.ConvertJsontoTrackObeject;

public class DataDenormalizePipeline {

  final static TupleTag<Streamz> streamsTag = new TupleTag<>();
  final static TupleTag<Tracks> tracksTag = new TupleTag<>();
  final static TupleTag<Users> usersTag = new TupleTag<>();
  final static TupleTag<StreamTrack> streamTrackTag = new TupleTag<>();
  final static TupleTag<KV<String, Streamz>> streamzkVTag = new TupleTag<>();
  final static TupleTag<KV<String, Tracks>> trackskVTag = new TupleTag<>();
  final static TupleTag<KV<String, Users>> usersKVTag = new TupleTag<>();
  final static TupleTag<KV<String, StreamTrack>> streamTrackKVTag = new TupleTag<>();
  final static TupleTag<ErrorRecord> errorRecordTag = new TupleTag<>();
  final static List<String> fileList = Arrays.asList("streams.gz", "tracks.gz", "users.gz");

  private static Logger logger = LoggerFactory.getLogger(DataDenormalizePipeline.class);


  public static void main(String[] args) throws FileNotFoundException {

    DataDenormalizeOptions denormOptions =
        PipelineOptionsFactory.fromArgs(args).as(DataDenormalizeOptions.class);

    runPipeLine(denormOptions);
  }

/**
 * Method to launch the Dataflow pipeline.
 * @param options
 * @throws FileNotFoundException
 */
  public static void runPipeLine(DataDenormalizeOptions options) throws FileNotFoundException {
    //create piple line from options
    Pipeline denormPipeline = Pipeline.create(options);

    //Check if files exist
    for (String file : fileList) {
      checkFileExist(options.getLandingBucketName(), file);
    }

    //Read Streams file and convert it to Streamz POJO
    PCollectionTuple streams = denormPipeline
        .apply(TextIO.read().from("gs://" + options.getLandingBucketName() + "/streams.gz"))
        .apply(ParDo.of(new ConvertJsontoStreamObeject(errorRecordTag)).withOutputTags(streamzkVTag,
            TupleTagList.of(errorRecordTag)));//Tuple tag to get error records
    //Read Tracks file and convert it to tracks POJO
    PCollectionTuple tracks = denormPipeline
        .apply(TextIO.read().from("gs://" + options.getLandingBucketName() + "/tracks.gz"))
        .apply(ParDo.of(new ConvertJsontoTrackObeject(errorRecordTag)).withOutputTags(trackskVTag,
            TupleTagList.of(errorRecordTag)));
    //Read Users file and convert it to Users pojo.
    PCollectionTuple users = denormPipeline
        .apply(TextIO.read().from("gs://" + options.getLandingBucketName() + "/users.gz"))
        .apply(ParDo.of(new ConvertJsonToUserObeject(errorRecordTag)).withOutputTags(usersKVTag,
            TupleTagList.of(errorRecordTag)));
    //Join Streams and tracks based on User_id and get cogroupbykey results back.
    PCollection<KV<String, CoGbkResult>> streamTrackGbk =
        KeyedPCollectionTuple
            .of(streamsTag,
                streams.get(streamzkVTag).setCoder(
                    KvCoder.of(StringUtf8Coder.of(), SerializableCoder.of(Streamz.class))))
            .and(tracksTag,
                tracks.get(trackskVTag)
                    .setCoder(KvCoder.of(StringUtf8Coder.of(), SerializableCoder.of(Tracks.class))))
            .apply(CoGroupByKey.create());
    //Convert above cogroupbykey results to StreamTrack POJO and sideoutput records to errortag if User_id is not present .
    PCollectionTuple streamTrackCollection =
        streamTrackGbk
            .apply(
                ParDo.of(new ConvertCbkResultToStreamTrack(streamsTag, tracksTag, errorRecordTag))
                    .withOutputTags(streamTrackKVTag, TupleTagList.of(errorRecordTag)));

    //Join StreamTrack Pojo with Users POJO on User_id
    PCollection<KV<String, CoGbkResult>> streamsDenormCbk =
        KeyedPCollectionTuple
            .of(streamTrackTag,
                streamTrackCollection.get(streamTrackKVTag).setCoder(
                    KvCoder.of(StringUtf8Coder.of(), SerializableCoder.of(StreamTrack.class))))
            .and(usersTag,
                users.get(usersKVTag)
                    .setCoder(KvCoder.of(StringUtf8Coder.of(), SerializableCoder.of(Users.class))))
            .apply(CoGroupByKey.create());

    //Convert CoGroupByKey Results from above transform into Denorm records.
    streamsDenormCbk.apply(ParDo.of(new ConvertCbkResultToStreamsDenorm(streamTrackTag, usersTag)))
        .apply(TextIO.write().to("gs://" + options.getOutPutBucketName() + "/streams_denorm")
            .withSuffix(".json"));
    //Collect all the records with Json parase syntax errors and missing primary key 
    PCollection<ErrorRecord> errorRecordList = PCollectionList
        .of(streams.get(errorRecordTag).setCoder(SerializableCoder.of(ErrorRecord.class)))
        .and(tracks.get(errorRecordTag).setCoder(SerializableCoder.of(ErrorRecord.class)))
        .and(users.get(errorRecordTag).setCoder(SerializableCoder.of(ErrorRecord.class)))
        .and(streamTrackCollection.get(errorRecordTag)
            .setCoder(SerializableCoder.of(ErrorRecord.class)))
        .apply(Flatten.<ErrorRecord>pCollections());//Combine into one pcollection of ErrorRecords

    // Conver the Pcollection of ErrorRecords POJO to String.
    errorRecordList.apply(MapElements.via(new SimpleFunction<ErrorRecord, String>() {

      /**
       * 
       */
      private static final long serialVersionUID = 15454154354L;

      @Override
      public String apply(ErrorRecord eRecord) {
        return eRecord.toString();
      }
    })).apply(TextIO.write().to("gs://" + options.getOutPutBucketName() + "/error/error.json"));//Write the data to Error File. 

    denormPipeline.run();

  }

  /**
   * Method to check if file exists in GCS .
   * @param bucketName
   * @param blobName
   * @throws FileNotFoundException
   */
  private static void checkFileExist(String bucketName, String blobName)
      throws FileNotFoundException {

    BlobId blobId = BlobId.of(bucketName, blobName);
    Storage storage = StorageOptions.getDefaultInstance().getService();
    if (storage.get(blobId) == null) {
      throw new FileNotFoundException("No file is present in " + blobName);

    }

  }



}
