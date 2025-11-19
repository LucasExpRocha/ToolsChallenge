package com.example.app.dto.payment;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * Forma de pagamento: tipo e número de parcelas.
 */
@Data
public class PaymentFormaPagamento {
  @Schema(description = "Tipos permitidos: AVISTA, PARCELADO LOJA, PARCELADO EMISSOR")
  @JsonProperty("tipo")
  private String tipo;

  @Schema(description = "Quando tipo=AVISTA, deve ser exatamente '1'")
  @JsonProperty("parcelas")
  private String parcelas;
}