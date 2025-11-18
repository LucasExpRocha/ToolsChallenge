package com.example.app.dto.payment;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Dados de entrada do pagamento: cartão, id, descrição e forma de pagamento.
 */
@Data
public class PaymentRequest {
  @JsonProperty("cartao")
  private String cartao;
  @JsonProperty("id")
  private String id;
  @JsonProperty("descricao")
  private PaymentDescricao descricao;
  @JsonProperty("formaPagamento")
  private PaymentFormaPagamento formaPagamento;
}