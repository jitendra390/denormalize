package com.umg.denormalize.transform;

import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.values.KV;
import com.aug.denormalize.model.Tracks;
import com.google.gson.Gson;

public class ConvertJsontoTrackObeject extends DoFn<String, KV<String,Tracks>> {

  /**
  * 
  */
  private static final long serialVersionUID = -8073913381876607585L;
  private transient Gson gson;

	public ConvertJsontoTrackObeject() {
		
	}
	
    @Setup
    public void setUp() {
      gson = new Gson();
    }
	
	@ProcessElement
	public void processElement(ProcessContext c) {
		
      Tracks tracks = gson.fromJson(c.element().toString(), Tracks.class);
		
        c.output(KV.of(tracks.getTrack_id(), tracks));
	}
}
