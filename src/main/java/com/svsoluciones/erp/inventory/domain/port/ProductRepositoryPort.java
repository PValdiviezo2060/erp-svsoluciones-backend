package com.svsoluciones.erp.inventory.domain.port;

import com.svsoluciones.erp.inventory.domain.model.Product;
import com.svsoluciones.erp.inventory.domain.model.ProductPage;
import com.svsoluciones.erp.inventory.infrastructure.input.dto.AuditContext;
import java.util.Optional;

public interface ProductRepositoryPort {
  Product save(Product product, AuditContext auditContext);

  Optional<Product> findBySku(String sku);

  ProductPage findAll(int page, int size);

  void update(Product product);

  void delete(String sku);

  boolean existsBySku(String sku);
}
