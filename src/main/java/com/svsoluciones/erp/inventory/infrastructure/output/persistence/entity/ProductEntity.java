package com.svsoluciones.erp.inventory.infrastructure.output.persistence.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.Version;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name = "products")
@Getter
@Setter
public class ProductEntity extends PanacheEntityBase {
  @Id
  @SequenceGenerator(name = "products_seq", sequenceName = "products_seq", allocationSize = 50)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "products_seq")
  public Long id;

  @Column(name = "tenant_id")
  private Long tenantId;

  @Column(unique = true, nullable = false, length = 100)
  private String sku;

  @Column(nullable = false, length = 255)
  private String name;

  @Column(name = "expected_margin", nullable = false)
  private BigDecimal expectedMargin = new BigDecimal("30.00");

  @Column(name = "wholesale_price", nullable = false)
  private BigDecimal wholesalePrice = BigDecimal.ZERO;

  @Column(name = "retail_price", nullable = false)
  private BigDecimal retailPrice = BigDecimal.ZERO;

  @Column(name = "allow_negative_stock", nullable = false)
  private Boolean allowNegativeStock = false;

  @Version
  @Column(nullable = false)
  private Integer version = 0;

  @Column(name = "created_at", insertable = false, updatable = false)
  private ZonedDateTime createdAt;
}
