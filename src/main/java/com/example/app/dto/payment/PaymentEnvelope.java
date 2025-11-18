package com.example.app.dto.payment;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PaymentEnvelope {
  @JsonProperty("transacao")
  private PaymentRequest transacao;
}