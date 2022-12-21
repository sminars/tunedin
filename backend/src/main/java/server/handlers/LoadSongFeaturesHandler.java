package server.handlers;

import com.squareup.moshi.Moshi;
import database.UserDatabase;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import org.apache.hc.core5.http.ParseException;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import song.Song;
import spark.Request;
import spark.Response;
import spark.Route;
import user.User;

/** Handler for load-song-features endpoing */
public class LoadSongFeaturesHandler implements Route {

  private UserDatabase database;

  /**
   * Constructor
   *
   * @param database that stores users
   */
  public LoadSongFeaturesHandler(UserDatabase database) {
    this.database = database;
  }

  /**
   * Method that handles the GET request and outputs a serialized response. Retrieves each user's
   * most recent song and then updates the user object.
   *
   * @param request - the request to handle
   * @param response - the response to modify
   * @return A serialized success response or error response
   */
  @Override
  public Object handle(Request request, Response response)
      throws ExecutionException, InterruptedException, ParseException, SpotifyWebApiException,
          IOException {

    List<String> userIds = this.database.getAllUserIds();

    for (String userId : userIds) {
      System.out.println("..........USER UPDATE BEGUN..........");
      System.out.println("userId: " + userId);
      User user = this.database.getUser(userId);
      System.out.println("Pre-update: " + user);
      System.out.println("displayName: " + user.getDisplayName());
      // get new song
      Song newSong = user.getMostRecentSong();
      System.out.println("Song Title: " + newSong.getTitle());
      System.out.println("Artists: " + newSong.getArtists());
      user.setCurrentSong(newSong);
      // add new song to averaged historical song point
      user.updateHistoricalSongPoint(newSong.getPoint());
      // update document in firebase to reflect new user info
      this.database.updateUser(userId, user);
      System.out.println("Post-update: " + user);
      System.out.println("..........USER UPDATE FINISHED..........");
    }
    return new LoadSongFeaturesSuccessResponse(userIds).serialize();
  }

  /**
   * Response object to send with User object
   *
   * @param result - success message
   * @param updatedUsers - list of user ids corresponding to users that were updated in the database
   */
  public record LoadSongFeaturesSuccessResponse(String result, List<String> updatedUsers) {

    public LoadSongFeaturesSuccessResponse(List<String> updatedUsers) {
      this("success", updatedUsers);
    }

    /**
     * Serializes this response object ot a Json String
     *
     * @return this response, serialized as Json
     */
    String serialize() {
      try {
        Moshi moshi = new Moshi.Builder().build();
        return moshi.adapter(LoadSongFeaturesSuccessResponse.class).toJson(this);
      } catch (Exception e) {
        e.printStackTrace();
        throw e;
      }
    }
  }
}
