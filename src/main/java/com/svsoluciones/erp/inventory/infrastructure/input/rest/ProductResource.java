package com.svsoluciones.erp.inventory.infrastructure.input.rest;

import com.svsoluciones.erp.inventory.application.usecase.*;
import com.svsoluciones.erp.inventory.domain.model.Product;
import com.svsoluciones.erp.inventory.domain.model.ProductPage;
import com.svsoluciones.erp.inventory.infrastructure.input.dto.*;
import com.svsoluciones.erp.inventory.infrastructure.input.rest.api.InventoryApi;
import com.svsoluciones.erp.inventory.infrastructure.input.rest.filter.ValidateHeaders;
import com.svsoluciones.erp.inventory.infrastructure.input.rest.mapper.ProductRestMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import org.jspecify.annotations.NonNull;

@ApplicationScoped
@ValidateHeaders
public class ProductResource implements InventoryApi {
  @Inject
  CreateProductUseCase createProductUseCase;
  @Inject
  ProductRestMapper productRestMapper;
  @Inject
  GetProductBySkuUseCase getProductBySkuUseCase;
  @Inject
  ListProductsUseCase listProductsUseCase;
  @Inject
  PatchProductUseCase patchProductUseCase;
  @Inject
  DeleteProductUseCase deleteProductUseCase;

  @Override
  public Response createProduct(String requestId, String callerName, String appCode, ProductRequest productRequest) {

    AuditContext auditContext = new AuditContext(null, requestId, callerName, appCode);
    var productMapper = productRestMapper.toDomainRequest(productRequest);

    Product savedProduct = createProductUseCase.execute(productMapper, auditContext);
    var productResponse = productRestMapper.toResponse(savedProduct);
    return Response.status(Response.Status.CREATED).entity(productResponse).build();
  }

  @Override
  public Response deleteProductBySku(String requestId, String callerName, String appCode, String sku) {
    deleteProductUseCase.execute(sku);
    return Response.noContent().build();
  }

  @Override
  public Response getProductBySku(String requestId, String callerName, String appCode, String sku) {
    Product foundProduct = getProductBySkuUseCase.execute(sku);
    ProductResponse response = productRestMapper.toResponse(foundProduct);
    return Response.ok(response).build();
  }

  @Override
  public Response listProducts(String requestId, String callerName, String appCode, Integer page, Integer size) {
    ProductPage productPage = listProductsUseCase.execute(page, size);
    ProductPageResponse response = productRestMapper.toPageResponse(productPage);
    return Response.ok(response).build();
  }

  @Override
  public Response patchProductBySku(String requestId, String callerName, String appCode, String sku, ProductPatchRequest productPatchRequest) {
    patchProductUseCase.execute(sku, productPatchRequest);
    return Response.noContent().build();
  }
}
