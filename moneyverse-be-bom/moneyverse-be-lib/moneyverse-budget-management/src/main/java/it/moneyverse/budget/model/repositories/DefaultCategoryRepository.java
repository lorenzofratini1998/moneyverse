package it.moneyverse.budget.model.repositories;

import it.moneyverse.budget.model.entities.DefaultCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DefaultCategoryRepository extends JpaRepository<DefaultCategory, Long> {}
