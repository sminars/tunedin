package server;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static server.Constants.MOCK_SONGS_FILEPATH;
import static server.Constants.MOCK_USERS_FILEPATH;

import com.squareup.moshi.Moshi;
import com.squareup.moshi.Moshi.Builder;
import csv.CSVParser;
import csv.FactoryFailureException;
import database.LocalDatabase;
import java.io.FileReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import okio.Buffer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.handlers.GetUserHandler;
import server.handlers.GetUserHandler.GetUserSuccessResponse;
import server.handlers.GetUserIdsHandler;
import server.handlers.GetUserIdsHandler.GetUserIdsSuccessResponse;
import server.handlers.LoadConnectionsHandler;
import server.handlers.LoadConnectionsHandler.LoadConnectionsSuccessResponse;
import server.handlers.LoadSongFeaturesHandler;
import server.handlers.LoadSongFeaturesHandler.LoadSongFeaturesSuccessResponse;
import song.Song;
import song.SongFactory;
import song.SongLibrary;
import spark.Spark;
import user.User;
import user.UserFactory;

public class HandlersTest {

  private final LocalDatabase database = this.createLocalDatabase();

  private LocalDatabase createLocalDatabase() {
    try {
      CSVParser<Song> songCSVParser =
          new CSVParser<>(new FileReader(MOCK_SONGS_FILEPATH), new SongFactory());
      SongLibrary songLibrary = new SongLibrary(songCSVParser);

      CSVParser<User> userCSVParser =
          new CSVParser<>(new FileReader(MOCK_USERS_FILEPATH), new UserFactory(songLibrary));

      return new LocalDatabase(userCSVParser.getParsedData());
    } catch (IOException | FactoryFailureException e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }

  @BeforeAll
  public static void spark_port_setup() {
    Spark.port(0);
    Logger.getLogger("").setLevel(Level.WARNING);
  }

  @BeforeEach
  public void setup() {
    Spark.get("/load-song-features", new LoadSongFeaturesHandler(this.database));
    Spark.get("/load-connections", new LoadConnectionsHandler(this.database));
    Spark.get("/get-user", new GetUserHandler(this.database));
    Spark.get("/get-all-user-ids", new GetUserIdsHandler(this.database));
    Spark.init();
    Spark.awaitInitialization(); // don't continue until the server is listening
  }

  @AfterEach
  public void teardown() {
    Spark.unmap("/load-song-features");
    Spark.unmap("/load-connections");
    Spark.unmap("/get-user");
    Spark.unmap("/get-all-user-ids");
    Spark.awaitStop(); // don't continue until the server is stopped
  }

  /** Integration Test: Mocking the API calls that occur when we load our frontend web-app */
  @Test
  public void integrationTest() throws IOException {

    User initUser = this.database.getUser("y9zwQW9ptlPURO6XtVoG");

    URL requestLoadSongFeatures =
        new URL("http://localhost:" + Spark.port() + "/load-song-features");
    HttpURLConnection connectLoadSongFeatures =
        (HttpURLConnection) requestLoadSongFeatures.openConnection();
    assertEquals(200, connectLoadSongFeatures.getResponseCode());
    connectLoadSongFeatures.connect();
    connectLoadSongFeatures.disconnect();

    URL requestLoadConnections = new URL("http://localhost:" + Spark.port() + "/load-connections");
    HttpURLConnection connectLoadConnections =
        (HttpURLConnection) requestLoadConnections.openConnection();
    connectLoadConnections.connect();
    assertEquals(200, connectLoadConnections.getResponseCode());
    connectLoadConnections.disconnect();

    URL requestUser =
        new URL("http://localhost:" + Spark.port() + "/get-user?id=y9zwQW9ptlPURO6XtVoG");
    HttpURLConnection clientConnection = (HttpURLConnection) requestUser.openConnection();
    clientConnection.connect();
    assertEquals(200, clientConnection.getResponseCode());
    Moshi moshi = new Builder().build();
    GetUserSuccessResponse actualResponse =
        moshi
            .adapter(GetUserSuccessResponse.class)
            .fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

    User updatedUser = actualResponse.user();

    // First we check that the user ids are the same to ensure we are getting the same User object
    assertEquals(initUser.getUserId(), updatedUser.getUserId());

    // Now we check that Song, Connections, and Historical Connections have been updated by our API
    // calls
    assertNotEquals(initUser.getCurrentSong(), updatedUser.getCurrentSong());
    assertNotEquals(initUser.getConnections(), updatedUser.getConnections());
    assertNotEquals(initUser.getHistoricalConnections(), updatedUser.getHistoricalConnections());

    connectLoadConnections.disconnect();
  }

  /**
   * Testing that providing get-user request with a valid userId return the correct success response
   *
   * @throws IOException
   */
  @Test
  public void testGetUser() throws IOException {
    URL requestURL =
        new URL("http://localhost:" + Spark.port() + "/get-user?id=TJWATWNITXLQCVcSroz1");
    HttpURLConnection clientConnection = (HttpURLConnection) requestURL.openConnection();
    clientConnection.connect();
    assertEquals(200, clientConnection.getResponseCode());
    Moshi moshi = new Builder().build();
    GetUserSuccessResponse actualResponse =
        moshi
            .adapter(GetUserSuccessResponse.class)
            .fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    GetUserSuccessResponse mockResponse =
        new GetUserSuccessResponse("success", this.database.getUser("TJWATWNITXLQCVcSroz1"));
    assertEquals(mockResponse.user().getUserId(), actualResponse.user().getUserId());
    assertEquals(mockResponse.user().getDisplayName(), actualResponse.user().getDisplayName());
    assertEquals(mockResponse.user().getRefreshToken(), actualResponse.user().getRefreshToken());
    assertEquals(mockResponse.user().getCurrentSong(), actualResponse.user().getCurrentSong());
    assertEquals(
        mockResponse.user().getMembershipLength(), actualResponse.user().getMembershipLength());
    assertEquals(
        Arrays.toString(mockResponse.user().getConnections()),
        Arrays.toString(actualResponse.user().getConnections()));
    assertEquals(
        Arrays.toString(mockResponse.user().getHistoricalConnections()),
        Arrays.toString(actualResponse.user().getHistoricalConnections()));
    assertEquals(
        Arrays.toString(mockResponse.user().getHistoricalSongPoint()),
        Arrays.toString(actualResponse.user().getHistoricalSongPoint()));
    clientConnection.disconnect();
  }

  /**
   * Testing that providing a get-user request with an invalid userId returns an error response
   *
   * @throws IOException
   */
  @Test
  public void testInvalidGetUser() throws IOException {
    URL requestURL = new URL("http://localhost:" + Spark.port() + "/get-user?id=TJW");
    HttpURLConnection clientConnection = (HttpURLConnection) requestURL.openConnection();
    clientConnection.connect();
    assertEquals(200, clientConnection.getResponseCode());
    Moshi moshi = new Builder().build();
    ErrBadRequestResponse actualResponse =
        moshi
            .adapter(ErrBadRequestResponse.class)
            .fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    ErrBadRequestResponse mockResponse = new ErrBadRequestResponse();
    assertEquals(mockResponse.getClass(), actualResponse.getClass());
    clientConnection.disconnect();
  }

  /**
   * Testing that providing get-all-user-ids request will return the correct success response
   *
   * @throws IOException
   */
  @Test
  public void testGetAllUserIds() throws IOException {
    URL requestURL = new URL("http://localhost:" + Spark.port() + "/get-all-user-ids");
    HttpURLConnection clientConnection = (HttpURLConnection) requestURL.openConnection();
    clientConnection.connect();
    assertEquals(200, clientConnection.getResponseCode());
    Moshi moshi = new Builder().build();
    GetUserIdsSuccessResponse actualResponse =
        moshi
            .adapter(GetUserIdsSuccessResponse.class)
            .fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    GetUserIdsSuccessResponse mockResponse =
        new GetUserIdsSuccessResponse("success", this.database.getAllUserIds());
    assertEquals(mockResponse, actualResponse);
    clientConnection.disconnect();
  }

  /**
   * Testing that a load-song-features request returns the correct success response and updates the
   * current song of each user. We check if the original song (prior to calling load-song-features)
   * is different from the song updated by calling load-song-features.
   *
   * @throws IOException
   */
  @Test
  public void testLoadSongFeatures() throws IOException {
    List<Song> origSongs = new ArrayList<>();
    for (String id : this.database.getAllUserIds()) {
      origSongs.add(this.database.getUser(id).getCurrentSong());
    }

    URL requestURL = new URL("http://localhost:" + Spark.port() + "/load-song-features");
    HttpURLConnection clientConnection = (HttpURLConnection) requestURL.openConnection();
    clientConnection.connect();
    assertEquals(200, clientConnection.getResponseCode());
    Moshi moshi = new Builder().build();
    LoadSongFeaturesSuccessResponse actualResponse =
        moshi
            .adapter(LoadSongFeaturesSuccessResponse.class)
            .fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    LoadSongFeaturesSuccessResponse mockResponse =
        new LoadSongFeaturesSuccessResponse("success", this.database.getAllUserIds());
    assertEquals(mockResponse, actualResponse);

    List<Song> newSongs = new ArrayList<>();
    for (String id : this.database.getAllUserIds()) {
      newSongs.add(this.database.getUser(id).getCurrentSong());
    }

    for (int i = 0; i < this.database.getAllUserIds().size(); i++) {
      if (!origSongs.get(i).equals(newSongs.get(i))) {
        // With mock users, our random song generator can generate a song that user is already
        // listening too. This protects against the newly generated song being equal to the
        // previously generated one
        assertNotEquals(origSongs.get(i), newSongs.get(i));
      }
    }

    clientConnection.disconnect();
  }

  @Test
  public void testLoadConnections() throws IOException {
    List<String[]> origConnections = new ArrayList<>();
    List<String[]> origHistConnections = new ArrayList<>();
    for (String id : this.database.getAllUserIds()) {
      origConnections.add(this.database.getUser(id).getConnections());
      origHistConnections.add(this.database.getUser(id).getHistoricalConnections());
    }

    URL requestURL = new URL("http://localhost:" + Spark.port() + "/load-connections");
    HttpURLConnection clientConnection = (HttpURLConnection) requestURL.openConnection();
    clientConnection.connect();
    assertEquals(200, clientConnection.getResponseCode());
    Moshi moshi = new Builder().build();
    LoadConnectionsSuccessResponse actualResponse =
        moshi
            .adapter(LoadConnectionsSuccessResponse.class)
            .fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    LoadConnectionsSuccessResponse mockResponse = new LoadConnectionsSuccessResponse("success");
    assertEquals(mockResponse, actualResponse);

    List<String[]> newConnections = new ArrayList<>();
    List<String[]> newHistConnections = new ArrayList<>();
    for (String id : this.database.getAllUserIds()) {
      newConnections.add(this.database.getUser(id).getConnections());
      newHistConnections.add(this.database.getUser(id).getHistoricalConnections());
    }

    for (int i = 0; i < this.database.getAllUserIds().size(); i++) {
      assertNotEquals(origConnections.get(i), newConnections.get(i));
      assertNotEquals(origHistConnections.get(i), newHistConnections.get(i));
    }

    clientConnection.disconnect();
  }
}
