package com.example.app.service.lookup;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Resultado da consulta por pagamento no repositório.
 *
 * Indica se a transação foi encontrada para um dado {@code externalId}.
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class LookupResult {
  /** Indica se o registro foi encontrado. */
  private final boolean found;

  /** Fábrica para resultado positivo (registro encontrado). */
  public static LookupResult found() {
    return new LookupResult(true);
  }

  /** Fábrica para resultado negativo (registro não encontrado). */
  public static LookupResult notFound() {
    return new LookupResult(false);
  }
}