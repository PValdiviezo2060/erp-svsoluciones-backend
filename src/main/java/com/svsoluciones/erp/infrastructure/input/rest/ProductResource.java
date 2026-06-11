package com.svsoluciones.erp.infrastructure.input.rest;

import com.svsoluciones.erp.infrastructure.output.persistence.entity.ProductEntity;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/api/v1/products")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProductResource {// Obtener todos los productos

    @GET
    public List<ProductEntity> getAll() {
        return ProductEntity.listAll();
    }

    @POST
    @Transactional
    public Response create(ProductEntity product) {
        product.persist();

        return Response.status(Response.Status.CREATED).entity(product).build();
    }
}
