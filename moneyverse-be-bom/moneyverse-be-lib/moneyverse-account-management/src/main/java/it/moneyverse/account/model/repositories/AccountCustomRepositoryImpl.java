package it.moneyverse.account.model.repositories;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.ComparableExpressionBase;
import it.moneyverse.account.enums.AccountSortAttributeEnum;
import it.moneyverse.account.model.dto.AccountCriteria;
import it.moneyverse.account.model.entities.Account;
import it.moneyverse.account.model.entities.QAccount;
import it.moneyverse.core.enums.SortAttribute;
import it.moneyverse.core.model.dto.SortCriteria;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

@Repository
public class AccountCustomRepositoryImpl extends QuerydslRepositorySupport
    implements AccountCustomRepository {

  public AccountCustomRepositoryImpl() {
    super(Account.class);
  }

  @Override
  public List<Account> findAccounts(AccountCriteria param) {
    QAccount account = QAccount.account;
    BooleanBuilder predicate = new BooleanBuilder();
    param.getUsername().ifPresent(username -> predicate.and(account.username.eq(username)));
    param
        .getBalance()
        .ifPresent(
            balance -> {
              balance.getLower().ifPresent(lower -> predicate.and(account.balance.gt(lower)));
              balance.getUpper().ifPresent(upper -> predicate.and(account.balance.lt(upper)));
            });
    param
        .getBalanceTarget()
        .ifPresent(
            balanceTarget -> {
              balanceTarget
                  .getLower()
                  .ifPresent(lower -> predicate.and(account.balanceTarget.gt(lower)));
              balanceTarget
                  .getUpper()
                  .ifPresent(upper -> predicate.and(account.balanceTarget.lt(upper)));
            });
    param
        .getAccountCategory()
        .ifPresent(accountCategory -> predicate.and(account.accountCategory.eq(accountCategory)));
    param.getIsDefault().ifPresent(isDefault -> predicate.and(account.isDefault.eq(isDefault)));
    return from(account)
        .where(predicate)
        .offset(param.getPage().getOffset())
        .limit(param.getPage().getLimit())
        .orderBy(getOrderSpecifier(param.getSort()))
        .fetch();
  }

  private OrderSpecifier<?> getOrderSpecifier(SortCriteria<AccountSortAttributeEnum> sortCriteria) {
    SortAttribute attribute = sortCriteria.getAttribute();
    ComparableExpressionBase<?> field = attribute.getField();

    return sortCriteria.getDirection() == Sort.Direction.ASC ? field.asc() : field.desc();
  }
}
