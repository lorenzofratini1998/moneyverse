package it.moneyverse.budget.enums;

import it.moneyverse.budget.model.entities.Budget_;
import it.moneyverse.core.enums.SortAttribute;

public enum BudgetSortAttributeEnum implements SortAttribute {
    BUDGET_NAME {
        @Override
        public String getField() {
            return Budget_.BUDGET_NAME;
        }

        @Override
        public Boolean isDefault() {
            return true;
        }
    },
    AMOUNT {
        @Override
        public String getField() {
            return Budget_.AMOUNT;
        }

        @Override
        public Boolean isDefault() {
            return false;
        }
    },
    BUDGET_LIMIT {
        @Override
        public String getField() {
            return Budget_.BUDGET_LIMIT;
        }

        @Override
        public Boolean isDefault() {
            return false;
        }
    }
}
