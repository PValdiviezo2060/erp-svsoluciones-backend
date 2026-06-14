package com.svsoluciones.erp.inventory.application.usecase;

import com.svsoluciones.erp.inventory.domain.exception.ProductNotFoundException;
import com.svsoluciones.erp.inventory.domain.port.ProductRepositoryPort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class DeleteProductUseCase {
  private static final Logger log = LoggerFactory.getLogger(DeleteProductUseCase.class);
  @Inject
  ProductRepositoryPort repository;

  @Transactional
  public void execute(String sku) {
    if (!repository.existsBySku(sku)) {
      log.info("Product with SKU: {}", sku);
      throw new ProductNotFoundException("No se puede eliminar, producto no existe: " + sku);
    }
    log.info("Eliminando producto con SKU: {}", repository.existsBySku(sku));
    repository.delete(sku);
  }
}
