package csv;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import song.Song;
import song.SongFactory;
import song.SongLibrary;
import user.User;
import user.UserFactory;

public class CSVParserTest {

  private Reader testFileReader;
  private CSVParser testParser;
  private SongLibrary testSongLibrary;

  /** Sets the testParser for the basic csv case. */
  public void setBasicCase() throws IOException, FactoryFailureException {
    try {
      this.testFileReader = new FileReader("data/testing/test-basic.csv");
    } catch (FileNotFoundException e) {
      throw new RuntimeException(e);
    }
    this.testParser = new CSVParser(this.testFileReader);
  }

  /** Sets the testParser for the empty csv case. */
  public void setEmptyCase() throws IOException, FactoryFailureException {
    try {
      this.testFileReader = new FileReader("data/testing/test-empty.csv");
    } catch (FileNotFoundException e) {
      throw new RuntimeException(e);
    }
    this.testParser = new CSVParser(this.testFileReader);
  }

  /**
   * Sets the testParser for the missing fields csv case, where some rows may have missing fields.
   */
  public void setMissingFieldsCase() throws IOException, FactoryFailureException {
    try {
      this.testFileReader = new FileReader("data/testing/test-missing-fields.csv");
    } catch (FileNotFoundException e) {
      throw new RuntimeException(e);
    }
    this.testParser = new CSVParser(this.testFileReader);
  }

  /**
   * Sets the testParser for the case where some values may have spaces (e.g. first and last names).
   */
  public void setWithSpacesCase() throws IOException, FactoryFailureException {
    try {
      this.testFileReader = new FileReader("data/testing/test-with-spaces.csv");
    } catch (FileNotFoundException e) {
      throw new RuntimeException(e);
    }
    this.testParser = new CSVParser(this.testFileReader);
  }

  /** Sets the testParser with a StringReader as input to the constructor. */
  public void setTestParserStringReader() throws IOException, FactoryFailureException {
    StringReader sr =
        new StringReader(
            """
        Name,Age,Sex
        Joe,12,Male
        Sue,1,Female
        Derek,17,Male
        Quinn,20,Female""");
    this.testParser = new CSVParser<>(sr);
  }

  /**
   * Tests the expected parsed data, counter values, and column titles on the basic csv test file.
   */
  @Test
  public void testBasicCase() throws IOException, FactoryFailureException {
    setBasicCase();

    ArrayList<List<String>> expectedData = new ArrayList<>();
    expectedData.add(List.of("Joe", "12", "Male"));
    expectedData.add(List.of("Sue", "1", "Female"));
    expectedData.add(List.of("Derek", "17", "Male"));
    expectedData.add(List.of("Quinn", "20", "Female"));
    assertEquals(expectedData, this.testParser.getParsedData());

    assertEquals(12, this.testParser.getWordCount());
    assertEquals(43, this.testParser.getCharCount());
    assertEquals(4, this.testParser.getRowCount());
    assertEquals(3, this.testParser.getColumnCount());

    String[] expectedColumns = new String[] {"Name", "Age", "Sex"};
    assertArrayEquals(expectedColumns, this.testParser.getColumnTitles());
  }

  /**
   * Tests the expected parsed data, counter values, and column titles on the empty csv test file.
   */
  @Test
  public void testEmptyCase() throws IOException, FactoryFailureException {
    setEmptyCase();
    ArrayList<List<String>> expected = new ArrayList<>();

    assertEquals(expected, this.testParser.getParsedData());
    assertEquals(0, this.testParser.getWordCount());
    assertEquals(0, this.testParser.getCharCount());
    assertEquals(0, this.testParser.getRowCount());
    assertEquals(0, this.testParser.getColumnCount());

    String[] expectedColumns = new String[] {};
    assertArrayEquals(expectedColumns, this.testParser.getColumnTitles());
  }

  /**
   * Tests the expected parsed data, counter values, and column titles on the csv test file with
   * missing fields.
   */
  @Test
  public void testMissingFieldsCase() throws IOException, FactoryFailureException {
    setMissingFieldsCase();

    ArrayList<List<String>> expectedData = new ArrayList<>();
    expectedData.add(List.of("Joe", "12", "Male"));
    expectedData.add(List.of("", "1", "Female"));
    expectedData.add(List.of("Derek", "", "Male"));
    expectedData.add(List.of("Quinn", "20", "Female"));
    assertEquals(expectedData, this.testParser.getParsedData());

    assertEquals(10, this.testParser.getWordCount());
    assertEquals(38, this.testParser.getCharCount());
    assertEquals(4, this.testParser.getRowCount());
    assertEquals(3, this.testParser.getColumnCount());
  }

  /**
   * Tests the expected parsed data, counter values, and column titles on the csv test file with
   * spaces.
   */
  @Test
  public void testWithSpacesCase() throws IOException, FactoryFailureException {
    setWithSpacesCase();

    ArrayList<List<String>> expectedData = new ArrayList<>();
    expectedData.add(List.of("Joe Anderson", "12", "Male"));
    expectedData.add(List.of("Sue", "1", "Female"));
    expectedData.add(List.of("Derek Smith", "17", "Male"));
    expectedData.add(List.of("Quinn", "20", "Female"));
    assertEquals(expectedData, this.testParser.getParsedData());

    assertEquals(14, this.testParser.getWordCount());
    assertEquals(56, this.testParser.getCharCount());
    assertEquals(4, this.testParser.getRowCount());
    assertEquals(3, this.testParser.getColumnCount());

    String[] expectedColumns = new String[] {"Name", "Age", "Sex"};
    assertArrayEquals(expectedColumns, this.testParser.getColumnTitles());
  }

  /**
   * Tests the expected parsed data, counter values, and column titles from a StringReader input.
   */
  @Test
  public void testStringReaderCase() throws IOException, FactoryFailureException {
    setTestParserStringReader();

    ArrayList<List<String>> expectedData = new ArrayList<>();
    expectedData.add(List.of("Joe", "12", "Male"));
    expectedData.add(List.of("Sue", "1", "Female"));
    expectedData.add(List.of("Derek", "17", "Male"));
    expectedData.add(List.of("Quinn", "20", "Female"));
    assertEquals(expectedData, this.testParser.getParsedData());

    assertEquals(12, this.testParser.getWordCount());
    assertEquals(43, this.testParser.getCharCount());
    assertEquals(4, this.testParser.getRowCount());
    assertEquals(3, this.testParser.getColumnCount());

    String[] expectedColumns = new String[] {"Name", "Age", "Sex"};
    assertArrayEquals(expectedColumns, this.testParser.getColumnTitles());
  }

  /** Sets the parser for the song factory test. */
  public void setWithSongFactory() throws IOException, FactoryFailureException {
    this.testFileReader = new FileReader("data/songs.csv");
    this.testParser = new CSVParser<Song>(this.testFileReader, new SongFactory());
  }

  /** Tests the song factory creator. */
  @Test
  public void testSongFactory() throws IOException, FactoryFailureException {
    setWithSongFactory();

    Song firstSong =
        new Song(
            "willow",
            "5C9JlYhuv96JQXyXuxYsB2",
            new ArrayList<String>(List.of("Taylor Swift")),
            new double[] {
              0.8349999785423279,
              0.3919999897480011,
              0.5789999961853027,
              0.0017900000093504786,
              0.164000004529953,
              0.5490000247955322
            });
    assertEquals(firstSong, this.testParser.getParsedData().get(0));
    assertEquals(400, this.testParser.getParsedData().size());
  }

  /**
   * Sets the testParser with a UserFactory as a second input to the constructor.
   *
   * @throws IOException if an I/O Exception occurs
   * @throws FactoryFailureException if a Factory Failure Exception occurs
   */
  public void setWithUserFactory() throws IOException, FactoryFailureException {
    CSVParser<Song> songCSVParser =
        new CSVParser<Song>(new FileReader("data/songs.csv"), new SongFactory());
    this.testSongLibrary = new SongLibrary(songCSVParser);
    this.testFileReader = new FileReader("data/mock-users.csv");
    this.testParser =
        new CSVParser<User>(this.testFileReader, new UserFactory(this.testSongLibrary));
  }

  /** Tests userFactory from csv data. */
  @Test
  public void testUsersCase() throws IOException, FactoryFailureException {
    setWithUserFactory();

    assertEquals(100, this.testParser.getParsedData().size());

    User actualFirstUser = (User) this.testParser.getParsedData().get(0);
    assertEquals(
        "3nVvzfAdYooxYlp4ElVS", ((User) this.testParser.getParsedData().get(0)).getUserId());
    assertEquals(
        "Bradley Wiseman", ((User) this.testParser.getParsedData().get(0)).getDisplayName());
    assertEquals(2, ((User) this.testParser.getParsedData().get(0)).getMembershipLength());
  }

  /** Testing exceptions */
  @Test
  public void testExceptions() {
    assertThrows(
        FactoryFailureException.class,
        () -> new CSVParser<>(new FileReader("data/testing/test-basic.csv"), new SongFactory()));
  }
}
