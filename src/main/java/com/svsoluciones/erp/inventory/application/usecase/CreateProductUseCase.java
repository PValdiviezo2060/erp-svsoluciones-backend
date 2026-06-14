package com.svsoluciones.erp.inventory.application.usecase;

import com.svsoluciones.erp.inventory.domain.model.Product;
import com.svsoluciones.erp.inventory.domain.port.ProductRepositoryPort;
import com.svsoluciones.erp.inventory.infrastructure.input.dto.AuditContext;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class CreateProductUseCase {
  private final ProductRepositoryPort productRepositoryPort;

  @Inject
  public CreateProductUseCase(ProductRepositoryPort productRepositoryPort) {
    this.productRepositoryPort = productRepositoryPort;
  }

  public Product execute(Product product, AuditContext auditContext) {
    productRepositoryPort.findBySku(product.getSku()).ifPresent(existingProduct -> {
      throw new IllegalStateException(
          "El producto con SKU '" + product.getSku() + "' ya se encuentra registrado.");
    });
    return productRepositoryPort.save(product, auditContext);
  }
}
