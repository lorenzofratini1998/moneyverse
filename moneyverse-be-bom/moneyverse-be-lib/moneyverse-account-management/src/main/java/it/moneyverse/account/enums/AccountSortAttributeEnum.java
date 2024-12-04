package it.moneyverse.account.enums;

import com.querydsl.core.types.dsl.ComparableExpressionBase;
import it.moneyverse.account.model.entities.QAccount;
import it.moneyverse.core.enums.SortAttribute;

public enum AccountSortAttributeEnum implements SortAttribute {

    ACCOUNT_NAME {
        @Override
        public ComparableExpressionBase<?> getField() {
            return QAccount.account.accountName;
        }

        @Override
        public Boolean isDefault() {
            return true;
        }
    },
    ACCOUNT_CATEGORY {
        @Override
        public ComparableExpressionBase<?> getField() {
            return QAccount.account.accountCategory;
        }

        @Override
        public Boolean isDefault() {
            return false;
        }
    },
    BALANCE {
        @Override
        public ComparableExpressionBase<?> getField() {
            return QAccount.account.balance;
        }

        @Override
        public Boolean isDefault() {
            return false;
        }
    },
    BALANCE_TARGET {
        @Override
        public ComparableExpressionBase<?> getField() {
            return QAccount.account.balanceTarget;
        }

        @Override
        public Boolean isDefault() {
            return false;
        }
    },
    USERNAME {
        @Override
        public ComparableExpressionBase<?> getField() {
            return QAccount.account.username;
        }

        @Override
        public Boolean isDefault() {
            return false;
        }
    }
}
