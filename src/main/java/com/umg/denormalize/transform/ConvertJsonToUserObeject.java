package com.umg.denormalize.transform;

import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.values.KV;
import com.aug.denormalize.model.Users;
import com.google.gson.Gson;

public class ConvertJsonToUserObeject extends DoFn<String, KV<String, Users>> {

  /**
  * 
  */
  private static final long serialVersionUID = -8073913381876607235L;
  private transient Gson gson;
	public ConvertJsonToUserObeject() {
		
	}
	
    @Setup
    public void setUp() {
      gson = new Gson();
    }

	@ProcessElement
	public void processElement(ProcessContext c) {
      Users users = gson.fromJson(c.element(), Users.class);
      c.output(KV.of(users.getUser_id(), users));
	}



}
