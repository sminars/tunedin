package user;

import static org.junit.jupiter.api.Assertions.assertEquals;

import database.LocalDatabase;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import kdtree.KdTree;
import org.junit.jupiter.api.Test;
import song.Song;

public class UserTest {

  private User newUser;

  private LocalDatabase localDatabase;
  private KdTree<User> userTree;
  private KdTree<Song> songTree;

  /** Sets up a local database with 7 mock users */
  public void setUpLocalDatabase() {
    Song song1 =
        new Song(
            "user1",
            "DESPECHÁ",
            "53tfEupEzQRtVFOeZvk7xq",
            new ArrayList<String>(List.of("ROSALÍA")),
            new double[] {0.1, 0.9, 0.6, 0.0, 0.1, 0.8});
    User user1 =
        new User(
            "user1",
            "Kawthar Morris",
            null,
            2,
            song1,
            new String[] {"user4", "user3", "user5", "user2", "user7"},
            new double[] {0.3, 0.7, 0.2, 0.1, 0.4, 0.6},
            new String[] {"user2", "user3", "user7", "user4", "user5"});
    Song song2 =
        new Song(
            "user2",
            "Photograph",
            "7sgRVxSsmg7QqyL4qofyPP",
            new ArrayList<String>(List.of("Ed Sheeran")),
            new double[] {0.6, 0.7, 0.4, 0.0, 0.0, 0.2});
    User user2 =
        new User(
            "user2",
            "Rin Holt",
            null,
            2,
            song2,
            new String[] {"user4", "user3", "user7", "user1", "user5"},
            new double[] {0.4, 0.5, 0.2, 0.0, 0.4, 0.6},
            new String[] {null, null, null, null, null});
    Song song3 =
        new Song(
            "user3",
            "One Kiss (with Dua Lipa)",
            "7ef4DlsgrMEH11cDZd32M6",
            new ArrayList<String>(List.of("Calvin Harris", "Dua Lipa")),
            new double[] {0.0, 0.8, 0.9, 0.0, 0.1, 0.6});
    User user3 =
        new User(
            "user3",
            "Heike Allegri",
            null,
            5,
            song3,
            new String[] {"user5", "user4", "user1", "user2", "user7"},
            new double[] {0.4, 0.5, 0.2, 0.0, 0.4, 0.6},
            new String[] {"user4", "user2", "user7", "user1", "user5"});
    Song song4 =
        new Song(
            "user4",
            "Nights",
            "7eqoqGkKwgOaWNNHx90uEZ",
            new ArrayList<String>(List.of("Frank Ocean")),
            new double[] {0.4, 0.5, 0.6, 0.0, 0.2, 0.4});
    User user4 =
        new User(
            "user4",
            "Nanabozho Wibawa",
            null,
            5,
            song4,
            new String[] {"user2", "user5", "user3", "user1", "user6"},
            new double[] {0.4, 0.5, 0.4, 0.0, 0.4, 0.5},
            new String[] {"user2", "user3", "user7", "user5", "user6"});
    Song song5 =
        new Song(
            "user5",
            "When You Say Nothing At All",
            "0gcjc7Vt5xtcfmJgf6g2IO",
            new ArrayList<String>(List.of("Ronan Keating")),
            new double[] {0.2, 0.5, 0.4, 0.0, 0.0, 0.4});
    User user5 =
        new User(
            "user5",
            "Rambabu Preston",
            null,
            4,
            song5,
            new String[] {"user2", "user4", "user1", "user3", "user6"},
            new double[] {0.3, 0.5, 0.6, 0.0, 0.2, 0.6},
            new String[] {"user4", "user7", "user6", "user3", "user2"});
    Song song6 =
        new Song(
            "user6",
            "Have Yourself A Merry Little Christmas",
            "7Jv9FmMkHDDXMtORtu9Lbo",
            new ArrayList<String>(List.of("Sam Smith")),
            new double[] {0.9, 0.4, 0.2, 0.0, 0.1, 0.3});
    User user6 =
        new User(
            "user6",
            "Svetlana Abraham",
            null,
            3,
            song6,
            new String[] {"user2", "user5", "user4", "user1", "user3"},
            new double[] {0.3, 0.5, 0.6, 0.0, 0.2, 0.6},
            new String[] {"user4", "user7", "user5", "user3", "user2"});
    Song song7 =
        new Song(
            "user7",
            "Too Many Nights - Instrumental",
            "4ReJBi5fD6RiAQvknuRkqs",
            new ArrayList<String>(List.of("Metro Boomin")),
            new double[] {0.0, 0.6, 0.6, 0.9, 0.0, 0.3});
    User user7 =
        new User(
            "user7",
            "Johanna Law",
            null,
            2,
            song7,
            new String[] {"user4", "user5", "user3", "user1", "user2"},
            new double[] {0.3, 0.5, 0.6, 0.0, 0.2, 0.6},
            new String[] {"user4", "user5", "user6", "user3", "user2"});
    this.localDatabase = new LocalDatabase();
    this.localDatabase.register(user1);
    this.localDatabase.register(user2);
    this.localDatabase.register(user3);
    this.localDatabase.register(user4);
    this.localDatabase.register(user5);
    this.localDatabase.register(user6);
    this.localDatabase.register(user7);
  }

  /** Sets up a brand new user and adds it to the local database */
  public void setUpNewUser() {
    Song song =
        new Song(
            "newUser",
            "willow",
            "5C9JlYhuv96JQXyXuxYsB2",
            new ArrayList<String>(List.of("Taylor Swift")),
            new double[] {0.8, 0.3, 0.5, 0.1, 0.2, 0.4});
    this.newUser =
        new User(
            "newUser",
            "Bradley Wiseman",
            null,
            0,
            song,
            new String[] {null, null, null, null, null},
            null,
            new String[] {null, null, null, null, null});
  }

  /** Sets up userTree and songTree using localDatabase */
  public void setUpKdTrees() {
    List<String> userIds = this.localDatabase.getAllUserIds();
    List<User> users = new ArrayList<>();
    List<Song> songs = new ArrayList<>();
    for (String userId : userIds) {
      User user = this.localDatabase.getUser(userId);
      users.add(user);
      songs.add(user.getCurrentSong());
    }
    this.userTree = new KdTree<>(users, 0);
    this.songTree = new KdTree<>(songs, 0);
  }

  /** Tests update historical song point, where there is no historical song point to begin with */
  @Test
  public void testUpdateHspNewUser() {
    setUpNewUser();

    this.newUser.updateHistoricalSongPoint(newUser.getCurrentSong().getPoint());

    // membershipLength incremented by 1
    assertEquals(1, this.newUser.getMembershipLength());
    assertEquals(
        "[0.8, 0.3, 0.5, 0.1, 0.2, 0.4]", Arrays.toString(this.newUser.getHistoricalSongPoint()));
    assertEquals(
        Arrays.toString(this.newUser.getCurrentSong().getPoint()),
        Arrays.toString(this.newUser.getHistoricalSongPoint()));
  }

  /** Tests update historical song point, where one previous update was made */
  @Test
  public void testSecondUpdateHspNewUser() {
    setUpNewUser();

    // simulate 1 previous update
    this.newUser.updateHistoricalSongPoint(this.newUser.getCurrentSong().getPoint());

    // simulate 2nd update
    Song newSong =
        new Song(
            "newUser",
            "Hold Me While You Wait",
            "60iSKGrGazRzICtMjADNSM",
            new ArrayList<String>(List.of("Lewis Capaldi")),
            new double[] {0.4, 0.7, 0.5, 0.0, 0.0, 0.2});
    this.newUser.setCurrentSong(newSong);
    this.newUser.updateHistoricalSongPoint(this.newUser.getCurrentSong().getPoint());

    // membershipLength incremented by 1
    assertEquals(2, this.newUser.getMembershipLength());
    assertEquals(
        "[0.6000000000000001, 0.5, 0.5, 0.05, 0.1, 0.30000000000000004]",
        Arrays.toString(newUser.getHistoricalSongPoint()));
  }

  /** Tests update historical song point, where two previous updates were made */
  @Test
  public void testThirdUpdateHspNewUser() {
    setUpNewUser();

    // simulate 1 previous update
    this.newUser.updateHistoricalSongPoint(this.newUser.getCurrentSong().getPoint());

    // simulate 2nd update
    Song song2 =
        new Song(
            "newUser",
            "Hold Me While You Wait",
            "60iSKGrGazRzICtMjADNSM",
            new ArrayList<String>(List.of("Lewis Capaldi")),
            new double[] {0.4, 0.7, 0.5, 0.0, 0.0, 0.2});
    this.newUser.setCurrentSong(song2);
    this.newUser.updateHistoricalSongPoint(this.newUser.getCurrentSong().getPoint());

    // simulate 3rd update
    Song newSong =
        new Song(
            "newUser",
            "Iyong Iyo",
            "6RUEFRLol05iSCzQaCHNK5",
            new ArrayList<String>(List.of("Zack Tabudlo")),
            new double[] {0.4, 0.6, 0.3, 0.0, 0.0, 0.4});
    this.newUser.setCurrentSong(newSong);
    this.newUser.updateHistoricalSongPoint(this.newUser.getCurrentSong().getPoint());

    // membershipLength incremented by 1
    assertEquals(3, this.newUser.getMembershipLength());
    assertEquals(
        "[0.5333333333333334"
            + ", 0.5333333333333333"
            + ", 0.43333333333333335"
            + ", 0.03333333333333334"
            + ", 0.06666666666666668"
            + ", 0.33333333333333337]",
        Arrays.toString(newUser.getHistoricalSongPoint()));
  }

  /** Tests findConnections on a new user */
  @Test
  public void testFindConnectionsNewUser() {
    setUpLocalDatabase();
    setUpNewUser();
    // register new user
    this.localDatabase.register(this.newUser);
    // update historical song point
    this.newUser.updateHistoricalSongPoint(newUser.getCurrentSong().getPoint());
    // update user in database
    this.localDatabase.updateUser(this.newUser.getUserId(), this.newUser);
    setUpKdTrees();

    String[] connections = this.newUser.findConnections(this.songTree);
    assertEquals("[user4, user6, user5, user2, user1]", Arrays.toString(connections));
  }

  /** Tests findHistoricalConnections on a new user */
  @Test
  public void testFindHistoricalConnectionsNewUser() {
    setUpLocalDatabase();
    setUpNewUser();
    // register new user
    this.localDatabase.register(this.newUser);
    // update historical song point
    this.newUser.updateHistoricalSongPoint(newUser.getCurrentSong().getPoint());
    // update user in database
    this.localDatabase.updateUser(this.newUser.getUserId(), this.newUser);
    setUpKdTrees();

    String[] connections = this.newUser.findHistoricalConnections(this.userTree);
    assertEquals("[user4, user7, user5, user6, user3]", Arrays.toString(connections));
  }

  /**
   * Tests findConnections and findHistorical Connections on a user with previously loaded
   * connections
   */
  @Test
  public void testReplaceConnections() {
    setUpLocalDatabase();
    setUpNewUser();
    // register new user
    this.localDatabase.register(this.newUser);
    // update historical song point
    this.newUser.updateHistoricalSongPoint(newUser.getCurrentSong().getPoint());
    // update user in database
    this.localDatabase.updateUser(this.newUser.getUserId(), this.newUser);
    // load trees
    setUpKdTrees();
    // get connections
    String[] conn = this.newUser.findConnections(this.songTree);
    String[] histConn = this.newUser.findHistoricalConnections(this.userTree);
    // update user's connections
    this.newUser.setConnections(conn);
    this.newUser.setHistoricalConnections(histConn);

    // simulate 2nd connections update
    this.localDatabase.updateUser(this.newUser.getUserId(), this.newUser);
    Song newSong =
        new Song(
            "newUser",
            "Hold Me While You Wait",
            "60iSKGrGazRzICtMjADNSM",
            new ArrayList<String>(List.of("Lewis Capaldi")),
            new double[] {0.4, 0.7, 0.5, 0.0, 0.0, 0.2});
    this.newUser.setCurrentSong(newSong);
    this.newUser.updateHistoricalSongPoint(this.newUser.getCurrentSong().getPoint());

    // update user in database to load trees with updated points
    this.localDatabase.updateUser(this.newUser.getUserId(), this.newUser);
    setUpKdTrees();

    // check expected connections
    String[] actualConn = this.newUser.findConnections(this.songTree);
    String[] actualHistConn = this.newUser.findHistoricalConnections(this.userTree);
    assertEquals("[user2, user4, user5, user6, user3]", Arrays.toString(actualConn));
    assertEquals("[user4, user7, user5, user6, user2]", Arrays.toString(actualHistConn));
  }
}
