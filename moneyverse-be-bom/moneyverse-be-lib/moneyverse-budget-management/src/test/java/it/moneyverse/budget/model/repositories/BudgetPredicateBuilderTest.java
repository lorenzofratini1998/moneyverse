package it.moneyverse.budget.model.repositories;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import it.moneyverse.budget.model.dto.BudgetCriteria;
import it.moneyverse.budget.model.entities.Budget;
import it.moneyverse.budget.model.entities.Category;
import it.moneyverse.test.model.TestFactory;
import it.moneyverse.test.utils.RandomUtils;
import jakarta.persistence.criteria.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class BudgetPredicateBuilderTest {

  @Test
  void testBuildPredicate(
      @Mock BudgetCriteria criteria,
      @Mock CriteriaBuilder cb,
      @Mock Root<Budget> root,
      @Mock Predicate predicate,
      @Mock Join<Budget, Category> categoryJoins,
      @Mock Path<Object> objectPath) {
    UUID userId = RandomUtils.randomUUID();
    when(criteria.getAmount()).thenReturn(Optional.of(TestFactory.fakeBoundCriteria()));
    when(criteria.getBudgetLimit()).thenReturn(Optional.of(TestFactory.fakeBoundCriteria()));
    when(criteria.getCurrency()).thenReturn(Optional.of(RandomUtils.randomCurrency()));
    when(criteria.getDate()).thenReturn(Optional.of(TestFactory.fakeDateCriteria()));

    when(cb.equal(any(), any(String.class))).thenReturn(predicate);
    when(cb.greaterThan(any(), any(BigDecimal.class))).thenReturn(predicate);
    when(cb.lessThan(any(), any(BigDecimal.class))).thenReturn(predicate);
    when(cb.lessThanOrEqualTo(any(), any(LocalDate.class))).thenReturn(predicate);
    when(cb.greaterThanOrEqualTo(any(), any(LocalDate.class))).thenReturn(predicate);
    when(cb.and(any(Predicate[].class))).thenReturn(predicate);

    when(root.<Budget, Category>join(any(String.class))).thenReturn(categoryJoins);
    when(categoryJoins.get(any(String.class))).thenReturn(objectPath);

    Predicate result = new BudgetPredicateBuilder(cb, root).build(userId, criteria);

    assertNotNull(result);
    verify(cb, times(1)).equal(any(), any(String.class));
    verify(cb, times(2)).greaterThan(any(), any(BigDecimal.class));
    verify(cb, times(2)).lessThan(any(), any(BigDecimal.class));
    verify(cb, times(1)).lessThanOrEqualTo(any(), any(LocalDate.class));
    verify(cb, times(1)).greaterThanOrEqualTo(any(), any(LocalDate.class));
    verify(cb, times(1)).and(any(Predicate[].class));
  }
}
