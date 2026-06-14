package com.svsoluciones.erp.inventory.domain.model;

import com.svsoluciones.erp.inventory.infrastructure.output.persistence.entity.ProductEntity;
import java.math.BigDecimal;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product extends ProductEntity {
  private Long id;
  private String sku;
  private String name;
  private BigDecimal expectedMargin;
  private BigDecimal wholesalePrice;
  private BigDecimal retailPrice;
  private Boolean allowNegativeStock;
}
