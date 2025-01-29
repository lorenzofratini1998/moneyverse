package it.moneyverse.budget.model.repositories;

import static org.junit.jupiter.api.Assertions.assertEquals;

import it.moneyverse.budget.model.dto.BudgetCriteria;
import it.moneyverse.budget.model.entities.Budget;
import it.moneyverse.budget.utils.BudgetCriteriaRandomGenerator;
import it.moneyverse.budget.utils.BudgetTestContext;
import it.moneyverse.core.boot.*;
import jakarta.persistence.EntityManager;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest(
    properties = {
      "spring.datasource.url=jdbc:h2:mem:testdb",
      "spring.datasource.driverClassName=org.h2.Driver",
      "spring.datasource.username=sa",
      "spring.datasource.password=password",
      "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
      "spring.jpa.hibernate.ddl-auto=create",
      "spring.jpa.properties.hibernate.show_sql=false",
      "flyway.enabled=false"
    },
    excludeAutoConfiguration = {
      FlywayAutoConfiguration.class,
      SecurityAutoConfiguration.class,
      UserServiceGrpcClientAutoConfiguration.class,
      CurrencyServiceGrpcClientAutoConfiguration.class,
      DatasourceAutoConfiguration.class,
      KafkaAutoConfiguration.class
    })
class BudgetCustomRepositoryImplTest {

  @Autowired EntityManager entityManager;
  @Autowired BudgetCustomRepositoryImpl customRepository;

  static BudgetTestContext testContext;

  @BeforeAll
  public static void beforeAll() {
    testContext = new BudgetTestContext();
    testContext.getBudgets().forEach(budget -> budget.setBudgetId(null));
  }

  @BeforeEach
  public void setup() {
    for (Budget budget : testContext.getBudgets()) {
      entityManager.persist(budget);
    }
    entityManager.flush();
  }

  @Test
  void givenCriteria_thenReturnFilteredBudgets() {
    BudgetCriteria criteria = new BudgetCriteriaRandomGenerator(testContext).generate();
    List<Budget> expected = testContext.filterBudgets(criteria);

    List<Budget> actual = customRepository.findBudgets(criteria);

    assertEquals(expected.size(), actual.size());
  }
}
