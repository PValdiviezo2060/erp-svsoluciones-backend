package com.svsoluciones.erp.inventory.infrastructure.output.persistence;

import static io.quarkus.hibernate.orm.panache.PanacheEntityBase.count;

import com.svsoluciones.erp.inventory.domain.model.Product;
import com.svsoluciones.erp.inventory.domain.model.ProductPage;
import com.svsoluciones.erp.inventory.domain.port.ProductRepositoryPort;
import com.svsoluciones.erp.inventory.infrastructure.input.dto.AuditContext;
import com.svsoluciones.erp.inventory.infrastructure.input.rest.mapper.ProductRestMapper;
import com.svsoluciones.erp.inventory.infrastructure.output.persistence.entity.ProductEntity;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
public class ProductRepositoryAdapter implements ProductRepositoryPort {
  @Inject
  ProductRestMapper mapper;

  @Override
  @Transactional
  public Product save(Product product, AuditContext auditContext) {
    var productEntity = mapper.toEntity(product);
    productEntity.persist();
    return mapper.toDomain(productEntity);
  }

  @Override
  public Optional<Product> findBySku(String sku) {
    return ProductEntity
        .find("sku", sku)
        .firstResultOptional()
        .map(entity -> mapper.toDomain((ProductEntity) entity));
  }

  @Override
  public ProductPage findAll(int page, int size) {
    var query = ProductEntity.findAll().page(page, size);
    List<Product> items = query.stream()
        .map(entity -> mapper.toDomain((ProductEntity) entity))
        .toList();
    return new ProductPage(items, query.count(), query.pageCount(), page);
  }

  @Override
  public void update(Product product) {
    ProductEntity.find("sku", product.getSku()).firstResultOptional()
        .map(ProductEntity.class::cast)
        .ifPresent(entity -> {
          mapper.updateEntityFromDomain(product, entity);
          entity.persist();
        });
  }

  @Override
  public void delete(String sku) {
    log.info("delete sku {}", sku);
    ProductEntity.delete("sku", sku);
  }

  @Override
  public boolean existsBySku(String sku) {
    log.info("Checking existence of product with SKU: {}", sku);
    return ProductEntity.count("sku", sku) > 0;
  }
}
