package com.umg.denormalize.transform;

import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.values.KV;
import com.aug.denormalize.model.Streamz;
import com.google.gson.Gson;

public class ConvertJsontoStreamObeject extends DoFn<String, KV<String, Streamz>> {

  /**
   * 
   */
  private static final long serialVersionUID = -1568892176161684722L;
  private transient Gson gson;

  public ConvertJsontoStreamObeject() {

  }

  @Setup
  public void setUp() {
    gson = new Gson();
  }

  @ProcessElement
  public void processElement(ProcessContext c) {
    Streamz strm = gson.fromJson(c.element().toString(), Streamz.class);
    c.output(KV.of(strm.getTrack_id(), strm));
  }
}
