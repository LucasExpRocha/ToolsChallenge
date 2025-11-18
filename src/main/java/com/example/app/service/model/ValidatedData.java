package com.example.app.service.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Dados normalizados e validados de uma requisição de pagamento.
 *
 * Contém os campos prontos para uso na criação de uma transação,
 * garantindo consistência tipada e imutabilidade.
 */
@Getter
@AllArgsConstructor
public class ValidatedData {
  /** Identificador externo único da transação. */
  private final String id;
  /** Número do cartão ou identificador da transação original. */
  private final String transacao;
  /** Valor da transação em unidade monetária. */
  private final BigDecimal valor;
  /** Data e hora da transação. */
  private final LocalDateTime dataHora;
  /** Tipo de pagamento normalizado. */
  private final String tipo;
  /** Quantidade de parcelas (se aplicável). */
  private final Integer parcelas;
  /** Nome do estabelecimento sanitizado em UTF-8. */
  private final String estabelecimento;
}