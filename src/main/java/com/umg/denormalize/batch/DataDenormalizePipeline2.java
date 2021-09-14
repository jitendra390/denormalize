package com.umg.denormalize.batch;

import java.io.Serializable;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.coders.Coder;
import org.apache.beam.sdk.coders.KvCoder;
import org.apache.beam.sdk.coders.SerializableCoder;
import org.apache.beam.sdk.coders.StringUtf8Coder;
import org.apache.beam.sdk.io.TextIO;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.TupleTag;
import com.aug.denormalize.model.StreamTrack;
import com.aug.denormalize.model.Streamz;
import com.aug.denormalize.model.Tracks;
import com.aug.denormalize.model.Users;
import com.umg.denormalize.options.DataDenormalizeOptions;
import com.umg.denormalize.transform.JsonMapper;

public class DataDenormalizePipeline2 {

  final static TupleTag<Streamz> streamsTag = new TupleTag<>();
  final static TupleTag<Tracks> tracksTag = new TupleTag<>();
  final static TupleTag<Users> usersTag = new TupleTag<>();
  final static TupleTag<StreamTrack> streamTrackTag = new TupleTag<>();

	public static void main(String[] args) {
		
		DataDenormalizeOptions denormOptions = PipelineOptionsFactory.fromArgs(args).as(DataDenormalizeOptions.class);
		
		runPipeLine(denormOptions);
	}
	
	
	public static void runPipeLine(DataDenormalizeOptions options) {
		
		Pipeline denormPipeline = Pipeline.create(options);
		


        /*
         * PCollection<KV<String, Streamz>> streams = denormPipeline
         * .apply(TextIO.read().from("gs://" + options.getLandingBucketName() + "/streamz.gz"))
         * .apply(ParDo.of(new ConvertJsontoStreamObeject()))
         * .setCoder(KvCoder.of(StringUtf8Coder.of(), SerializableCoder.of(Streamz.class)));
         * 
         * PCollection<KV<String, Tracks>> tracks = denormPipeline .apply(TextIO.read().from("gs://"
         * + options.getLandingBucketName() + "/tracks.gz")) .apply(ParDo.of(new
         * ConvertJsontoTrackObeject())) .setCoder(KvCoder.of(StringUtf8Coder.of(),
         * SerializableCoder.of(Tracks.class)));
         * 
         * PCollection<KV<String, Users>> users = denormPipeline .apply(TextIO.read().from("gs://" +
         * options.getLandingBucketName() + "/users.gz")) .apply(ParDo.of(new
         * ConvertJsonToUserObeject())) .setCoder(KvCoder.of(StringUtf8Coder.of(),
         * SerializableCoder.of(Users.class)));;
         * 
         * 
         * JsonMapper<Users> userMapper = new
         * JsonMapper<>("com.aug.denormalize.model.Users","userId");
         * 
         * PCollection<KV<String, Users>> users2 = denormPipeline .apply(TextIO.read().from("gs://"
         * + options.getLandingBucketName() + "/users.gz")) .apply(ParDo.of(userMapper))
         * .setCoder(KvCoder.of(StringUtf8Coder.of(), SerializableCoder.of(Users.class)));;
         * 
         * 
         * PCollection<KV<String, Users>> users3 = getStream(options, denormPipeline, Users.class,
         * "com.aug.denormalize.model.Users", "userId", "/users.gz");
         * 
         * 
         * PCollection<KV<String,CoGbkResult>> streamTrackGbk = KeyedPCollectionTuple
         * .of(streamsTag, streams).and(tracksTag,tracks) .apply(CoGroupByKey.create());
         * 
         * PCollection<KV<String, StreamTrack>> streamTrackCollection = streamTrackGbk
         * .apply(ParDo.of(new ConvertCbkResultToStreamTrack(streamsTag, tracksTag)))
         * .setCoder(KvCoder.of(StringUtf8Coder.of(), SerializableCoder.of(StreamTrack.class)));
         * 
         * PCollection<KV<String, CoGbkResult>> streamsDenormCbk =
         * KeyedPCollectionTuple.of(streamTrackTag, streamTrackCollection).and(usersTag, users)
         * .apply(CoGroupByKey.create());
         * 
         * streamsDenormCbk .apply(ParDo.of(new ConvertCbkResultToStreamsDenorm(streamTrackTag,
         * usersTag))) .apply(TextIO.write().to("gs://" + options.getOutPutBucketName() +
         * "/streams_denorm") .withSuffix(".json"))
         * 
         * ;
         * 
         * 
         * 
         * denormPipeline.run();
         */
	}


private static <T extends Serializable> PCollection<KV<String, T>> getStream(
    DataDenormalizeOptions options,
    Pipeline denormPipeline, Class<T> classOfT, String className, String fieldName, String gzFile) {
  JsonMapper<T> mapper = new JsonMapper<>(className, fieldName);
  Coder<KV<String, T>> coder = KvCoder.of(StringUtf8Coder.of(), SerializableCoder.of(classOfT));
    PCollection<KV<String, T>> streams = denormPipeline
        .apply(TextIO.read().from("gs://" + options.getLandingBucketName() + gzFile))
        .apply(ParDo.of(mapper))
      .setCoder(coder);
    return streams;
  }


}
