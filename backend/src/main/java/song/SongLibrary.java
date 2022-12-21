package song;

import csv.CSVParser;
import java.util.List;
import java.util.Random;

public class SongLibrary {

  private CSVParser<Song> songCSVParser;
  private Song[] songLibrary;

  public SongLibrary(CSVParser<Song> songCSVParser) {
    this.songCSVParser = songCSVParser;
    List<Song> songs = this.songCSVParser.getParsedData();
    this.songLibrary = new Song[songs.size()];
    int i = 0;
    for (Song song : songs) {
      this.songLibrary[i] = song;
      i++;
    }
  }

  public Song[] getSongLibrary() {
    return this.songLibrary;
  }

  public Song getRandom() {
    int randInt = this.randomIndex();
    return this.getSongLibrary()[randInt];
  }

  private int randomIndex() {
    int total = this.getSongLibrary().length;
    Random random = new Random();
    return random.nextInt(total);
  }
}
