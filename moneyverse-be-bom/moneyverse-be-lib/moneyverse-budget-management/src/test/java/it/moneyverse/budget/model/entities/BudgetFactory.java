package it.moneyverse.budget.model.entities;

import static it.moneyverse.test.utils.FakeUtils.*;

import it.moneyverse.core.model.entities.UserModel;
import it.moneyverse.test.utils.RandomUtils;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BudgetFactory {
  private static final Logger LOGGER = LoggerFactory.getLogger(BudgetFactory.class);

  public static List<Budget> createBudgets(List<UserModel> users) {
    List<Budget> budgets = new ArrayList<>();
    for (UserModel user : users) {
      int numBudgetsPerUser = RandomUtils.randomInteger(MIN_BUDGETS_PER_USER, MAX_BUDGETS_PER_USER);
      for (int i = 0; i < numBudgetsPerUser; i++) {
        budgets.add(BudgetFactory.fakeBudget(user.getUsername(), i));
      }
    }
    LOGGER.info("Created {} random budgets for testing", budgets.size());
    return budgets;
  }

  public static List<DefaultBudgetTemplate> createDefaultBudgetTemplates() {
    List<DefaultBudgetTemplate> defaultBudgetTemplates = new ArrayList<>();
    for (int i = 0; i < DEFAULT_BUDGETS_PER_USER; i++) {
      defaultBudgetTemplates.add(BudgetFactory.fakeDefaultBudgetTemplate(i));
    }
    LOGGER.info("Created {} random budget template for testing", defaultBudgetTemplates.size());
    return defaultBudgetTemplates;
  }

  public static Budget fakeBudget(String username, Integer counter) {
    counter = counter + 1;
    Budget budget = new Budget();
    budget.setBudgetId(RandomUtils.randomUUID());
    budget.setUsername(username);
    budget.setBudgetName("Budget %s".formatted(counter));
    budget.setDescription(RandomUtils.randomString(30));
    budget.setBudgetLimit(
        (int) (Math.random() * 100) % 2 == 0
            ? RandomUtils.randomDecimal(0.0, Math.random() * 2000)
                .setScale(2, RoundingMode.HALF_EVEN)
            : null);
    budget.setAmount(RandomUtils.randomBigDecimal().setScale(2, RoundingMode.HALF_EVEN));
    budget.setCurrency(RandomUtils.randomString(3).toUpperCase());
    budget.setCreatedBy(FAKE_USER);
    budget.setCreatedAt(LocalDateTime.now());
    budget.setUpdatedBy(FAKE_USER);
    budget.setUpdatedAt(LocalDateTime.now());
    return budget;
  }

  public static DefaultBudgetTemplate fakeDefaultBudgetTemplate(Integer counter) {
    counter = counter + 1;
    DefaultBudgetTemplate defaultBudgetTemplate = new DefaultBudgetTemplate();
    defaultBudgetTemplate.setId(RandomUtils.randomUUID());
    defaultBudgetTemplate.setName("Default Budget %s".formatted(counter));
    defaultBudgetTemplate.setDescription(RandomUtils.randomString(30));
    return defaultBudgetTemplate;
  }
}
