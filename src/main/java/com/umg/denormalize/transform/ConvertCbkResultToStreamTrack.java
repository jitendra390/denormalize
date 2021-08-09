package com.umg.denormalize.transform;

import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.join.CoGbkResult;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.TupleTag;
import com.aug.denormalize.model.StreamTrack;
import com.aug.denormalize.model.Streamz;
import com.aug.denormalize.model.Tracks;

public class ConvertCbkResultToStreamTrack
    extends DoFn<KV<String, CoGbkResult>, KV<String, StreamTrack>> {

  /**
   * 
   */
  private static final long serialVersionUID = 7585329052923943988L;
  private TupleTag<Streamz> streamsTag;
  private TupleTag<Tracks> tracksTag;

  public ConvertCbkResultToStreamTrack(TupleTag<Streamz> streamsTag, TupleTag<Tracks> tracksTag) {
    this.streamsTag = streamsTag;
    this.tracksTag = tracksTag;
  }


  @ProcessElement
  public void processElement(ProcessContext ctx) {

    CoGbkResult gbkResult = ctx.element().getValue();
    
    Iterable<Streamz> stream = gbkResult.getAll(streamsTag);
    Iterable<Tracks> track  = gbkResult.getAll(tracksTag);
    
    if(stream.iterator().hasNext() && track.iterator().hasNext())
    {
      Streamz streamz = stream.iterator().next();
      Tracks tracks = track.iterator().next();
      StreamTrack streamTrack = new StreamTrack(streamz.getUser_id(), streamz.getCached(),
          streamz.getTimestamp(), streamz.getSource_uri(), streamz.getTrack_id(),
          streamz.getSource(), streamz.getLength(), streamz.getVersion(), streamz.getDevice_type(),
          streamz.getMessage(), streamz.getOs(), streamz.getStream_country(),
          streamz.getReport_date(), tracks.getIsrc(), tracks.getAlbum_code());
      ctx.output(KV.of(streamz.getUser_id(), streamTrack));
    } else if (stream.iterator().hasNext()) {
      Streamz streamz = stream.iterator().next();
      StreamTrack streamTrack = new StreamTrack(streamz.getUser_id(), streamz.getCached(),
          streamz.getTimestamp(), streamz.getSource_uri(), streamz.getTrack_id(),
          streamz.getSource(), streamz.getLength(), streamz.getVersion(), streamz.getDevice_type(),
          streamz.getMessage(), streamz.getOs(), streamz.getStream_country(),
          streamz.getReport_date());
      ctx.output(KV.of(streamz.getUser_id(), streamTrack));
    }
  }
}
