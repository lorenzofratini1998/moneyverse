package it.moneyverse.budget.model.repositories;

import it.moneyverse.budget.model.entities.Category;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {
  Boolean existsByUserIdAndCategoryName(UUID userId, String categoryName);

  List<Category> findCategoriesByUserId(UUID userId, Pageable pageable);

  List<Category> findCategoriesByUserId(UUID userId);

  boolean existsByUserIdAndCategoryId(UUID userId, UUID categoryId);

  boolean existsByCategoryId(UUID categoryId);

  @Query(
      value =
          """
          WITH RECURSIVE category_tree AS (
              -- Start with the root categories (those with no parent)
              SELECT
                  p.*
              FROM CATEGORIES p
              WHERE USER_ID = :userId
                AND PARENT_ID IS NULL
            UNION ALL
              -- Recursively join to get subcategories
              SELECT
                  c.*
              FROM CATEGORIES c
              INNER JOIN category_tree ct ON c.PARENT_ID = ct.CATEGORY_ID
          )
          SELECT * FROM category_tree
          """,
      nativeQuery = true)
  List<Category> findCategoryTreeByUserId(UUID userId);
}
