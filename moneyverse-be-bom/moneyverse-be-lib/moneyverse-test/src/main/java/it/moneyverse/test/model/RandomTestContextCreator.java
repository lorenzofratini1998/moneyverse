package it.moneyverse.test.model;

import it.moneyverse.core.model.entities.AccountModel;
import it.moneyverse.core.model.entities.UserModel;
import it.moneyverse.test.model.entities.FakeAccount;
import it.moneyverse.test.model.entities.FakeUser;
import it.moneyverse.test.utils.RandomUtils;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

public class RandomTestContextCreator implements TestContextCreator {

  private static final Integer MIN_USERS = 5;
  private static final Integer MAX_USERS = 20;
  private static final Integer MIN_ACCOUNTS_PER_USER = 1;
  private static final Integer MAX_ACCOUNTS_PER_USER = 5;

  @Override
  public List<UserModel> createUsers() {
    int numUsers = RandomUtils.randomInteger(MIN_USERS, MAX_USERS);
    return IntStream.range(0, numUsers)
        .mapToObj(FakeUser::new)
        .map(UserModel.class::cast)
        .toList();
  }

  @Override
  public List<AccountModel> createAccounts(List<UserModel> users) {
    return users
        .stream()
        .map(user -> randomAccounts(user.getUserId()))
        .toList()
        .stream()
        .flatMap(List::stream)
        .toList();
  }

  private List<AccountModel> randomAccounts(UUID userId) {
    int numAccountsPerUser = RandomUtils.randomInteger(MIN_ACCOUNTS_PER_USER,
        MAX_ACCOUNTS_PER_USER);
    return IntStream.range(0, numAccountsPerUser)
        .mapToObj(i -> new FakeAccount(userId, i))
        .map(AccountModel.class::cast)
        .toList();
  }
}
