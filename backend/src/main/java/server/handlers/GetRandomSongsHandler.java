package server.handlers;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import java.util.ArrayList;
import java.util.List;
import song.RandomSpotifySongSearch;
import song.Song;
import spark.Request;
import spark.Response;
import spark.Route;

public class GetRandomSongsHandler implements Route {

  @Override
  public Object handle(Request request, Response response) throws Exception {
    String numSongs = request.queryParams("n");
    String countryCode = request.queryParams("country-code");
    int n = Integer.parseInt(numSongs);

    RandomSpotifySongSearch generator;
    if (countryCode != null) {
      generator = new RandomSpotifySongSearch(countryCode);
    } else {
      generator = new RandomSpotifySongSearch();
    }
    List<Song> songs = new ArrayList<>();
    for (int i = 0; i < n; i++) {
      Song song = generator.getRandomSong();
      songs.add(song);
    }
    this.convertToCSV(songs);
    return new GetRandomSongsSuccessResponse(songs).serialize();
  }

  public record GetRandomSongsSuccessResponse(String result, List<Song> songs) {

    public GetRandomSongsSuccessResponse(List<Song> songs) {
      this("success", songs);
    }

    public String serialize() {
      try {
        Moshi moshi = new Moshi.Builder().build();

        JsonAdapter<GetRandomSongsSuccessResponse> adapter =
            moshi.adapter(GetRandomSongsSuccessResponse.class);
        return adapter.toJson(this);
      } catch (Exception e) {
        e.printStackTrace();
        throw e;
      }
    }
  }

  String CSV_SEPARATOR = ",";

  private void convertToCSV(List<Song> songs) {
    for (Song song : songs) {
      StringBuffer oneLine = new StringBuffer();
      oneLine.append(song.getTitle());
      oneLine.append(this.CSV_SEPARATOR);
      oneLine.append(song.getId());
      oneLine.append(this.CSV_SEPARATOR);

      List<String> artists = song.getArtists();
      StringBuilder artistStr = new StringBuilder();
      for (String artist : artists) {
        if (artistStr.length() == 0) {
          artistStr.append(artist);
        } else {
          artistStr.append(";").append(artist);
        }
      }
      oneLine.append(artistStr);
      oneLine.append(this.CSV_SEPARATOR);

      double[] features = song.getFeatures();
      StringBuilder featuresStr = new StringBuilder();
      for (double val : features) {
        if (featuresStr.length() == 0) {
          featuresStr.append(val);
        } else {
          featuresStr.append(";").append(val);
        }
      }
      oneLine.append(featuresStr);
      // print to terminal for easy retrieval
      System.out.println(oneLine);
    }
  }
}
