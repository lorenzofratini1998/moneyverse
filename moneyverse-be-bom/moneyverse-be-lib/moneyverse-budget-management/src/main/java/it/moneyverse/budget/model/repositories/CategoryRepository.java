package it.moneyverse.budget.model.repositories;

import it.moneyverse.budget.model.entities.Category;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {
  Boolean existsByUserIdAndCategoryName(UUID userId, String categoryName);

  List<Category> findCategoriesByUserId(UUID userId, Pageable pageable);

  List<Category> findCategoriesByUserId(UUID userId);

  boolean existsByUserIdAndCategoryId(UUID userId, UUID categoryId);

  boolean existsByCategoryId(UUID categoryId);
}
