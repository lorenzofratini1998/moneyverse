package it.moneyverse.budget.boot;

import it.moneyverse.core.model.beans.BudgetDeletionTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BudgetAutoConfiguration {

    @Bean
    public BudgetDeletionTopic budgetDeletionTopic() {
        return new BudgetDeletionTopic();
    }
}
