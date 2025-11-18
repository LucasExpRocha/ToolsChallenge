package com.example.app.exception;

/**
 * Exceção base para falhas no processamento de pagamentos.
 */
public class PaymentProcessingException extends BusinessException {
  public PaymentProcessingException(String code, String message) {
    super(code, message);
  }
}