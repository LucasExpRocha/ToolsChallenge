package com.example.app.service;

import com.example.app.dto.payment.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class PaymentServiceTest {
  @Autowired
  private PaymentService service;

  @Test
  void processOk() {
    PaymentRequest requisicao = new PaymentRequest();
    requisicao.setCartao("4444123412341234");
    requisicao.setId("100023568900010");
    PaymentDescricao descricao = new PaymentDescricao();
    descricao.setValor("50.00");
    descricao.setDataHora("01/05/2021 18:30:00");
    descricao.setEstabelecimento("PetShop Mundo cão");
    requisicao.setDescricao(descricao);
    PaymentFormaPagamento formaPagamento = new PaymentFormaPagamento();
    formaPagamento.setTipo("AVISTA");
    formaPagamento.setParcelas("1");
    requisicao.setFormaPagamento(formaPagamento);
    PaymentResponse resposta = service.process(requisicao);
    assertEquals("AUTORIZADO", resposta.getDescricao().getStatus());
    assertNotNull(resposta.getDescricao().getNsu());
    assertNotNull(resposta.getDescricao().getCodigoAutorizacao());
  }

  @Test
  void processNegado() {
    PaymentRequest requisicao = new PaymentRequest();
    requisicao.setCartao("4444XXXX1234");
    requisicao.setId("100023568900010");
    PaymentDescricao descricao = new PaymentDescricao();
    descricao.setValor("50.0");
    descricao.setDataHora("2021-05-01 18:30:00");
    descricao.setEstabelecimento("Bad \uD800");
    requisicao.setDescricao(descricao);
    PaymentFormaPagamento formaPagamento = new PaymentFormaPagamento();
    formaPagamento.setTipo("FOO");
    formaPagamento.setParcelas("0");
    requisicao.setFormaPagamento(formaPagamento);
    PaymentResponse resposta = service.process(requisicao);
    assertEquals("NEGADO", resposta.getDescricao().getStatus());
  }
}