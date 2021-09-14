package com.umg.denormalize.transform;

import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.join.CoGbkResult;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.TupleTag;
import com.aug.denormalize.model.StreamTrack;
import com.aug.denormalize.model.StreamsDenorm;
import com.aug.denormalize.model.Users;
import com.google.gson.Gson;

/**
 * Class to convert Data from StreamTrack POJO and Users POJO to StreamzDenorm POJO.
 * 
 * @author jjaladi
 *
 */
public class ConvertCbkResultToStreamsDenorm
    extends DoFn<KV<String, CoGbkResult>, String> {

  /**
   * 
   */
  private static final long serialVersionUID = -859085521019595992L;
  private TupleTag<StreamTrack> streamsTrackTag;
  private TupleTag<Users> usersTag;
  private transient Gson gson;

  public ConvertCbkResultToStreamsDenorm(TupleTag<StreamTrack> streamsTrackTag,
      TupleTag<Users> usersTag) {
    this.streamsTrackTag = streamsTrackTag;
    this.usersTag = usersTag;
  }

  @Setup
  public void setUp() {
    gson = new Gson();// Initiage On gson object per one thread.
  }

  @ProcessElement
  public void processElement(ProcessContext ctx) {

    CoGbkResult gbkResult = ctx.element().getValue();

    Iterable<StreamTrack> streamTrackIter = gbkResult.getAll(streamsTrackTag);
    Iterable<Users> usersIter = gbkResult.getAll(usersTag);

    // Check if the CogroupbyKey result has values for both left and right side of the groupby
    if (streamTrackIter.iterator().hasNext() && usersIter.iterator().hasNext()) {
      StreamTrack streamTrack = streamTrackIter.iterator().next();
      Users users = usersIter.iterator().next();
      StreamsDenorm streamsDenorm = // Create StreamDenorm object with data from all three files.
          new StreamsDenorm(streamTrack.getUser_id(), streamTrack.getCached(),
              streamTrack.getTimestamp(), streamTrack.getSource_uri(), streamTrack.getTrack_id(),
              streamTrack.getSource(), streamTrack.getLength(), streamTrack.getVersion(),
              streamTrack.getDevice_type(), streamTrack.getMessage(), streamTrack.getOs(),
              streamTrack.getStream_country(), streamTrack.getReport_date(), streamTrack.getIsrc(),
              streamTrack.getAlbum_code(), users.getProduct(), users.getCountry(),
              users.getRegion(), users.getZip_code(), users.getAccess(), users.getGender(),
              users.getPartner(), users.getReferral(), users.getType(), users.getBirth_year());
      ctx.output(gson.toJson(streamsDenorm));//
    } else if (streamTrackIter.iterator().hasNext()) { // No match found on users file.
      StreamTrack streamTrack = streamTrackIter.iterator().next();
      StreamsDenorm streamsDenorm = // Create denorm record with only data from Streams and Tracks
                                    // and data from users as null.
          new StreamsDenorm(streamTrack.getUser_id(), streamTrack.getCached(),
          streamTrack.getTimestamp(), streamTrack.getSource_uri(), streamTrack.getTrack_id(),
          streamTrack.getSource(), streamTrack.getLength(), streamTrack.getVersion(),
          streamTrack.getDevice_type(), streamTrack.getMessage(), streamTrack.getOs(),
              streamTrack.getStream_country(), streamTrack.getReport_date(), streamTrack.getIsrc(),
              streamTrack.getAlbum_code());
      ctx.output(gson.toJson(streamsDenorm));
    } else { // no Match found on StreamTracks side.
      Users users = usersIter.iterator().next();
      StreamsDenorm streamsDenorm = // Create Denorm record wth only data from Users file and null
                                    // for other two files.
          new StreamsDenorm(users.getUser_id(), users.getProduct(), users.getCountry(),
              users.getRegion(), users.getZip_code(), users.getAccess(), users.getGender(),
              users.getPartner(), users.getReferral(), users.getType(), users.getBirth_year());
      ctx.output(gson.toJson(streamsDenorm));
    }
  }

}
