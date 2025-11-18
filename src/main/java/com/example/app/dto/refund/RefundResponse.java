package com.example.app.dto.refund;

import com.example.app.dto.payment.PaymentResponse;
import lombok.Data;

/**
 * Resposta do endpoint de estorno.
 *
 * Encapsula a estrutura `transacao` conforme o contrato da API.
 */
@Data
public class RefundResponse {
  /** Transação após o estorno. */
  private PaymentResponse transacao;
}