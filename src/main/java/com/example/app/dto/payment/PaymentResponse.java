package com.example.app.dto.payment;

import lombok.Data;

/**
 * DTO de resposta do processamento de pagamento.
 *
 * Consolida os dados ecoados ao cliente (cartão e id), a descrição
 * do resultado e a forma de pagamento.
 */
@Data
public class PaymentResponse {
  private String cartao;
  private String id;
  private PaymentDescricaoResponse descricao;
  private PaymentFormaPagamento formaPagamento;
}