package com.example.app.service.validation;

import com.example.app.service.model.ValidatedData;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Resultado da validação de uma requisição de pagamento.
 *
 * Fornece fábricas estáticas para sucesso (com {@link ValidatedData})
 * e erro (com mensagem descritiva), mantendo a API simples.
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ValidationResult {
  /** Indica se a validação foi bem-sucedida. */
  private final boolean ok;
  /** Mensagem de erro quando a validação falha, ou {@code null} quando ok. */
  private final String erro;
  /** Dados validados disponíveis quando {@code ok} é verdadeiro. */
  private final ValidatedData dados;

  /** Cria um resultado de validação bem-sucedido contendo os dados validados. */
  public static ValidationResult ok(ValidatedData dados) {
    return new ValidationResult(true, null, dados);
  }

  /** Cria um resultado de validação com falha contendo a mensagem de erro. */
  public static ValidationResult erro(String erro) {
    return new ValidationResult(false, erro, null);
  }
}