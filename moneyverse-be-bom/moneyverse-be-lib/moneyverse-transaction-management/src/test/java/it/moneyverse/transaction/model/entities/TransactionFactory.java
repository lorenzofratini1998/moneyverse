package it.moneyverse.transaction.model.entities;

import static it.moneyverse.test.utils.FakeUtils.FAKE_USER;

import it.moneyverse.core.enums.CurrencyEnum;
import it.moneyverse.core.model.entities.UserModel;
import it.moneyverse.test.utils.RandomUtils;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TransactionFactory {
  private static final Logger LOGGER = LoggerFactory.getLogger(TransactionFactory.class);
  private static final Integer MIN_TRANSACTION_PER_USER = 25;
  private static final Integer MAX_TRANSACTION_PER_USER = 100;

  public static List<Transaction> createTransactions(List<UserModel> users, List<Tag> tags) {
    List<Transaction> transactions = new ArrayList<>();
    for (UserModel user : users) {
      int numTransactionsPerUser =
          RandomUtils.randomInteger(MIN_TRANSACTION_PER_USER, MAX_TRANSACTION_PER_USER);
      for (int i = 0; i < numTransactionsPerUser; i++) {
        List<Tag> userTags =
            tags.stream().filter(tag -> tag.getUsername().equals(user.getUsername())).toList();
        if (Math.random() < 0.5 && !userTags.isEmpty()) {
          Set<Tag> randomTags = new HashSet<>();
          int tagsPerUser = RandomUtils.randomInteger(0, userTags.size() - 1);
          for (int j = 0; j < tagsPerUser; j++) {
            randomTags.add(userTags.get(RandomUtils.randomInteger(0, userTags.size() - 1)));
          }
          transactions.add(fakeTransaction(user.getUsername(), randomTags));
        } else {
          transactions.add(fakeTransaction(user.getUsername()));
        }
      }
    }
    LOGGER.info("Created {} random transactions for testing", transactions.size());
    return transactions;
  }

  public static Transaction fakeTransaction(String username, Set<Tag> tags) {
    Transaction transaction = fakeTransaction(username);
    transaction.setTags(tags);
    return transaction;
  }

  public static Transaction fakeTransaction(String username) {
    Transaction transaction = new Transaction();
    transaction.setTransactionId(RandomUtils.randomUUID());
    transaction.setUsername(username);
    transaction.setAccountId(RandomUtils.randomUUID());
    transaction.setBudgetId(RandomUtils.randomUUID());
    transaction.setDate(RandomUtils.randomLocalDate(2024, 2024));
    transaction.setDescription(RandomUtils.randomString(30));
    transaction.setAmount(RandomUtils.randomBigDecimal());
    transaction.setCurrency(RandomUtils.randomEnum(CurrencyEnum.class));
    transaction.setCreatedBy(FAKE_USER);
    transaction.setCreatedAt(LocalDateTime.now());
    transaction.setUpdatedBy(FAKE_USER);
    transaction.setUpdatedAt(LocalDateTime.now());
    return transaction;
  }
}
