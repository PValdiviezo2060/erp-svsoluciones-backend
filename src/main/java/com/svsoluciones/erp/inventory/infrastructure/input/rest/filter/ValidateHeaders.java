package com.svsoluciones.erp.inventory.infrastructure.input.rest.filter;

import jakarta.ws.rs.NameBinding;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@NameBinding
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidateHeaders {
}
