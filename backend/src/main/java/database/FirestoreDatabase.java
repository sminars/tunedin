package database;

import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.common.primitives.Doubles;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import song.Song;
import user.User;

/** Wrapper class for Firestore Database, which implements the UserDatabase interface */
public class FirestoreDatabase implements UserDatabase {
  private Firestore firestore;

  /**
   * Constructor that takes care of setting up the Firestore configurations
   *
   * @param filepath - filepath of a private file that stores sensitive credential information
   * @param projectId - id of our Firestore project
   */
  public FirestoreDatabase(String filepath, String projectId) {
    try {
      FileInputStream serviceAccount = new FileInputStream(filepath);
      FirebaseOptions options =
          FirebaseOptions.builder()
              .setCredentials(GoogleCredentials.fromStream(serviceAccount))
              .setProjectId(projectId)
              .build();
      FirebaseApp.initializeApp(options);

      this.firestore = FirestoreClient.getFirestore();

    } catch (IOException e) {
      System.out.println(e.getMessage());
    }
  }

  /** Getter method to retrieve the Firestore object */
  public Firestore getFireStore() {
    return this.firestore;
  }

  /**
   * Generates specific a User object from its document reference stored in Firestore
   *
   * @param userId - id of user to retrieve from Firestore
   * @return - User object
   */
  @Override
  public User getUser(String userId) {
    DocumentReference docRef = this.firestore.collection("users").document(userId);
    // asynchronously retrieve the document
    ApiFuture<DocumentSnapshot> future = docRef.get();

    // future.get() blocks on response
    DocumentSnapshot document = null;
    try {
      document = future.get();
      String displayName = document.getString("displayName");
      String refreshToken = document.getString("refreshToken");
      int membershipLength = document.get("membershipLength", Integer.class);

      Map<String, Object> docMap = document.getData();

      Map<String, Object> songMap = (Map) docMap.get("currentSong");
      List<Double> featList = (List<Double>) songMap.get("features");
      Song currentSong =
          new Song(
              (String) songMap.get("userId"),
              (String) songMap.get("title"),
              (String) songMap.get("id"),
              (List<String>) songMap.get("artists"),
              this.listToDoubleArray(featList));

      List<String> connections = (List<String>) docMap.get("connections");
      List<Double> historicalSongPoint = (List<Double>) docMap.get("historicalSongPoint");
      List<String> historicalConnections = (List<String>) docMap.get("historicalConnections");

      return new User(
          userId,
          displayName,
          refreshToken,
          membershipLength,
          currentSong,
          this.listToStrArray(connections),
          this.listToDoubleArray(historicalSongPoint),
          this.listToStrArray(historicalConnections));
    } catch (ExecutionException | InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Helper method that converts a List<Double> to a double[]. Useful because Firestore is
   * incompatible with Java Array Objects.
   *
   * @param lst - lst retrieved from Firestore
   * @return double[] with the same contents as the list
   */
  private double[] listToDoubleArray(List<Double> lst) {
    if (lst != null) {
      double[] array = new double[6];
      for (int i = 0; i < lst.size(); i++) {
        array[i] = lst.get(i);
      }
      return array;
    } else {
      return new double[6];
    }
  }

  /**
   * Helper method that converts a List<String> to a String[] Useful because Firestore is
   * incompatible with Java Array Objects
   *
   * @param lst - lst retrieved from Firestore
   * @return String[] with the same contents as the list
   */
  private String[] listToStrArray(List<String> lst) {
    if (lst != null) {
      String[] array = new String[5];
      for (int i = 0; i < lst.size(); i++) {
        array[i] = lst.get(i);
      }
      return array;
    } else {
      return new String[5];
    }
  }

  /**
   * Updates the Firestore document reference of a specific user
   *
   * @param userId - id of user to update
   * @param user - User object containing the updated fields
   */
  @Override
  public void updateUser(String userId, User user) {
    DocumentReference docRef = this.firestore.collection("users").document(userId);
    docRef.update("userId", user.getUserId());
    docRef.update("displayName", user.getDisplayName());
    docRef.update("refreshToken", user.getRefreshToken());
    docRef.update("membershipLength", user.getMembershipLength());
    // update list fields as arrays
    docRef.update("connections", Arrays.asList(user.getConnections()));
    docRef.update("historicalConnections", Arrays.asList(user.getHistoricalConnections()));
    docRef.update("historicalSongPoint", Doubles.asList(user.getHistoricalSongPoint()));
    // update song field as map
    try {
      this.updateUserSong(docRef, user.getCurrentSong());
    } catch (ExecutionException | InterruptedException e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }

  /**
   * Updates the Firestore document reference with new Song information for a specific user
   *
   * @param docRef - document reference of user to update
   * @param song - Song object with updated fields
   * @throws ExecutionException
   * @throws InterruptedException
   */
  private void updateUserSong(DocumentReference docRef, Song song)
      throws ExecutionException, InterruptedException {
    Map<String, Object> songMap = new HashMap();
    songMap.put("userId", song.getUserId());
    songMap.put("title", song.getTitle());
    songMap.put("id", song.getId());
    songMap.put("artists", song.getArtists());
    List<Double> featuresList = Doubles.asList(song.getFeatures());
    songMap.put("features", featuresList);
    docRef.update("currentSong", songMap);
  }

  /**
   * Retrieves all user ids from Firestore users collection
   *
   * @return List<String> of all user ids
   */
  @Override
  public List<String> getAllUserIds() {
    List<String> ids = new ArrayList<>();
    ApiFuture<QuerySnapshot> future = this.firestore.collection("users").get();
    // future.get() blocks on response
    List<QueryDocumentSnapshot> documents = null;
    try {
      documents = future.get().getDocuments();
    } catch (InterruptedException | ExecutionException e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
    for (QueryDocumentSnapshot doc : documents) {
      ids.add(doc.getString("userId"));
    }
    return ids;
  }
}
