package user;

import csv.CreatorFromRow;
import csv.FactoryFailureException;
import java.util.List;
import song.Song;
import song.SongLibrary;

/** Creates User objects from a List of Strings */
public class UserFactory implements CreatorFromRow<User> {

  SongLibrary songLibrary;

  public UserFactory(SongLibrary songLibrary) {
    this.songLibrary = songLibrary;
  }

  @Override
  public User create(List<String> row) throws FactoryFailureException {
    if (row.size() != 7) {
      throw new FactoryFailureException(row);
    }
    String userId = row.get(0);
    String displayName = row.get(1);
    String refreshToken = row.get(2);
    int membershipLength = Integer.parseInt(row.get(3));
    Song currentSong = this.songLibrary.getRandom();
    currentSong.setUserId(userId);
    String[] connections = row.get(4).split(";");
    String[] historicalSPStr = row.get(5).split(";");
    double[] historicalSongPoint = new double[6];
    for (int i = 0; i < historicalSPStr.length; i++) {
      historicalSongPoint[i] = Double.parseDouble(historicalSPStr[i]);
    }
    String[] historicalConnections = row.get(6).split(";");

    User user =
        new User(
            userId,
            displayName,
            refreshToken,
            membershipLength,
            currentSong,
            connections,
            historicalSongPoint,
            historicalConnections);
    // add song library for access in getting new most recent song
    user.setSongLibrary(this.songLibrary);
    return user;
  }
}
