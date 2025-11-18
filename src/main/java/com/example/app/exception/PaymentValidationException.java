package com.example.app.exception;

/**
 * Exceção para requisições de pagamento com dados inválidos.
 */
public class PaymentValidationException extends PaymentProcessingException {
  public PaymentValidationException(String code, String message) {
    super(code, message);
  }
}