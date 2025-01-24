package it.moneyverse.transaction.model.entities;

import static it.moneyverse.test.utils.FakeUtils.*;

import it.moneyverse.core.enums.CurrencyEnum;
import it.moneyverse.core.model.entities.UserModel;
import it.moneyverse.test.utils.RandomUtils;
import java.time.LocalDateTime;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TransactionFactory {
  private static final Logger LOGGER = LoggerFactory.getLogger(TransactionFactory.class);

  public static List<Transaction> createTransactions(List<UserModel> users, List<Tag> tags) {
    List<Transaction> transactions = new ArrayList<>();
    for (UserModel user : users) {
      List<UUID> accounts = randomAccounts();
      List<UUID> budgets = randomBudgets();
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
          transactions.add(fakeTransaction(user.getUsername(), randomTags, accounts, budgets));
        } else {
          transactions.add(fakeTransaction(user.getUsername(), accounts, budgets));
        }
      }
    }
    LOGGER.info("Created {} random transactions for testing", transactions.size());
    return transactions;
  }

  private static List<UUID> randomAccounts() {
    List<UUID> accounts = new ArrayList<>();
    for (int i = 0;
        i < RandomUtils.randomInteger(MIN_ACCOUNTS_PER_USER, MAX_ACCOUNTS_PER_USER);
        i++) {
      accounts.add(RandomUtils.randomUUID());
    }
    return accounts;
  }

  private static List<UUID> randomBudgets() {
    List<UUID> budgets = new ArrayList<>();
    for (int i = 0;
        i < RandomUtils.randomInteger(MIN_BUDGETS_PER_USER, MAX_BUDGETS_PER_USER);
        i++) {
      budgets.add(RandomUtils.randomUUID());
    }
    return budgets;
  }

  public static Transaction fakeTransaction(
      String username, Set<Tag> tags, List<UUID> accounts, List<UUID> budgets) {
    Transaction transaction = fakeTransaction(username, accounts, budgets);
    transaction.setTags(tags);
    return transaction;
  }

  public static Transaction fakeTransaction(
      String username, List<UUID> accounts, List<UUID> budgets) {
    Transaction transaction = createTransaction(username);
    transaction.setAccountId(accounts.get(RandomUtils.randomInteger(0, accounts.size() - 1)));
    transaction.setBudgetId(budgets.get(RandomUtils.randomInteger(0, budgets.size() - 1)));
    return transaction;
  }

  public static Transaction fakeTransaction(String username) {
    Transaction transaction = createTransaction(username);
    transaction.setAccountId(RandomUtils.randomUUID());
    transaction.setBudgetId(RandomUtils.randomUUID());
    return transaction;
  }

  private static Transaction createTransaction(String username) {
    Transaction transaction = new Transaction();
    transaction.setTransactionId(RandomUtils.randomUUID());
    transaction.setUsername(username);
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
