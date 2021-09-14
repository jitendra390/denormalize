package com.umg.denormalize.transform;

import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.TupleTag;
import com.aug.denormalize.model.ErrorRecord;
import com.aug.denormalize.model.Users;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class ConvertJsonToUserObeject extends DoFn<String, KV<String, Users>> {

  /**
  * 
  */
  private static final long serialVersionUID = -8073913381876607235L;
  private transient Gson gson;
  private TupleTag<ErrorRecord> errTag;

  public ConvertJsonToUserObeject(TupleTag<ErrorRecord> errTag) {
    this.errTag = errTag;
  }

  @Setup
  public void setUp() {
    gson = new Gson(); //Setup gson object on per DoFn instance 
  }

  @ProcessElement
  public void processElement(ProcessContext c) {
    try {
      Users users = gson.fromJson(c.element(), Users.class);
      if (users.getUser_id() != null) { //Check if user_id is not null
        c.output(KV.of(users.getUser_id(), users));
      } else {
        c.output(errTag,
            new ErrorRecord("Users.gz", c.element(), "user_id is missing in the json")); //Create Error record if user_id is missing.
      }
    } catch (JsonSyntaxException ex) {
      c.output(this.errTag, new ErrorRecord("Users.gz", c.element(), ex.getMessage())); //Create Error record if Json conversion exception occur.
    } 
  }



}
