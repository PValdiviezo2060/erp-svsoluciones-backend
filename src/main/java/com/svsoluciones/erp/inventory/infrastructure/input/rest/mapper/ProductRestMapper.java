package com.svsoluciones.erp.inventory.infrastructure.input.rest.mapper;

import com.svsoluciones.erp.inventory.domain.model.Product;
import com.svsoluciones.erp.inventory.domain.model.ProductPage;
import com.svsoluciones.erp.inventory.infrastructure.input.dto.ProductPageResponse;
import com.svsoluciones.erp.inventory.infrastructure.input.dto.ProductRequest;
import com.svsoluciones.erp.inventory.infrastructure.input.dto.ProductResponse;
import com.svsoluciones.erp.inventory.infrastructure.output.persistence.entity.ProductEntity;
import org.mapstruct.*;

@Mapper(componentModel = MappingConstants.ComponentModel.CDI,
    typeConversionPolicy = ReportingPolicy.IGNORE)
public interface ProductRestMapper {
  Product toDomain(ProductEntity entity);

  ProductResponse toResponse(Product product);

  ProductPageResponse toPageResponse(ProductPage productPage);

  void updateEntityFromDomain(Product product, @MappingTarget ProductEntity entity);

  ProductEntity toEntity(Product product);

  Product toDomainRequest(ProductRequest entity);
}
