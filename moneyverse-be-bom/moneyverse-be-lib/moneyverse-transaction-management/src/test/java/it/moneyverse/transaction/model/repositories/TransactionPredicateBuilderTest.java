package it.moneyverse.transaction.model.repositories;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import it.moneyverse.core.model.dto.BoundCriteria;
import it.moneyverse.core.model.dto.DateCriteria;
import it.moneyverse.test.utils.RandomUtils;
import it.moneyverse.transaction.model.dto.TransactionCriteria;
import it.moneyverse.transaction.model.entities.Tag;
import it.moneyverse.transaction.model.entities.Transaction;
import jakarta.persistence.criteria.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
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
class TransactionPredicateBuilderTest {

  @Test
  void testBuildPredicate(
      @Mock TransactionCriteria criteria,
      @Mock CriteriaBuilder cb,
      @Mock Root<Transaction> root,
      @Mock Predicate predicate,
      @Mock Join<Transaction, Tag> tagJoins,
      @Mock Path<Object> object) {
    UUID userId = RandomUtils.randomUUID();
    when(criteria.getAccounts()).thenReturn(Optional.of(List.of(RandomUtils.randomUUID())));
    when(criteria.getBudgets()).thenReturn(Optional.of(List.of(RandomUtils.randomUUID())));
    when(criteria.getAmount()).thenReturn(Optional.of(randomBoundCriteria()));
    when(criteria.getDate()).thenReturn(Optional.of(randomDateCriteria()));
    when(criteria.getTags()).thenReturn(Optional.of(List.of(RandomUtils.randomUUID())));

    when(cb.equal(any(), any(String.class))).thenReturn(predicate);
    when(cb.equal(any(), any(UUID.class))).thenReturn(predicate);
    when(cb.lessThanOrEqualTo(any(), any(BigDecimal.class))).thenReturn(predicate);
    when(cb.greaterThanOrEqualTo(any(), any(BigDecimal.class))).thenReturn(predicate);
    when(cb.lessThan(any(), any(LocalDate.class))).thenReturn(predicate);
    when(cb.greaterThanOrEqualTo(any(), any(LocalDate.class))).thenReturn(predicate);
    when(cb.and(any(Predicate[].class))).thenReturn(predicate);
    when(cb.or(any(Predicate[].class))).thenReturn(predicate);

    when(root.<Transaction, Tag>join(any(String.class))).thenReturn(tagJoins);
    when(tagJoins.get(any(String.class))).thenReturn(object);

    Predicate result = new TransactionPredicateBuilder(cb, root).build(userId, criteria);

    assertNotNull(result);
    verify(cb, times(4)).equal(any(), any(UUID.class));
    verify(cb, times(1)).lessThanOrEqualTo(any(), any(BigDecimal.class));
    verify(cb, times(1)).greaterThanOrEqualTo(any(), any(BigDecimal.class));
    verify(cb, times(1)).lessThanOrEqualTo(any(), any(LocalDate.class));
    verify(cb, times(1)).greaterThanOrEqualTo(any(), any(LocalDate.class));
    verify(cb, times(2)).and(any(Predicate[].class));
    verify(cb, times(2)).or(any(Predicate[].class));
  }

  private BoundCriteria randomBoundCriteria() {
    BoundCriteria bound = new BoundCriteria();
    bound.setUpper(RandomUtils.randomBigDecimal());
    bound.setLower(RandomUtils.randomBigDecimal());
    return bound;
  }

  private DateCriteria randomDateCriteria() {
    DateCriteria date = new DateCriteria();
    LocalDate lower = RandomUtils.randomLocalDate(2024, 2025);
    date.setStart(lower);
    date.setEnd(lower.plusMonths(3));
    return date;
  }
}
