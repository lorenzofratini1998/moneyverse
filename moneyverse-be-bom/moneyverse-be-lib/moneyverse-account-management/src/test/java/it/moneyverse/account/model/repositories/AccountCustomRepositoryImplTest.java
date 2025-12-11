package it.moneyverse.account.model.repositories;

import static org.junit.jupiter.api.Assertions.assertEquals;

import it.moneyverse.account.model.AccountTestContext;
import it.moneyverse.account.model.AccountTestFactory;
import it.moneyverse.account.model.dto.AccountCriteria;
import it.moneyverse.account.model.entities.Account;
import it.moneyverse.account.model.entities.AccountCategory;
import it.moneyverse.core.boot.*;
import it.moneyverse.test.utils.RandomUtils;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.UUID;
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
public class AccountCustomRepositoryImplTest {

  @Autowired EntityManager entityManager;
  @Autowired AccountCustomRepositoryImpl customRepository;

  static AccountTestContext testContext;

  @BeforeAll
  public static void beforeAll() {
    testContext = new AccountTestContext();
    testContext.getCategories().forEach(category -> category.setAccountCategoryId(null));
    testContext.getAccounts().forEach(account -> account.setAccountId(null));
  }

  @BeforeEach
  public void setup() {
    for (AccountCategory category : testContext.getCategories()) {
      entityManager.persist(category);
    }
    entityManager.flush();
    for (Account account : testContext.getAccounts()) {
      entityManager.persist(account);
    }
    entityManager.flush();
  }

  @Test
  void givenCriteria_thenReturnFilteredAccounts() {
    UUID userId = RandomUtils.randomUUID();
    AccountCriteria criteria =
        AccountTestFactory.AccountCriteriaBuilder.generator(testContext).generate();
    List<Account> expected = testContext.filterAccounts(userId, criteria);

    List<Account> actual = customRepository.findAccounts(userId, criteria);

    assertEquals(expected.size(), actual.size());
  }
}
