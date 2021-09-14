package com.umg.denormalize.transform;

import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.TupleTag;
import com.aug.denormalize.model.ErrorRecord;
import com.aug.denormalize.model.Streamz;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

/**
 * Class to Convert Stream json to Stream Object Creates Error record with content of the element if
 * any error occcurs during json parsing or no track_id is present.
 * 
 * @author jjaladi
 *
 */
public class ConvertJsontoStreamObeject extends DoFn<String, KV<String, Streamz>> {

  /**
   * 
   */
  private static final long serialVersionUID = -1568892176161684722L;
  private transient Gson gson;
  private TupleTag<ErrorRecord> errTag;
  //Constructor to initiate error tag
  public ConvertJsontoStreamObeject(TupleTag<ErrorRecord> errTag) {
    this.errTag = errTag;
  }

  @Setup
  public void setUp() {
    gson = new Gson(); //One Gson object per instance.
  }

  @ProcessElement
  public void processElement(ProcessContext c) {
    try {
    Streamz strm = gson.fromJson(c.element().toString(), Streamz.class); //Convert streams json to streamz pojo.
    if (strm.getTrack_id() != null) { // Check if track_id is available
    c.output(KV.of(strm.getTrack_id(), strm));
    } else {
      c.output(errTag,
          new ErrorRecord("streams.gz", c.element(), "track_id is missing in the json")); //create Error record if Track_id is missing.
    }
  } catch (JsonSyntaxException ex) {
    c.output(errTag, new ErrorRecord("streams.gz", c.element(), ex.getMessage())); //Create Error record if an exception occures during json conersion 
  }
  }
}
