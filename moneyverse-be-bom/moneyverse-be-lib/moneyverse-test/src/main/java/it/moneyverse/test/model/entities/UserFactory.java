package it.moneyverse.test.model.entities;

import static it.moneyverse.test.model.TestFactory.MAX_USERS;
import static it.moneyverse.test.model.TestFactory.MIN_USERS;

import it.moneyverse.core.model.entities.UserModel;
import it.moneyverse.test.utils.RandomUtils;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserFactory {

  private static final Logger LOGGER = LoggerFactory.getLogger(UserFactory.class);

  public static List<UserModel> createUsers() {
    int numUsers = RandomUtils.randomInteger(MIN_USERS, MAX_USERS);
    List<UserModel> users = new ArrayList<>();
    for (int i = 0; i < numUsers; i++) {
      users.add(new FakeUser(i));
    }
    LOGGER.info("Created {} random users for testing", users.size());
    return users;
  }
}
