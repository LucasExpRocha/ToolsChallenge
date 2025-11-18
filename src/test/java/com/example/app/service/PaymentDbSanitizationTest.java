package com.example.app.service;

import com.example.app.dto.payment.*;
import com.example.app.entity.Payment;
import com.example.app.repository.PaymentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class PaymentDbSanitizationTest {
  @Autowired
  private PaymentService service;
  @Autowired
  private PaymentRepository repository;

  @Test
  void sanitizaPalavrasChaveSqlEParenteses() {
    PaymentRequest requisicao = new PaymentRequest();
    requisicao.setCartao("4444123412341234");
    requisicao.setId("100023568900050");
    PaymentDescricao descricao = new PaymentDescricao();
    descricao.setValor("50.00");
    descricao.setDataHora("01/05/2021 18:30:00");
    descricao.setEstabelecimento("PetShop Mundo cão (DROP TABLE payment)");
    requisicao.setDescricao(descricao);
    PaymentFormaPagamento formaPagamento = new PaymentFormaPagamento();
    formaPagamento.setTipo("AVISTA");
    formaPagamento.setParcelas("1");
    requisicao.setFormaPagamento(formaPagamento);

    PaymentResponse resposta = service.process(requisicao);
    assertEquals("AUTORIZADO", resposta.getDescricao().getStatus());
    Payment p = repository.findByExternalId("100023568900050").orElseThrow();
    assertEquals("PetShop Mundo cão", p.getEstabelecimento());
  }
}