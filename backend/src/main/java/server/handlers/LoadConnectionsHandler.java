package server.handlers;

import com.squareup.moshi.Moshi;
import database.UserDatabase;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import kdtree.KdTree;
import server.ErrBadJsonResponse;
import song.Song;
import spark.Request;
import spark.Response;
import spark.Route;
import user.User;

/** Handler for the load-connections endpoint */
public class LoadConnectionsHandler implements Route {

  private UserDatabase database;

  /**
   * Constructor for handler that takes in userDatabase to access nodes for kd-tree
   *
   * @param database - the database housing users to build tree
   */
  public LoadConnectionsHandler(UserDatabase database) {
    this.database = database;
  }

  /**
   * Method that handles the GET request and outputs a serialized response. Calculates each user's
   * connections and historical connections using the k-tree and then updates the user object.
   *
   * @param request - the request to handle
   * @param response - the response to modify
   * @return A serialized success response or error response
   */
  @Override
  public Object handle(Request request, Response response) {
    try {
      // build kd trees for finding nearest neighbors
      System.out.println("Constructing user and song trees...");
      List<User> userNodes = new ArrayList<>();
      List<Song> songNodes = new ArrayList<>();
      List<String> userIds = this.database.getAllUserIds();
      for (String userId : userIds) {
        User user = this.database.getUser(userId);
        userNodes.add(user);
        songNodes.add(user.getCurrentSong());
      }
      System.out.println("User count: " + userNodes.size());
      System.out.println("Song count: " + songNodes.size());
      KdTree<User> userTree = new KdTree<User>(userNodes, 0);
      System.out.println("User Tree built.");
      KdTree<Song> songTree = new KdTree<Song>(songNodes, 0);
      System.out.println("Song Tree built.");
      for (User user : userNodes) {
        // create new user object so user in tree does not get modified
        System.out.println("..........BEGINNING CONNECTIONS..........");
        User newUser = this.database.getUser(user.getUserId());
        System.out.println("userId: " + user.getDisplayName());
        System.out.println("displayName: " + user.getDisplayName());
        String[] connections = user.findConnections(songTree);
        System.out.println("Today's Connections: " + Arrays.asList(connections));
        user.setConnections(connections);
        String[] historicalConnections = user.findHistoricalConnections(userTree);
        System.out.println("All Time Connections: " + Arrays.asList(historicalConnections));
        user.setHistoricalConnections(historicalConnections);
        // update database using new user info
        this.database.updateUser(user.getUserId(), user);
        System.out.println("..........CONNECTIONS UPDATED..........");
      }
      return new LoadConnectionsSuccessResponse().serialize();
    } catch (Exception e) {
      e.printStackTrace();
      return new ErrBadJsonResponse().serialize();
    }
  }

  /**
   * Response object to send with User object
   *
   * @param result - success message
   */
  public record LoadConnectionsSuccessResponse(String result) {

    public LoadConnectionsSuccessResponse() {
      this("success");
    }

    /**
     * Serializes this response object ot a Json String
     *
     * @return this response, serialized as Json
     */
    String serialize() {
      try {
        Moshi moshi = new Moshi.Builder().build();
        return moshi.adapter(LoadConnectionsSuccessResponse.class).toJson(this);
      } catch (Exception e) {
        e.printStackTrace();
        throw e;
      }
    }
  }
}
