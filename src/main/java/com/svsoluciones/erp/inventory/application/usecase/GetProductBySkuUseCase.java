package com.svsoluciones.erp.inventory.application.usecase;

import com.svsoluciones.erp.inventory.domain.model.Product;
import com.svsoluciones.erp.inventory.domain.port.ProductRepositoryPort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class GetProductBySkuUseCase {
  private final ProductRepositoryPort repository;

  @Inject
  public GetProductBySkuUseCase(ProductRepositoryPort repository) {
    this.repository = repository;
  }

  public Product execute(String sku) {
    return repository.findBySku(sku)
        .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado con SKU: " + sku));
  }
}
