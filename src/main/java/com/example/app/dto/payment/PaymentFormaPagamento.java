package com.example.app.dto.payment;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Forma de pagamento: tipo e número de parcelas.
 */
@Data
public class PaymentFormaPagamento {
  @JsonProperty("tipo")
  private String tipo;
  @JsonProperty("parcelas")
  private String parcelas;
}