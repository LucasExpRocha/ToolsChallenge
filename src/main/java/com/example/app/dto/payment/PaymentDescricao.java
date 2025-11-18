package com.example.app.dto.payment;

import com.example.app.validator.Utf8Safe;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Descrição de entrada da transação: valor, data/hora e estabelecimento.
 */
@Data
public class PaymentDescricao {
  @JsonProperty("valor")
  private String valor;
  @JsonProperty("dataHora")
  private String dataHora;
  @Utf8Safe
  @JsonProperty("estabelecimento")
  private String estabelecimento;
}