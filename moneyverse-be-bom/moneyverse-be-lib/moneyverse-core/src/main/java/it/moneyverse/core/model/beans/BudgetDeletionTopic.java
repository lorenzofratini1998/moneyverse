package it.moneyverse.core.model.beans;

import org.apache.kafka.clients.admin.NewTopic;

public class BudgetDeletionTopic extends NewTopic {

    public static final String TOPIC = "budget-deletion-topic";

    public BudgetDeletionTopic() {
        super(TOPIC, 1, (short) 1);
    }
}
