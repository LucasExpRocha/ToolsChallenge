package com.example.app.service;

import com.example.app.dto.payment.*;
import com.example.app.entity.Payment;
import com.example.app.exception.PaymentValidationException;
import com.example.app.repository.PaymentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class PaymentServiceRefundTest {
  @Autowired
  private PaymentService service;
  @Autowired
  private PaymentRepository repository;

  @Test
  void cancelOk() {
    PaymentRequest requisicao = new PaymentRequest();
    requisicao.setCartao("4444123412341234");
    requisicao.setId("100023568900210");
    PaymentDescricao descricao = new PaymentDescricao();
    descricao.setValor("50.00");
    descricao.setDataHora("01/05/2021 18:30:00");
    descricao.setEstabelecimento("PetShop Mundo cão");
    requisicao.setDescricao(descricao);
    PaymentFormaPagamento formaPagamento = new PaymentFormaPagamento();
    formaPagamento.setTipo("AVISTA");
    formaPagamento.setParcelas("1");
    requisicao.setFormaPagamento(formaPagamento);

    PaymentResponse respostaAutorizada = service.process(requisicao);
    assertEquals("AUTORIZADO", respostaAutorizada.getDescricao().getStatus());

    PaymentResponse resposta = service.cancel("100023568900210");
    assertEquals("CANCELADO", resposta.getDescricao().getStatus());

    Payment p = repository.findByExternalId("100023568900210").orElseThrow();
    assertEquals("CANCELADO", p.getStatus());
    assertNotNull(p.getCanceladoEm());
  }

  @Test
  void cancelBadStatus() {
    Payment p = new Payment();
    p.setExternalId("100023568900211");
    p.setCartao("4444123412341234");
    p.setTipo("AVISTA");
    p.setParcelas(1);
    p.setStatus("NEGADO");
    repository.save(p);

    assertThrows(PaymentValidationException.class, () -> service.cancel("100023568900211"));
  }

  @Test
  void cancelNotFound() {
    assertThrows(PaymentValidationException.class, () -> service.cancel("999999999999999"));
  }
}