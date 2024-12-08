package it.moneyverse.account.model.repositories;

import static org.junit.jupiter.api.Assertions.assertEquals;

import it.moneyverse.account.model.dto.AccountCriteria;
import it.moneyverse.account.model.entities.Account;
import it.moneyverse.account.utils.AccountCriteriaRandomGenerator;
import it.moneyverse.account.utils.AccountTestContext;
import it.moneyverse.core.boot.DatasourceAutoConfiguration;
import it.moneyverse.core.boot.KafkaAutoConfiguration;
import it.moneyverse.core.boot.SecurityAutoConfiguration;
import it.moneyverse.core.boot.UserServiceGrpcClientAutoConfiguration;
import it.moneyverse.test.enums.TestModelStrategyEnum;
import it.moneyverse.test.utils.helper.MapperTestHelper;
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
      DatasourceAutoConfiguration.class,
      KafkaAutoConfiguration.class
    })
public class AccountCustomRepositoryImplTest {

  @Autowired EntityManager entityManager;
  @Autowired AccountCustomRepositoryImpl customRepository;

  static AccountTestContext testContext;
  static List<Account> accounts;

  @BeforeAll
  public static void beforeAll() {
    testContext =
        AccountTestContext.builder()
            .withStrategy(TestModelStrategyEnum.RANDOM)
            .withTestUsers()
            .withTestAccount()
            .build();
    accounts =
        MapperTestHelper.map(testContext.getModel().getAccounts(), Account.class).stream().toList();
    accounts.forEach(account -> account.setAccountId(null));
  }

  @BeforeEach
  public void setup() {
    for (Account account : accounts) {
      entityManager.persist(account);
    }
    entityManager.flush();
  }

  @Test
  void givenCriteria_thenReturnFilteredAccounts() {
    AccountCriteria criteria = new AccountCriteriaRandomGenerator(testContext).generate();
    List<Account> expected =
        testContext.filterAccounts(criteria).stream()
            .map(account -> MapperTestHelper.map(account, Account.class))
            .toList();

    List<Account> actual = customRepository.findAccounts(criteria);

    assertEquals(expected.size(), actual.size());
  }
}
