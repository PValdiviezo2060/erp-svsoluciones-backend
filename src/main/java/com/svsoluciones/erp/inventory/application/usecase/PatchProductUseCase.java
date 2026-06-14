package com.svsoluciones.erp.inventory.application.usecase;

import com.svsoluciones.erp.inventory.domain.exception.ProductNotFoundException;
import com.svsoluciones.erp.inventory.domain.port.ProductRepositoryPort;
import com.svsoluciones.erp.inventory.infrastructure.input.dto.ProductPatchRequest;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.util.Optional;

@ApplicationScoped
public class PatchProductUseCase {
  @Inject
  ProductRepositoryPort repository;

  @Transactional
  public void execute(String sku, ProductPatchRequest request) {
    repository.findBySku(sku)
        .map(product -> {
          Optional.ofNullable(request.getName()).ifPresent(product::setName);
          Optional.ofNullable(request.getWholesalePrice()).map(BigDecimal::valueOf).ifPresent(
              product::setWholesalePrice);
          Optional.ofNullable(request.getRetailPrice()).map(BigDecimal::valueOf).ifPresent(
              product::setRetailPrice);
          Optional.ofNullable(request.getAllowNegativeStock()).ifPresent(
              product::setAllowNegativeStock);
          return product;
        })
        .map(product -> {
          repository.update(product);
          return product;
        })
        .orElseThrow(() -> new ProductNotFoundException(
            "No se puede actualizar, producto no existe: " + sku));
  }
}
