package server.handlers;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import database.UserDatabase;
import server.ErrBadRequestResponse;
import spark.Request;
import spark.Response;
import spark.Route;
import user.User;

/** Handler class for the get-user endpoint */
public class GetUserHandler implements Route {

  UserDatabase database;

  /**
   * Constructor
   *
   * @param database that stores users
   */
  public GetUserHandler(UserDatabase database) {
    this.database = database;
  }

  /**
   * Retrieves user from database using the request query parameter id
   *
   * @param request
   * @param response
   * @return a serialized success or error response
   */
  @Override
  public Object handle(Request request, Response response) {
    try {
      String userId = request.queryParams("id");
      User user = this.database.getUser(userId);
      return new GetUserSuccessResponse(user).serialize();
    } catch (Exception e) {
      return new ErrBadRequestResponse().serialize();
    }
  }

  /**
   * Response object to send with User object
   *
   * @param result - success message
   * @param user - User object that was retrieved from the database
   */
  public record GetUserSuccessResponse(String result, User user) {

    public GetUserSuccessResponse(User user) {
      this("success", user);
    }

    public String serialize() {
      try {
        Moshi moshi = new Moshi.Builder().build();

        JsonAdapter<GetUserSuccessResponse> adapter = moshi.adapter(GetUserSuccessResponse.class);
        return adapter.toJson(this);
      } catch (Exception e) {
        e.printStackTrace();
        throw e;
      }
    }
  }
}
