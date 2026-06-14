package com.svsoluciones.erp.inventory.infrastructure.input.rest.exception;

import com.svsoluciones.erp.inventory.domain.exception.ProductNotFoundException;
import com.svsoluciones.erp.inventory.infrastructure.input.dto.ErrorResponse;
import com.svsoluciones.erp.inventory.infrastructure.input.dto.ErrorResponseDetailsInner;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.List;
import java.util.stream.Collectors;

@Provider
public class GlobalExceptionHandler implements ExceptionMapper<Exception> {

  @Override
  public Response toResponse(Exception exception) {

    if (exception instanceof HeaderValidationException ex) {
      return buildResponse(Response.Status.BAD_REQUEST, "Validación de cabeceras fallida",
          ex.getDetails());
    }
    if (exception instanceof ProductNotFoundException ex) {
      return buildResponse(Response.Status.NOT_FOUND, ex.getMessage(), null);
    }
    if (exception instanceof ConstraintViolationException ex) {
      List<ErrorResponseDetailsInner> details = ex.getConstraintViolations().stream()
          .map(v -> {
            ErrorResponseDetailsInner detail = new ErrorResponseDetailsInner();
            detail.setCode("VALIDATION_ERROR");
            detail.setType("FIELD_INVALID");
            detail.setDescription(v.getPropertyPath() + " " + v.getMessage());
            return detail;
          })
          .collect(Collectors.toList());

      return buildResponse(Response.Status.BAD_REQUEST, "Validación de cuerpo fallida", details);
    }
    return buildResponse(Response.Status.INTERNAL_SERVER_ERROR, "Error interno", null);
  }

  private Response buildResponse(Response.Status status, String message, List<ErrorResponseDetailsInner> details) {
    ErrorResponse errorResponse = new ErrorResponse();
    errorResponse.setStatus(status.getStatusCode());
    errorResponse.setMessage(message);
    errorResponse.setDetails(details);
    return Response.status(status).entity(errorResponse).build();
  }
}