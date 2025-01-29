package it.moneyverse.account.model.repositories;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import it.moneyverse.account.model.dto.AccountCriteria;
import it.moneyverse.account.model.entities.Account;
import it.moneyverse.account.model.entities.Account_;
import it.moneyverse.core.model.dto.BoundCriteria;
import it.moneyverse.test.utils.RandomUtils;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AccountPredicateBuilderTest {

  @Test
  void testBuildPredicate(
      @Mock AccountCriteria criteria,
      @Mock CriteriaBuilder cb,
      @Mock Root<Account> root,
      @Mock Predicate predicate,
      @Mock Path<Object> accountCategoryPath) {
    when(root.get(Account_.ACCOUNT_CATEGORY)).thenReturn(accountCategoryPath);
    when(criteria.getUsername()).thenReturn(Optional.of(RandomUtils.randomString(25)));
    when(criteria.getAccountCategory()).thenReturn(Optional.of(RandomUtils.randomString(15)));
    when(criteria.getCurrency()).thenReturn(Optional.of(RandomUtils.randomString(3)));
    when(criteria.getIsDefault()).thenReturn(Optional.of(RandomUtils.randomBoolean()));
    when(criteria.getBalance()).thenReturn(Optional.of(randomBoundCriteria()));
    when(criteria.getBalanceTarget()).thenReturn(Optional.of(randomBoundCriteria()));

    when(cb.equal(any(), any(String.class))).thenReturn(predicate);
    when(cb.equal(any(), any(Boolean.class))).thenReturn(predicate);
    when(cb.equal(any(), any(String.class))).thenReturn(predicate);
    when(cb.greaterThan(any(), any(BigDecimal.class))).thenReturn(predicate);
    when(cb.lessThan(any(), any(BigDecimal.class))).thenReturn(predicate);
    when(cb.and(any(Predicate[].class))).thenReturn(predicate);

    Predicate result = new AccountPredicateBuilder(cb, root).build(criteria);

    assertNotNull(result);
    verify(cb, times(3)).equal(any(), any(String.class));
    verify(cb, times(1)).equal(any(), any(Boolean.class));
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
