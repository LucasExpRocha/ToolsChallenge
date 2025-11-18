package com.example.app.exception;

/**
 * Exceção lançada ao falhar criar ou persistir a transação de pagamento.
 */
public class PaymentCreationException extends PaymentProcessingException {
  public PaymentCreationException(String code, String message) {
    super(code, message);
  }
}