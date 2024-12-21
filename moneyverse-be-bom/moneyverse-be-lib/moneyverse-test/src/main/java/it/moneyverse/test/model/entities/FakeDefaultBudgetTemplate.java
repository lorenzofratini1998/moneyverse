package it.moneyverse.test.model.entities;

import it.moneyverse.core.model.entities.DefaultBudgetTemplateModel;
import it.moneyverse.test.utils.RandomUtils;

public class FakeDefaultBudgetTemplate implements DefaultBudgetTemplateModel {

    private final Long id;
    private final String name;
    private final String description;

    public FakeDefaultBudgetTemplate(Integer counter) {
        counter++;
        this.id = (long) counter;
        this.name = "Default Budget %s".formatted(counter);
        this.description = RandomUtils.randomString(30);
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }
}
