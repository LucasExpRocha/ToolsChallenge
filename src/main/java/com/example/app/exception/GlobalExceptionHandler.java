package com.example.app.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {
  @ExceptionHandler(MethodArgumentNotValidException.class)
  /**
   * Trata erros de validação provenientes de @Valid/@Validated.
   * Retorna 400 (Bad Request) com a primeira mensagem de campo.
   */
  public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException exception, WebRequest request) {
    String message = exception.getBindingResult().getFieldErrors().stream()
      .map(e -> e.getField() + ": " + e.getDefaultMessage())
      .findFirst().orElse("Validation error");
    ApiError error = buildError(HttpStatus.BAD_REQUEST, message, request);
    return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(BusinessException.class)
  /**
   * Trata exceções de regra de negócio.
   * Retorna 422 (Unprocessable Entity).
   */
  public ResponseEntity<ApiError> handleBusiness(BusinessException exception, WebRequest request) {
    ApiError error = buildError(HttpStatus.UNPROCESSABLE_ENTITY, exception.getMessage(), request);
    return new ResponseEntity<>(error, HttpStatus.UNPROCESSABLE_ENTITY);
  }

  @ExceptionHandler(PaymentValidationException.class)
  /**
   * Trata falhas de validação específicas de pagamento.
   * Retorna 400 (Bad Request).
   */
  public ResponseEntity<ApiError> handlePaymentValidation(PaymentValidationException exception, WebRequest request) {
    ApiError error = buildError(HttpStatus.BAD_REQUEST, exception.getMessage(), request);
    return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(DuplicatePaymentException.class)
  /**
   * Trata tentativas de processamento duplicado.
   * Retorna 409 (Conflict).
   */
  public ResponseEntity<ApiError> handleDuplicatePayment(DuplicatePaymentException exception, WebRequest request) {
    ApiError error = buildError(HttpStatus.CONFLICT, exception.getMessage(), request);
    return new ResponseEntity<>(error, HttpStatus.CONFLICT);
  }

  @ExceptionHandler(PaymentCreationException.class)
  /**
   * Trata falhas internas ao criar a transação.
   * Retorna 500 (Internal Server Error).
   */
  public ResponseEntity<ApiError> handlePaymentCreation(PaymentCreationException exception, WebRequest request) {
    ApiError error = buildError(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage(), request);
    return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(Exception.class)
  /**
   * Fallback para exceções não tratadas.
   * Retorna 500 (Internal Server Error).
   */
  public ResponseEntity<ApiError> handleGeneric(Exception exception, WebRequest request) {
    ApiError error = buildError(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage(), request);
    return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  /**
   * Constrói o payload padrão de erro.
   */
  private ApiError buildError(HttpStatus status, String message, WebRequest request) {
    ApiError error = new ApiError();
    error.setTimestamp(LocalDateTime.now());
    error.setStatus(status.value());
    error.setError(status.getReasonPhrase());
    error.setMessage(message);
    error.setPath(request.getDescription(false).replace("uri=", ""));
    return error;
  }
}