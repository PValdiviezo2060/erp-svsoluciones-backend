package com.svsoluciones.erp.inventory.infrastructure.input.rest.exception;

import com.svsoluciones.erp.inventory.infrastructure.input.dto.ErrorResponseDetailsInner;
import java.util.List;

public class HeaderValidationException extends RuntimeException {
  private final List<ErrorResponseDetailsInner> details;

  public HeaderValidationException(List<ErrorResponseDetailsInner> details) {
    this.details = details;
  }

  public List<ErrorResponseDetailsInner> getDetails() {
    return details;
  }
}
