package com.umg.denormalize.transform;

import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.join.CoGbkResult;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.TupleTag;
import com.aug.denormalize.model.ErrorRecord;
import com.aug.denormalize.model.StreamTrack;
import com.aug.denormalize.model.Streamz;
import com.aug.denormalize.model.Tracks;
import com.google.gson.Gson;

/**
 * Class for converting CoGroupByKey of Streams and Tracks into StreamTracks POJO This POJO will be
 * joined to User POJO
 * 
 * @author jjaladi
 *
 */
public class ConvertCbkResultToStreamTrack
    extends DoFn<KV<String, CoGbkResult>, KV<String, StreamTrack>> {

  /**
   * 
   */
  private static final long serialVersionUID = 7585329052923943988L;
  private TupleTag<Streamz> streamsTag;
  private TupleTag<Tracks> tracksTag;
  private TupleTag<ErrorRecord> errTag;
  private Gson gson;

  public ConvertCbkResultToStreamTrack(TupleTag<Streamz> streamsTag, TupleTag<Tracks> tracksTag,
      TupleTag<ErrorRecord> errTag) {
    this.streamsTag = streamsTag;
    this.tracksTag = tracksTag;
    this.errTag = errTag;
  }



  @ProcessElement
  public void processElement(ProcessContext ctx) {

    CoGbkResult gbkResult = ctx.element().getValue();
    
    Iterable<Streamz> stream = gbkResult.getAll(streamsTag);
    Iterable<Tracks> track  = gbkResult.getAll(tracksTag);
    // Check if both Steams and Tracks has a match
    if(stream.iterator().hasNext() && track.iterator().hasNext())
    {
      Streamz streamz = stream.iterator().next();
      Tracks tracks = track.iterator().next();
      if (streamz.getUser_id() != null) { // Create StreamTrack object with data from both Streams
                                          // and Tracks
      StreamTrack streamTrack = new StreamTrack(streamz.getUser_id(), streamz.getCached(),
          streamz.getTimestamp(), streamz.getSource_uri(), streamz.getTrack_id(),
          streamz.getSource(), streamz.getLength(), streamz.getVersion(), streamz.getDevice_type(),
          streamz.getMessage(), streamz.getOs(), streamz.getStream_country(),
          streamz.getReport_date(), tracks.getIsrc(), tracks.getAlbum_code());
      ctx.output(KV.of(streamz.getUser_id(), streamTrack));
    } else {
      sendErrorMsg(ctx, streamz); // Create Error record if streams is missing value for User_id
    }

    } else if (stream.iterator().hasNext()) {
      Streamz streamz = stream.iterator().next();
      if (streamz.getUser_id() != null) { // Create Streamtrack with data from Streams only.
      StreamTrack streamTrack = new StreamTrack(streamz.getUser_id(), streamz.getCached(),
          streamz.getTimestamp(), streamz.getSource_uri(), streamz.getTrack_id(),
          streamz.getSource(), streamz.getLength(), streamz.getVersion(), streamz.getDevice_type(),
          streamz.getMessage(), streamz.getOs(), streamz.getStream_country(),
          streamz.getReport_date());
        ctx.output(KV.of(streamz.getUser_id(), streamTrack));
    }
    else {
      sendErrorMsg(ctx, streamz);// Create Error record if streams is missing value for User_id
    }
  }
}

// Method to create ErrorRecord and output the record to errorTag
private void sendErrorMsg(ProcessContext ctx,
    Streamz streamz) {
  ctx.output(errTag, new ErrorRecord("While joining Streams with Tracks", gson.toJson(streamz),
      "User_id is missing while making StreamTrack record"));

  }
}
