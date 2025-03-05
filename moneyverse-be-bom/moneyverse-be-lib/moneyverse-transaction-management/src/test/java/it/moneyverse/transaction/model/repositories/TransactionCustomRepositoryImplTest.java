package it.moneyverse.transaction.model.repositories;

import static org.junit.jupiter.api.Assertions.assertEquals;

import it.moneyverse.core.boot.*;
import it.moneyverse.transaction.model.TransactionTestContext;
import it.moneyverse.transaction.model.dto.TransactionCriteria;
import it.moneyverse.transaction.model.entities.Tag;
import it.moneyverse.transaction.model.entities.Transaction;
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
      DatasourceAutoConfiguration.class,
      KafkaAutoConfiguration.class,
      AccountServiceGrpcClientAutoConfiguration.class,
      BudgetServiceGrpcClientAutoConfiguration.class,
      UserServiceGrpcClientAutoConfiguration.class,
      CurrencyServiceGrpcClientAutoConfiguration.class
    })
public class TransactionCustomRepositoryImplTest {

  @Autowired EntityManager entityManager;
  @Autowired TransactionCustomRepositoryImpl customRepository;

  static TransactionTestContext testContext;

  @BeforeAll
  public static void beforeAll() {
    testContext = new TransactionTestContext();
    testContext.getTags().forEach(tag -> tag.setTagId(null));
    testContext
        .getTransactions()
        .forEach(
            transaction -> {
              transaction.setTransactionId(null);
              transaction.setTransfer(null);
            });
  }

  @BeforeEach
  public void setup() {
    for (Tag tag : testContext.getTags()) {
      entityManager.persist(tag);
    }
    entityManager.flush();
    for (Transaction transaction : testContext.getTransactions()) {
      entityManager.persist(transaction);
    }
    entityManager.flush();
  }

  @Test
  void givenCriteria_TheReturnFilteredCategories() {
    UUID userId = testContext.getRandomUser().getUserId();
    TransactionCriteria criteria = testContext.createTransactionCriteria(userId);
    List<Transaction> expected = testContext.filterTransactions(userId, criteria);

    List<Transaction> actual = customRepository.findTransactions(userId, criteria);
    assertEquals(expected.size(), actual.size());
  }
}
