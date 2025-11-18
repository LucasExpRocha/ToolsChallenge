package com.example.app.controller;

import com.example.app.dto.payment.*;
import com.example.app.entity.Payment;
import com.example.app.repository.PaymentRepository;
import com.example.app.service.PaymentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class RefundControllerTest {
  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private PaymentService service;
  @Autowired
  private PaymentRepository repository;

  @Test
  void cancelOk() throws Exception {
    PaymentRequest requisicao = new PaymentRequest();
    requisicao.setCartao("4444123412341234");
    requisicao.setId("100023568900220");
    PaymentDescricao descricao = new PaymentDescricao();
    descricao.setValor("50.00");
    descricao.setDataHora("01/05/2021 18:30:00");
    descricao.setEstabelecimento("PetShop Mundo cão");
    requisicao.setDescricao(descricao);
    PaymentFormaPagamento formaPagamento = new PaymentFormaPagamento();
    formaPagamento.setTipo("AVISTA");
    formaPagamento.setParcelas("1");
    requisicao.setFormaPagamento(formaPagamento);
    service.process(requisicao);

    mockMvc.perform(patch("/estorno/{id}", "100023568900220").contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.transacao.descricao.status").value("CANCELADO"))
      .andExpect(jsonPath("$.transacao.cartao").value("4444*********1234"))
      .andExpect(jsonPath("$.transacao.id").value("100023568900220"))
      .andExpect(jsonPath("$.transacao.descricao.nsu").exists())
      .andExpect(jsonPath("$.transacao.descricao.codigoAutorizacao").exists());
  }

  @Test
  void cancelBadStatus() throws Exception {
    Payment p = new Payment();
    p.setExternalId("100023568900221");
    p.setCartao("4444123412341234");
    p.setTipo("AVISTA");
    p.setParcelas(1);
    p.setStatus("NEGADO");
    repository.save(p);

    mockMvc.perform(patch("/estorno/{id}", "100023568900221").contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isBadRequest());
  }

  @Test
  void cancelNotFound() throws Exception {
    mockMvc.perform(patch("/estorno/{id}", "999999999999999").contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isBadRequest());
  }
}