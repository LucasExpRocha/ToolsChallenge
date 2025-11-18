package com.example.app.service;

import com.example.app.dto.payment.*;
import com.example.app.repository.PaymentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.concurrent.CompletableFuture;
import java.util.stream.IntStream;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PaymentLoadTest {
  @Autowired
  private PaymentService service;
  @Autowired
  private PaymentRepository repository;

  @Test
  void multiplaAutorizacaoConcorrente() {
    CompletableFuture<?>[] tasks = IntStream.range(0, 20).mapToObj(i -> CompletableFuture.runAsync(() -> {
      PaymentRequest requisicao = new PaymentRequest();
      requisicao.setCartao("4444123412341234");
      requisicao.setId("1000235689001" + String.format("%02d", i));
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
    })).toArray(CompletableFuture[]::new);
    CompletableFuture.allOf(tasks).join();
    assertTrue(repository.findByExternalId("100023568900102").isPresent());
  }
}