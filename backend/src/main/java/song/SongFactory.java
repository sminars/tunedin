package song;

import csv.CreatorFromRow;
import csv.FactoryFailureException;
import java.util.Arrays;
import java.util.List;

public class SongFactory implements CreatorFromRow<Song> {

  @Override
  public Song create(List<String> row) throws FactoryFailureException {
    if (row.size() != 4) {
      throw new FactoryFailureException(row);
    }
    String title = row.get(0);
    String id = row.get(1);
    List<String> artists = Arrays.asList(row.get(2).split(";"));
    String[] featuresStr = row.get(3).split(";");
    double[] features = new double[6];
    for (int i = 0; i < featuresStr.length; i++) {
      features[i] = Double.parseDouble(featuresStr[i]);
    }
    return new Song(title, id, artists, features);
  }
}
