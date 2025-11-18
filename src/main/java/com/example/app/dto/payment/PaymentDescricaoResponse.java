package com.example.app.dto.payment;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * Descrição do resultado da transação.
 *
 * Em sucesso, contém `nsu`, `codigoAutorizacao` e `status=AUTORIZADO`.
 * Em erro, traz `status=NEGADO` e uma `mensagem` com o motivo.
 * O campo `mensagem` é opcional e só é serializado quando não nulo
 * (não aparece em respostas 201).
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class PaymentDescricaoResponse {
  private String valor;
  private String dataHora;
  private String estabelecimento;
  private String nsu;
  private String codigoAutorizacao;
  private String status;
  /** Mensagem descritiva apenas em respostas não-201. */
  private String mensagem;
}