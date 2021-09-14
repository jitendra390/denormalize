package com.aug.denormalize.transform;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.coders.SerializableCoder;
import org.apache.beam.sdk.testing.PAssert;
import org.apache.beam.sdk.transforms.Create;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.transforms.SerializableFunction;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.PCollectionTuple;
import org.apache.beam.sdk.values.TupleTag;
import org.apache.beam.sdk.values.TupleTagList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import com.aug.denormalize.model.ErrorRecord;
import com.aug.denormalize.model.Users;
import com.google.gson.Gson;
import com.umg.denormalize.transform.ConvertJsonToUserObeject;
import junit.framework.Assert;

@RunWith(JUnit4.class)
public class TestConvertJsonToUserObject implements Serializable {
  Users users = new Users();
  private transient Gson gson ;

  @Test
  public void convertJsonToUserObjectTest() {

    Pipeline p = Pipeline.create();
    gson = new Gson();


    String userJson = getJsonFromFile("src/test/resources/users.json");
    users = gson.fromJson(userJson, Users.class);
    List<String> userList = new ArrayList<String>();
    userList.add(userJson);
    TupleTag<ErrorRecord> errorRecordTag = new TupleTag<>();
    TupleTag<KV<String, Users>> usersKVTag = new TupleTag<>();
    PCollection<String> userCollection = p.apply(Create.of(userList));
    PCollectionTuple userKV =
        userCollection.apply(ParDo.of(new ConvertJsonToUserObeject(errorRecordTag))
            .withOutputTags(usersKVTag, TupleTagList.of(errorRecordTag)));

    userKV.get(errorRecordTag).setCoder(SerializableCoder.of(ErrorRecord.class));
    PAssert.that(userKV.get(usersKVTag))
        .satisfies((SerializableFunction<Iterable<KV<String, Users>>, Void>) userkv -> {

          for (KV<String, Users> kvuser : userkv) {
            Assert.assertEquals(kvuser.getKey(), users.getUser_id());
          }
          return null;
        }

        );


    p.run();
  }


  public String getJsonFromFile(String filePath) {
    String json = null;

    try {
      File myObj = new File(filePath);
      Scanner myReader = new Scanner(myObj);
      while (myReader.hasNextLine()) {
        json = myReader.nextLine();
      }
      myReader.close();
      return json;
    } catch (FileNotFoundException e) {
      e.printStackTrace();
      return null;
    }
  }
}
