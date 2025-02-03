package it.moneyverse.budget.model.repositories;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import it.moneyverse.budget.model.dto.BudgetCriteria;
import it.moneyverse.budget.model.entities.Budget;
import it.moneyverse.core.model.dto.BoundCriteria;
import it.moneyverse.test.utils.RandomUtils;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.math.BigDecimal;
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
      @Mock Predicate predicate) {
    when(criteria.getUserId()).thenReturn(Optional.of(RandomUtils.randomUUID()));
    when(criteria.getAmount()).thenReturn(Optional.of(randomBoundCriteria()));
    when(criteria.getBudgetLimit()).thenReturn(Optional.of(randomBoundCriteria()));
    when(criteria.getCurrency()).thenReturn(Optional.of(RandomUtils.randomString(3).toUpperCase()));

    when(cb.equal(any(), any(String.class))).thenReturn(predicate);
    when(cb.greaterThan(any(), any(BigDecimal.class))).thenReturn(predicate);
    when(cb.lessThan(any(), any(BigDecimal.class))).thenReturn(predicate);
    when(cb.and(any(Predicate[].class))).thenReturn(predicate);

    Predicate result = new BudgetPredicateBuilder(cb, root).build(criteria);

    assertNotNull(result);
    verify(cb, times(1)).equal(any(), any(UUID.class));
    verify(cb, times(1)).equal(any(), any(String.class));
    verify(cb, times(2)).greaterThan(any(), any(BigDecimal.class));
    verify(cb, times(2)).lessThan(any(), any(BigDecimal.class));
    verify(cb, times(1)).and(any(Predicate[].class));
  }

  private BoundCriteria randomBoundCriteria() {
    BoundCriteria bound = new BoundCriteria();
    bound.setUpper(RandomUtils.randomBigDecimal());
    bound.setLower(RandomUtils.randomBigDecimal());
    return bound;
  }
}
