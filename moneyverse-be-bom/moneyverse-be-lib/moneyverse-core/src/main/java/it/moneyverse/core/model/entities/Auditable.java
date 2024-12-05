package it.moneyverse.core.model.entities;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class Auditable implements AuditableModel {

  @CreatedBy
  @Column(name = "CREATED_BY", nullable = false, updatable = false)
  private String createdBy = "TMP"; // TODO: temporary: replace with the authenticated user

  @CreatedDate
  @Column(
      name = "CREATED_AT",
      nullable = false,
      updatable = false
  )
  @CreationTimestamp
  private LocalDateTime createdAt;

  @LastModifiedBy
  @Column(name = "UPDATED_BY")
  private String updatedBy = "TMP"; // TODO: temporary: replace with the authenticated user

  @LastModifiedDate
  @Column(name = "UPDATED_AT")
  @UpdateTimestamp
  private LocalDateTime updatedAt;

  @Override
  public String getCreatedBy() {
    return createdBy;
  }

  public void setCreatedBy(String createdBy) {
    this.createdBy = createdBy;
  }

  @Override
  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  @Override
  public String getUpdatedBy() {
    return updatedBy;
  }

  public void setUpdatedBy(String updatedBy) {
    this.updatedBy = updatedBy;
  }

  @Override
  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(LocalDateTime updatedAt) {
    this.updatedAt = updatedAt;
  }
}
