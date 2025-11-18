package com.example.app.service;

import com.example.app.dto.payment.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class PaymentServiceAdditionalTest {
  @Autowired
  private PaymentService service;

  private PaymentRequest requisicaoValida(String id) {
    PaymentRequest requisicao = new PaymentRequest();
    requisicao.setCartao("4444123412341234");
    requisicao.setId(id);
    PaymentDescricao descricao = new PaymentDescricao();
    descricao.setValor("50.00");
    descricao.setDataHora("01/05/2021 18:30:00");
    descricao.setEstabelecimento("PetShop Mundo cão");
    requisicao.setDescricao(descricao);
    PaymentFormaPagamento formaPagamento = new PaymentFormaPagamento();
    formaPagamento.setTipo("AVISTA");
    formaPagamento.setParcelas("1");
    requisicao.setFormaPagamento(formaPagamento);
    return requisicao;
  }

  @Test
  void duplicadoNegado() {
    PaymentResponse resposta1 = service.process(requisicaoValida("100023568900030"));
    assertEquals("AUTORIZADO", resposta1.getDescricao().getStatus());
    PaymentResponse resposta2 = service.process(requisicaoValida("100023568900030"));
    assertEquals("NEGADO", resposta2.getDescricao().getStatus());
  }

  @Test
  void tipoInvalidoNegado() {
    PaymentRequest requisicao = requisicaoValida("100023568900031");
    requisicao.getFormaPagamento().setTipo("FOOBAR");
    PaymentResponse resposta = service.process(requisicao);
    assertEquals("NEGADO", resposta.getDescricao().getStatus());
  }

  @Test
  void parcelasNaoNumericasNegado() {
    PaymentRequest requisicao = requisicaoValida("100023568900032");
    requisicao.getFormaPagamento().setParcelas("abc");
    PaymentResponse resposta = service.process(requisicao);
    assertEquals("NEGADO", resposta.getDescricao().getStatus());
  }

  @Test
  void valorZeroNegado() {
    PaymentRequest requisicao = requisicaoValida("100023568900033");
    requisicao.getDescricao().setValor("0.00");
    PaymentResponse resposta = service.process(requisicao);
    assertEquals("NEGADO", resposta.getDescricao().getStatus());
  }

  @Test
  void dataInvalidaNegado() {
    PaymentRequest requisicao = requisicaoValida("100023568900034");
    requisicao.getDescricao().setDataHora("2021-05-01 18:30:00");
    PaymentResponse resposta = service.process(requisicao);
    assertEquals("NEGADO", resposta.getDescricao().getStatus());
  }
}