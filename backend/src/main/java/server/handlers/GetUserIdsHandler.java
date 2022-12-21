package server.handlers;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import database.UserDatabase;
import java.util.List;
import server.ErrBadJsonResponse;
import spark.Request;
import spark.Response;
import spark.Route;

/** Handler class for the get-all-user-ids endpoint */
public class GetUserIdsHandler implements Route {

  UserDatabase database;

  /**
   * Constructor
   *
   * @param database that stores users
   */
  public GetUserIdsHandler(UserDatabase database) {
    this.database = database;
  }

  /**
   * Retrieves a list of all user ids from the database
   *
   * @param request
   * @param response
   * @return a serialized success or error response
   */
  @Override
  public Object handle(Request request, Response response) {
    try {
      List<String> ids = this.database.getAllUserIds();
      return new GetUserIdsSuccessResponse(ids).serialize();
    } catch (Exception e) {
      return new ErrBadJsonResponse().serialize();
    }
  }

  /**
   * Response object to send with User object
   *
   * @param result - success message
   * @param ids - list of all user ids that was retrieved from the database
   */
  public record GetUserIdsSuccessResponse(String result, List<String> ids) {

    public GetUserIdsSuccessResponse(List<String> ids) {
      this("success", ids);
    }

    public String serialize() {
      try {
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<GetUserIdsSuccessResponse> adapter =
            moshi.adapter(GetUserIdsSuccessResponse.class);
        return adapter.toJson(this);
      } catch (Exception e) {
        e.printStackTrace();
        throw e;
      }
    }
  }
}
