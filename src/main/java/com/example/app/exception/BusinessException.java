package com.example.app.exception;

import lombok.Getter;
/**
 * Exceção base de regras de negócio.
 *
 * Carrega um código semântico e uma mensagem para diagnóstico,
 * servindo de base para exceções específicas do domínio.
 */
public class BusinessException extends RuntimeException {
  @Getter
  private final String code;

  /** Construtor com código semântico e mensagem. */
  public BusinessException(String code, String message) {
    super(message);
    this.code = code;
  }

  /** Código semântico do erro. */
  public String getCode() {
    return code;
  }
}