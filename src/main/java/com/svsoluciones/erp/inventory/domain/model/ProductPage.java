package com.svsoluciones.erp.inventory.domain.model;

import java.util.List;

public record ProductPage(List<Product> items,
    long totalElements,
    int totalPages,
    int currentPage) {
}
