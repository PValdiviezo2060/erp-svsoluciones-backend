package com.svsoluciones.erp.infrastructure.output.persistence.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Entity
@Table(name = "products")
public class ProductEntity extends PanacheEntityBase {
    @Id
    @SequenceGenerator(name = "products_seq", sequenceName = "products_seq", allocationSize = 50)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "products_seq")
    public Long id;

    @Column(name = "tenant_id")
    public Long tenantId; // Opcional por ahora

    @Column(unique = true, nullable = false, length = 100)
    public String sku;

    @Column(nullable = false, length = 255)
    public String name;

    @Column(name = "expected_margin", nullable = false)
    public BigDecimal expectedMargin = new BigDecimal("30.00");

    @Column(name = "wholesale_price", nullable = false)
    public BigDecimal wholesalePrice = BigDecimal.ZERO;

    @Column(name = "retail_price", nullable = false)
    public BigDecimal retailPrice = BigDecimal.ZERO;

    @Column(name = "allow_negative_stock", nullable = false)
    public Boolean allowNegativeStock = false;

    @Version
    @Column(nullable = false)
    public Integer version = 0;

    @Column(name = "created_at", insertable = false, updatable = false)
    public ZonedDateTime createdAt;
}
