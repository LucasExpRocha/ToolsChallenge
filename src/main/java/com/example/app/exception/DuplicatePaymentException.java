package com.example.app.exception;

/**
 * Exceção para tentativa de processamento duplicado (mesmo `externalId`).
 */
public class DuplicatePaymentException extends PaymentProcessingException {
  public DuplicatePaymentException(String code, String message) {
    super(code, message);
  }
}