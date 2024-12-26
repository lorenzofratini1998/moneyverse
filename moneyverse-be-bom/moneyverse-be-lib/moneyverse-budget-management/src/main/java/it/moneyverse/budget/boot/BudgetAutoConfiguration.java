package it.moneyverse.budget.boot;

import it.moneyverse.core.model.beans.BudgetDeletionTopic;
import it.moneyverse.core.model.beans.UserCreationTopic;
import it.moneyverse.core.model.beans.UserDeletionTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BudgetAutoConfiguration {

    @Bean
    public BudgetDeletionTopic budgetDeletionTopic() {
        return new BudgetDeletionTopic();
    }

    @Bean
    public UserDeletionTopic userDeletionTopic() {
        return new UserDeletionTopic();
    }

    @Bean
    public UserCreationTopic userCreationTopic() {
        return new UserCreationTopic();
    }
}
