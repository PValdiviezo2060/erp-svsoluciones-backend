package com.svsoluciones.erp.inventory.infrastructure.input.rest.filter;

import com.svsoluciones.erp.inventory.infrastructure.input.dto.ErrorResponseDetailsInner;
import com.svsoluciones.erp.inventory.infrastructure.input.rest.exception.HeaderValidationException;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.ext.Provider;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

@Provider
@ValidateHeaders
public class HeaderValidationFilter implements ContainerRequestFilter {
  private record ValidationRule(String headerName, String regex, String code, String type,
      String desc) {
  }
  private static final List<ValidationRule> RULES = List.of(
      new ValidationRule("request-id",
          "^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$",
          "INVALID_REQUEST_ID", "FORMAT_ERROR", "El 'request-id' debe ser un UUID válido."),
      new ValidationRule("app-code", "^[A-Z0-9_-]+$",
          "INVALID_APP_CODE", "FORMAT_ERROR", "El 'app-code' contiene caracteres no permitidos."),
      new ValidationRule("caller-name", "^[a-zA-Z0-9_ ]+$",
          "INVALID_CALLER", "FORMAT_ERROR", "El 'caller-name' solo permite alfanuméricos.")
  );

  @Override
  public void filter(ContainerRequestContext context) {
    List<ErrorResponseDetailsInner> errors = RULES.stream()
        .map(rule -> validate(context.getHeaderString(rule.headerName()), rule))
        .filter(Objects::nonNull)
        .toList();
    if (!errors.isEmpty()) {
      throw new HeaderValidationException(errors);
    }
  }

  private ErrorResponseDetailsInner validate(String value, ValidationRule rule) {
    return isMatch(value, rule.regex()) ? null : createErrorDetail(value, rule);
  }

  private boolean isMatch(String value, String regex) {
    return value != null && Pattern.matches(regex, value);
  }

  private ErrorResponseDetailsInner createErrorDetail(String value, ValidationRule rule) {
    ErrorResponseDetailsInner detail = new ErrorResponseDetailsInner();
    detail.setCode(rule.code());
    detail.setType(rule.type());
    detail.setDescription(
        String.format("%s (Recibido: %s)", rule.desc(), value == null ? "nulo" : value));
    return detail;
  }
}