package it.moneyverse.budget.model.repositories;

import it.moneyverse.budget.model.entities.Budget;
import it.moneyverse.budget.model.dto.BudgetCriteria;
import it.moneyverse.budget.utils.BudgetCriteriaRandomGenerator;
import it.moneyverse.budget.utils.BudgetTestContext;
import it.moneyverse.core.boot.DatasourceAutoConfiguration;
import it.moneyverse.core.boot.KafkaAutoConfiguration;
import it.moneyverse.core.boot.SecurityAutoConfiguration;
import it.moneyverse.core.boot.UserServiceGrpcClientAutoConfiguration;
import it.moneyverse.test.enums.TestModelStrategyEnum;
import it.moneyverse.test.utils.helper.MapperTestHelper;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
class BudgetCustomRepositoryImplTest {

    @Autowired
    EntityManager entityManager;
    @Autowired BudgetCustomRepositoryImpl customRepository;

    static BudgetTestContext testContext;
    static List<Budget> budgets;

    @BeforeAll
    public static void beforeAll() {
        testContext =
            BudgetTestContext.builder()
                .withStrategy(TestModelStrategyEnum.RANDOM)
                .withTestUsers()
                .withTestBudgets()
                .build();
        budgets = MapperTestHelper.map(testContext.getModel().getBudgets(), Budget.class).stream().toList();
        budgets.forEach(budget -> budget.setBudgetId(null));
    }

    @BeforeEach
    public void setup() {
        for (Budget budget : budgets) {
            entityManager.persist(budget);
        }
        entityManager.flush();
    }

    @Test
    void givenCriteria_thenReturnFilteredBudgets() {
        BudgetCriteria criteria = new BudgetCriteriaRandomGenerator(testContext).generate();
        List<Budget> expected = testContext.filterBudgets(criteria).stream()
                .map(budget -> MapperTestHelper.map(budget, Budget.class))
                .toList();

        List<Budget> actual = customRepository.findBudgets(criteria);

        assertEquals(expected.size(), actual.size());
    }

}
