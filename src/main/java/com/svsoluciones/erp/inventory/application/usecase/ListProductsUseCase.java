package com.svsoluciones.erp.inventory.application.usecase;

import com.svsoluciones.erp.inventory.domain.model.ProductPage;
import com.svsoluciones.erp.inventory.domain.port.ProductRepositoryPort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class ListProductsUseCase {
  private final ProductRepositoryPort repository;

  @Inject
  public ListProductsUseCase(ProductRepositoryPort repository) {
    this.repository = repository;
  }

  public ProductPage execute(int page, int size) {
    int safeSize = switch (size) {
    case 5, 15, 25, 50 -> size;
    default -> 10;
  };

    int safePage = Math.max(page, 0);

    return repository.findAll(safePage, safeSize);
  }
}
