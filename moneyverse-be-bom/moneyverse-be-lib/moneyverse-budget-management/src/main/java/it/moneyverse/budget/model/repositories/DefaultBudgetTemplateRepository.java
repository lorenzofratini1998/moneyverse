package it.moneyverse.budget.model.repositories;

import it.moneyverse.budget.model.entities.DefaultBudgetTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DefaultBudgetTemplateRepository extends JpaRepository<DefaultBudgetTemplate, Long> {
}
