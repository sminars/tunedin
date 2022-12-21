package song;

import com.neovisionaries.i18n.CountryCode;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import org.apache.hc.core5.http.ParseException;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import se.michaelthelin.spotify.model_objects.specification.ArtistSimplified;
import se.michaelthelin.spotify.model_objects.specification.AudioFeatures;
import se.michaelthelin.spotify.model_objects.specification.Paging;
import se.michaelthelin.spotify.model_objects.specification.Track;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeRefreshRequest;
import se.michaelthelin.spotify.requests.data.search.simplified.SearchTracksRequest;
import se.michaelthelin.spotify.requests.data.tracks.GetAudioFeaturesForTrackRequest;

/**
 * Class used solely for getting random songs from the spotify API and returning song rows in
 * GetRandomSongsHandler to create local songs csv file.
 */
public class RandomSpotifySongSearch {

  private String countryCode;

  public RandomSpotifySongSearch() {
    this.countryCode = "US";
  }

  public RandomSpotifySongSearch(String countryCode) {
    if (Arrays.deepToString(iso3166_1_alpha_2_countryCodes).contains(countryCode)) {
      this.countryCode = countryCode;
    } else {
      this.countryCode = "US";
    }
    System.out.println("Country Code used: " + this.countryCode);
  }

  /**
   * Generates random search query for spotify search parameter
   *
   * @return - a String representing the query
   */
  private String getRandomSearch() {
    // characters to randomly choose from
    String characters = "abcdefghijklmnopqrstuvwxyz";

    Random random = new Random();
    String randomChar = String.valueOf(characters.charAt(random.nextInt(26)));

    return switch (random.nextInt(1)) {
      case 0 -> randomChar + "%";
      case 1 -> "%" + randomChar + "%";
      default -> "";
    };
  }

  private String generateAuthToken() {
    SpotifyApi spotifyApi =
        new SpotifyApi.Builder()
            .setClientId(System.getenv("CLIENT_ID"))
            .setClientSecret(System.getenv("CLIENT_SECRET"))
            .setRefreshToken(
                "AQAQcVJhyLYllahLB7R5wklN-ovXc9_RsRQxbkNsC8kqnP2KNA-sHmt5YR0Nqe0O70jKV9Y6Xobhgebi26nt1aAQ3RryvPg6_E-ty04PAoXC3r8P9usQsW01PnmxslJCBxk")
            .build();
    AuthorizationCodeRefreshRequest authorizationCodeRefreshRequest =
        spotifyApi.authorizationCodeRefresh().build();
    AuthorizationCodeCredentials authorizationCodeCredentials = null;
    try {
      authorizationCodeCredentials = authorizationCodeRefreshRequest.execute();
    } catch (IOException | SpotifyWebApiException | ParseException e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
    return authorizationCodeCredentials.getAccessToken();
  }

  public Song getRandomSong() {
    String search = this.getRandomSearch();

    Random random = new Random();
    int offset = random.nextInt(1000);
    System.out.println("offset: " + offset);

    SpotifyApi spotifyApi =
        new SpotifyApi.Builder()
            .setClientId(System.getenv("CLIENT_ID"))
            .setClientSecret(System.getenv("CLIENT_SECRET"))
            .setAccessToken(this.generateAuthToken())
            .build();
    SearchTracksRequest searchTracksRequest =
        spotifyApi
            .searchTracks(search)
            .limit(1)
            .market(CountryCode.getByCode(this.countryCode))
            .offset(offset)
            .build();
    try {
      Paging<Track> trackPaging = searchTracksRequest.execute();
      Track track = trackPaging.getItems()[0];
      String title = track.getName();
      System.out.println("title: " + title);
      String id = track.getId();
      // artists
      List<String> artists = new ArrayList<>();
      ArtistSimplified[] artistsSimp = track.getArtists();
      for (ArtistSimplified artist : artistsSimp) {
        artists.add(artist.getName());
      }
      System.out.println("artists: " + artists);
      // features
      GetAudioFeaturesForTrackRequest getAudioFeaturesForTrackRequest =
          spotifyApi.getAudioFeaturesForTrack(id).build();
      AudioFeatures audioFeatures = getAudioFeaturesForTrackRequest.execute();

      double[] features = new double[6];
      features[0] = audioFeatures.getAcousticness();
      features[1] = audioFeatures.getDanceability();
      features[2] = audioFeatures.getEnergy();
      features[3] = audioFeatures.getInstrumentalness();
      features[4] = audioFeatures.getSpeechiness();
      features[5] = audioFeatures.getValence();

      return new Song(title, id, artists, features);
    } catch (IOException | SpotifyWebApiException | ParseException e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }

  public static String[][] iso3166_1_alpha_2_countryCodes =
      new String[][] {
        // A
        {"AD", "Andorra"},
        {"AE", "United Arab Emirates"},
        {"AF", "Afghanistan"},
        {"AG", "Antigua and Barbuda"},
        {"AI", "Anguilla"},
        {"AL", "Albania"},
        {"AM", "Armenia"},
        {"AO", "Angola"},
        {"AQ", "Antarctica"},
        {"AR", "Argentina"},
        {"AS", "American Samoa"},
        {"AT", "Austria"},
        {"AU", "Australia"},
        {"AW", "Aruba"},
        {"AX", "Åland Islands"},
        {"AZ", "Azerbaijan"},
        // B
        {"BA", "Bosnia and Herzegovina"},
        {"BB", "Barbados"},
        {"BD", "Bangladesh"},
        {"BE", "Belgium"},
        {"BF", "Burkina Faso"},
        {"BG", "Bulgaria"},
        {"BH", "Bahrain"},
        {"BI", "Burundi"},
        {"BJ", "Benin"},
        {"BL", "Saint Barthélemy"},
        {"BM", "Bermuda"},
        {"BN", "Brunei Darussalam"},
        {"BO", "Bolivia, Plurinational State of"},
        {"BQ", "Bonaire, Sint Eustatius and Saba"},
        {"BR", "Brazil"},
        {"BS", "Bahamas"},
        {"BT", "Bhutan"},
        {"BV", "Bouvet Island"},
        {"BW", "Botswana"},
        {"BY", "Belarus"},
        {"BZ", "Belize"},
        // C
        {"CA", "Canada"},
        {"CC", "Cocos (Keeling) Islands"},
        {"CD", "Congo, the Democratic Republic of"},
        {"CF", "Central African Republic"},
        {"CG", "Congo"},
        {"CH", "Switzerland"},
        {"CI", "Côte d'Ivoire"},
        {"CK", "Cook Islands"},
        {"CL", "Chile"},
        {"CM", "Cameroon"},
        {"CN", "China"},
        {"CO", "Colombia"},
        {"CR", "Costa Rica"},
        {"CU", "Cuba"},
        {"CV", "Cabo Verde"},
        {"CW", "Curaçao"},
        {"CX", "Christmas Island"},
        {"CY", "Cyprus"},
        {"CZ", "Czech Republic"},
        // D
        {"DE", "Germany"},
        {"DJ", "Djibouti"},
        {"DK", "Denmark"},
        {"DM", "Dominica"},
        {"DO", "Dominican Republic"},
        {"DZ", "Algeria"},
        // E
        {"EC", "Ecuador"},
        {"EE", "Estonia"},
        {"EG", "Egypt"},
        {"EH", "Western Sahara"},
        {"ER", "Eritrea"},
        {"ES", "Spain"},
        {"ET", "Ethiopia"},
        // F
        {"FI", "Finland"},
        {"FJ", "Fiji"},
        {"FK", "Falkland Islands (Malvinas)"},
        {"FM", "Micronesia, Federated States of"},
        {"FO", "Faroe Islands"},
        {"FR", "France"},
        // G
        {"GA", "Gabon"},
        {"GB", "United Kingdom of Great Britain and Northern Ireland"},
        {"GD", "Grenada"},
        {"GE", "Georgia"},
        {"GF", "French Guiana"},
        {"GG", "Guernsey"},
        {"GH", "Ghana"},
        {"GI", "Gibraltar"},
        {"GL", "Greenland"},
        {"GM", "Gambia"},
        {"GN", "Guinea"},
        {"GP", "Guadeloupe"},
        {"GQ", "Equatorial Guinea"},
        {"GR", "Greece"},
        {"GS", "South Georgia and the South Sandwich Islands"},
        {"GT", "Guatemala"},
        {"GU", "Guam"},
        {"GW", "Guinea-Bissau"},
        {"GY", "Guyana"},
        // H
        {"HK", "Hong Kong"},
        {"HM", "Heard Island and McDonalds Islands"},
        {"HN", "Honduras"},
        {"HR", "Croatia"},
        {"HT", "Haiti"},
        {"HU", "Hungary"},
        // I
        {"ID", "Indonesia"},
        {"IE", "Ireland"},
        {"IL", "Israel"},
        {"IM", "Isle of Man"},
        {"IN", "India"},
        {"IO", "British Indian Ocean Territory"},
        {"IQ", "Iraq"},
        {"IR", "Iran, Islamic Republic of"},
        {"IS", "Iceland"},
        {"IT", "Italy"},
        // J
        {"JE", "Jersey"},
        {"JM", "Jamaica"},
        {"JO", "Jordan"},
        {"JP", "Japan"},
        // K
        {"KE", "Kenya"},
        {"KG", "Kyrgyzstan"},
        {"KH", "Cambodia"},
        {"KI", "Kiribati"},
        {"KM", "Comoros"},
        {"KN", "Saint Kitts and Nevis"},
        {"KP", "Korea, Democratic People's Republic of"},
        {"KR", "Korea, Republic of"},
        {"KW", "Kuwait"},
        {"KY", "Cayman Islands"},
        {"KZ", "Kazakhstan"},
        // L
        {"LA", "Lao People's Democratic Republic"},
        {"LB", "Lebanon"},
        {"LC", "Saint Lucia"},
        {"LI", "Liechtenstein"},
        {"LK", "Sri Lanka"},
        {"LR", "Liberia"},
        {"LS", "Lesotho"},
        {"LT", "Lithuania"},
        {"LU", "Luxembourg"},
        {"LV", "Latvia"},
        // M
        {"MA", "Morocco"},
        {"MC", "Monaco"},
        {"MD", "Moldova, Republic of"},
        {"ME", "Montenegro"},
        {"MF", "Saint Martin (French part)"},
        {"MG", "Madagascar"},
        {"MH", "Marshall Islands"},
        {"MK", "Macedonia, the former Yugoslav Republic of"},
        {"ML", "Mali"},
        {"MM", "Myanmar"},
        {"MN", "Mongolia"},
        {"MO", "Macao"},
        {"MP", "Northern Mariana Islands"},
        {"MQ", "Martinique"},
        {"MR", "Mauritania"},
        {"MS", "Montserrat"},
        {"MT", "Malta"},
        {"MU", "Mauritius"},
        {"MV", "Maldives"},
        {"MW", "Malawi"},
        {"MX", "Mexico"},
        {"MY", "Malaysia"},
        {"MZ", "Mozambique"},
        // N
        {"NA", "Namibia"},
        {"NC", "New Caledonia"},
        {"NE", "Niger"},
        {"NF", "Norfolk Island"},
        {"NG", "Nigeria"},
        {"NI", "Nicaragua"},
        {"NL", "Netherlands"},
        {"NO", "Norway"},
        {"NP", "Nepal"},
        {"NR", "Nauru"},
        {"NU", "Niue"},
        {"NZ", "New Zealand"},
        // O
        {"OM", "Oman"},
        // P
        {"PA", "Panama"},
        {"PE", "Peru"},
        {"PF", "French Polynesia"},
        {"PG", "Papua New Guinea"},
        {"PH", "Philippines"},
        {"PK", "Pakistan"},
        {"PL", "Poland"},
        {"PM", "Saint Pierre and Miquelon"},
        {"PN", "Pitcairn"},
        {"PR", "Puerto Rico"},
        {"PS", "Palestine, State of"},
        {"PT", "Portugal"},
        {"PW", "Palau"},
        {"PY", "Paraguay"},
        // Q
        {"QA", "Qatar"},
        // R
        {"RE", "Réunion"},
        {"RO", "Romania"},
        {"RS", "Serbia"},
        {"RU", "Russian Federation"},
        {"RW", "Rwanda"},
        // S
        {"SA", "Saudi Arabia"},
        {"SB", "Solomon Islands"},
        {"SC", "Seychelles"},
        {"SD", "Sudan"},
        {"SE", "Sweden"},
        {"SG", "Singapore"},
        {"SH", "Saint Helena, Ascension and Tristan da Cunha"},
        {"SI", "Slovenia"},
        {"SJ", "Svalbard and Jan Mayen"},
        {"SK", "Slovakia"},
        {"SL", "Sierra Leone"},
        {"SM", "San Marino"},
        {"SN", "Senegal"},
        {"SO", "Somalia"},
        {"SR", "Suriname"},
        {"SS", "South Sudan"},
        {"ST", "Sao Tome and Principe"},
        {"SV", "El Salvador"},
        {"SX", "Sint Maarten (Dutch part)"},
        {"SY", "Syrian Arab Republic"},
        {"SZ", "Swaziland"},
        // T
        {"TC", "Turks and Caicos Islands"},
        {"TD", "Chad"},
        {"TF", "French Southern Territories"},
        {"TG", "Togo"},
        {"TH", "Thailand"},
        {"TJ", "Tajikistan"},
        {"TK", "Tokelau"},
        {"TL", "Timor-Leste"},
        {"TM", "Turkmenistan"},
        {"TN", "Tunisia"},
        {"TO", "Tonga"},
        {"TR", "Turkey"},
        {"TT", "Tuvalu"},
        {"TW", "Taiwan, Province of China"},
        {"TZ", "Tanzania, United Republic of"},
        // U
        {"UA", "Ukraine"},
        {"UG", "Uganda"},
        {"UM", "United States Minor Outlying Islands"},
        {"US", "United States of America"},
        {"UY", "Uruguay"},
        {"UZ", "Uzbekistan"},
        // V
        {"VA", "Holy See"},
        {"VC", "Saint Vincent and the Grenadines"},
        {"VE", "Venezuela, Bolivarian Republic of"},
        {"VG", "Virgin Islands, British"},
        {"VI", "Virgin Islands, U.S."},
        {"VN", "Viet Nam"},
        {"VU", "Vanuatu"},
        // W
        {"WF", "Wallis and Futuna"},
        {"WS", "Samoa"},
        // Y
        {"YE", "Yemen"},
        {"YT", "Mayotte"},
        // Z
        {"ZA", "South Africa"},
        {"ZM", "Zambia"},
        {"ZW", "Zimbabwe"}
      };
}
