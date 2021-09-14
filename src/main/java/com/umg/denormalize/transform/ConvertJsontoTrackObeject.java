package com.umg.denormalize.transform;

import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.TupleTag;
import com.aug.denormalize.model.ErrorRecord;
import com.aug.denormalize.model.Tracks;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
/**
 * Convert Tracks json to Tracks POJO
 * Creates Error Record if track_id is missing or json conversion exception occurs .
 * @author jjaladi
 *
 */
public class ConvertJsontoTrackObeject extends DoFn<String, KV<String, Tracks>> {

  /**
  * 
  */
  private static final long serialVersionUID = -8073913381876607585L;
  private transient Gson gson;
  private TupleTag<ErrorRecord> errTag;
  
  public ConvertJsontoTrackObeject(TupleTag<ErrorRecord> errTag) {
    this.errTag = errTag;
  }

  @Setup
  public void setUp() {
    gson = new Gson(); //Setup gson object one per instance.
  }

  @ProcessElement
  public void processElement(ProcessContext c) {
    try {
      Tracks tracks = gson.fromJson(c.element().toString(), Tracks.class);
      if (tracks.getTrack_id() != null) { //Check if Track id is not null
        c.output(KV.of(tracks.getTrack_id(), tracks));
      } else {
        c.output(errTag,
            new ErrorRecord("Tracks.gz", c.element(), "Track_id is missing in the Json")); //Create Error record if Track_id is missing.
      }
    } catch (JsonSyntaxException ex) {
      c.output(errTag, new ErrorRecord("Tracks.gz", c.element(), ex.getMessage())); //Create Error record if json conversion exception occur.
    } 
  }
}
