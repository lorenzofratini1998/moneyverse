package it.moneyverse.budget.model.entities;

import it.moneyverse.core.model.entities.Auditable;
import jakarta.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(
    name = "CATEGORIES",
    uniqueConstraints = {@UniqueConstraint(columnNames = {"USER_ID", "CATEGORY_NAME"})})
public class Category extends Auditable implements Serializable {

  @Serial private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "CATEGORY_ID")
  private UUID categoryId;

  @Column(name = "USER_ID", nullable = false)
  private UUID userId;

  @Column(name = "CATEGORY_NAME", nullable = false)
  private String categoryName;

  @Column(name = "CATEGORY_DESCRIPTION")
  private String description;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "PARENT_ID")
  private Category parentCategory;

  @OneToMany(mappedBy = "parentCategory", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Category> subCategories = new ArrayList<>();

  @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Budget> budgets = new ArrayList<>();

  public UUID getCategoryId() {
    return categoryId;
  }

  public void setCategoryId(UUID budgetId) {
    this.categoryId = budgetId;
  }

  public UUID getUserId() {
    return userId;
  }

  public void setUserId(UUID userId) {
    this.userId = userId;
  }

  public String getCategoryName() {
    return categoryName;
  }

  public void setCategoryName(String budgetName) {
    this.categoryName = budgetName;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Category getParentCategory() {
    return parentCategory;
  }

  public void setParentCategory(Category parentCategory) {
    this.parentCategory = parentCategory;
  }

  public List<Category> getSubCategories() {
    return subCategories;
  }

  public void setSubCategories(List<Category> subCategories) {
    this.subCategories = subCategories;
  }

  public List<Budget> getBudgets() {
    return budgets;
  }

  public void setBudgets(List<Budget> budgets) {
    this.budgets = budgets;
  }
}
