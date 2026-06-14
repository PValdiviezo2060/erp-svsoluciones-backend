package com.svsoluciones.erp.inventory.infrastructure.input.dto;

public record AuditContext(String authorization,
    String requestId,
    String callerName,
    String appCode) {
}
