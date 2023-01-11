package server;

import static server.Constants.FIRESTORE_JSON_FILEPATH;
import static server.Constants.MOCK_SONGS_FILEPATH;
import static server.Constants.MOCK_USERS_FILEPATH;
import static spark.Spark.after;

import csv.CSVParser;
import csv.FactoryFailureException;
import database.FirestoreDatabase;
import database.LocalDatabase;
import database.UserDatabase;
import java.io.FileReader;
import java.io.IOException;
import server.handlers.GetUserHandler;
import server.handlers.GetUserIdsHandler;
import server.handlers.LoadConnectionsHandler;
import server.handlers.LoadSongFeaturesHandler;
import song.Song;
import song.SongFactory;
import song.SongLibrary;
import spark.Spark;
import user.User;
import user.UserFactory;

/**
 * Top-level class to run our API server. Contains the main() method which starts Spark and runs the
 * various handlers.
 */
public class Server {
  // constants to change users, songs, and firestore parameters

  /**
   * Creates local database to run server using locally stored mock users and songs.
   *
   * @return a local database
   */
  public static LocalDatabase createLocalDatabase() {
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

  public static void main(String[] args) {
    Spark.port(3232);

    /*
       Setting CORS headers to allow cross-origin requests from the client; this is necessary for the client to
       be able to make requests to the server.

       By setting the Access-Control-Allow-Origin header to "*", we allow requests from any origin.
       This is not a good idea in real-world applications, since it opens up your server to cross-origin requests
       from any website. Instead, you should set this header to the origin of your client, or a list of origins
       that you trust.

       By setting the Access-Control-Allow-Methods header to "*", we allow requests with any HTTP method.
       Again, it's generally better to be more specific here and only allow the methods you need, but for
       this demo we'll allow all methods.

       We recommend you learn more about CORS with these resources:
           - https://developer.mozilla.org/en-US/docs/Web/HTTP/CORS
           - https://portswigger.net/web-security/cors
    */
    after(
        (request, response) -> {
          response.header("Access-Control-Allow-Origin", "*");
          response.header("Access-Control-Allow-Methods", "*");
        });

    // instantiate database
    UserDatabase db;
    if (System.getenv("USING_MOCKS").equals("true")) {
      db = createLocalDatabase();
    } else {
      db = new FirestoreDatabase(FIRESTORE_JSON_FILEPATH, System.getenv("FIRESTORE_PROJECT_ID"));
    }
    System.out.println(System.getenv("FIRESTORE_PROJECT_ID"));

    // Setting up the handler for the GET endpoints
    Spark.get("load-song-features", new LoadSongFeaturesHandler(db));
    Spark.get("load-connections", new LoadConnectionsHandler(db));
    Spark.get("get-user", new GetUserHandler(db));
    Spark.get("get-all-user-ids", new GetUserIdsHandler(db));
    // just for generating mock songs to store in songs.csv
    // Spark.get("get-random-songs", new GetRandomSongsHandler());

    Spark.init();
    Spark.awaitInitialization();
    System.out.println("Server started.");
  }
}
