package database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import user.User;

/** Class representing the complete database of userMap */
public class LocalDatabase implements UserDatabase {

  private HashMap<String, User> userMap;

  /** Constructor */
  public LocalDatabase() {
    this.userMap = new HashMap<String, User>();
  }

  /**
   * Constructor given a list of userMap.
   *
   * @param userMap - the list of Users to register upon creation of the database
   */
  public LocalDatabase(List<User> userMap) {
    this.userMap = new HashMap<>();
    for (User user : userMap) {
      this.register(user);
    }
  }

  /**
   * Returns User object given username
   *
   * @param userId - the name of the user to get
   * @return the User
   */
  @Override
  public User getUser(String userId) {
    if (this.userMap.containsKey(userId)) {
      return this.userMap.get(userId).clone();
    } else {
      throw new RuntimeException(
          "Local Database does not contain a user corresponding to the given user id");
    }
  }

  /**
   * Replaces the currently stored User object with the updated one, if it exists.
   *
   * @param user - the new User object to be stored
   */
  @Override
  public void updateUser(String userId, User user) {
    if (this.userMap.containsKey(userId)) {
      this.userMap.replace(userId, user);
    } else {
      this.register(user);
    }
  }

  /**
   * Get a list of all the userIds in the database.
   *
   * @return - a List of userIds
   */
  @Override
  public List<String> getAllUserIds() {
    List<String> userIds = new ArrayList<String>();
    this.userMap.forEach(
        (userId, user) -> {
          userIds.add(userId);
        });
    return userIds;
  }

  /**
   * Registers a user to the database if it doesn't already exist; if it does, the user is not added
   *
   * @param user - the user to be registered
   */
  public void register(User user) {
    if (!this.userMap.containsKey(user.getUserId())) {
      this.userMap.put(user.getUserId(), user);
    }
  }

  /**
   * Erases a user from the database if they exist
   *
   * @param user - the user to be erased
   */
  public void delete(User user) {
    if (this.userMap.containsKey(user.getUserId())) {
      this.userMap.remove(user.getUserId());
    }
  }
}
