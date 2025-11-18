package com.example.app.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import java.util.Objects;
import org.springframework.test.web.servlet.MockMvc;
import com.example.app.repository.PaymentRepository;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class PaymentControllerTest {
  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private PaymentRepository repository;

  @Test
  void autorizado() throws Exception {
    String json = "{\"transacao\":{\"cartao\":\"4444123412341234\",\"id\":\"100023568900001\",\"descricao\":{\"valor\":\"50.00\",\"dataHora\":\"01/05/2021 18:30:00\",\"estabelecimento\":\"PetShop Mundo cão\"},\"formaPagamento\":{\"tipo\":\"AVISTA\",\"parcelas\":\"1\"}}}";
    mockMvc.perform(post("/pagamentos").contentType(Objects.requireNonNull(MediaType.APPLICATION_JSON)).content(json))
      .andExpect(status().isCreated())
      .andExpect(jsonPath("$.cartao").value("4444*********1234"))
      .andExpect(jsonPath("$.descricao.status").value("AUTORIZADO"))
      .andExpect(jsonPath("$.descricao.nsu").exists())
      .andExpect(jsonPath("$.descricao.codigoAutorizacao").exists());
  }

  @Test
  void negadoFormatoInvalido() throws Exception {
    String json = "{\"transacao\":{\"cartao\":\"4444XXXX1234\",\"id\":\"100023568900002\",\"descricao\":{\"valor\":\"50.0\",\"dataHora\":\"2021-05-01 18:30:00\",\"estabelecimento\":\"PetShop Mundo cão\"},\"formaPagamento\":{\"tipo\":\"FOO\",\"parcelas\":\"0\"}}}";
    mockMvc.perform(post("/pagamentos").contentType(Objects.requireNonNull(MediaType.APPLICATION_JSON)).content(json))
      .andExpect(status().is(402))
      .andExpect(jsonPath("$.descricao.status").value("NEGADO"));
  }

  @Test
  void negadoDigitosUnicodeNaoAscii() throws Exception {
    String unicodeDigits = "１２３４５６７８９０１２３４５６";
    String json = "{\"transacao\":{\"cartao\":\"" + unicodeDigits + "\",\"id\":\"100023568900003\",\"descricao\":{\"valor\":\"50.00\",\"dataHora\":\"01/05/2021 18:30:00\",\"estabelecimento\":\"PetShop Mundo cão\"},\"formaPagamento\":{\"tipo\":\"AVISTA\",\"parcelas\":\"1\"}}}";
    mockMvc.perform(post("/pagamentos").contentType(Objects.requireNonNull(MediaType.APPLICATION_JSON)).content(json))
      .andExpect(status().is(402))
      .andExpect(jsonPath("$.descricao.status").value("NEGADO"));
  }

  @Test
  void consultaComIdOk() throws Exception {
    String id = "100023568900300";
    String json = "{\"transacao\":{\"cartao\":\"4444123412341234\",\"id\":\"" + id + "\",\"descricao\":{\"valor\":\"50.00\",\"dataHora\":\"01/05/2021 18:30:00\",\"estabelecimento\":\"PetShop Mundo cão\"},\"formaPagamento\":{\"tipo\":\"AVISTA\",\"parcelas\":\"1\"}}}";
    mockMvc.perform(post("/pagamentos").contentType(Objects.requireNonNull(MediaType.APPLICATION_JSON)).content(json))
      .andExpect(status().isCreated());

    mockMvc.perform(get("/pagamentos/consulta/{id}", id))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.data[0].cartao").value("4444*********1234"))
      .andExpect(jsonPath("$.data[0].id").value(id))
      .andExpect(jsonPath("$.rowsPerPage").value(20))
      .andExpect(jsonPath("$.page").value(0));
  }

  @Test
  void consultaSemIdPaginacao() throws Exception {
    for (int i = 0; i < 25; i++) {
      String id = String.format("10002356890%05d", i);
      String json = "{\"transacao\":{\"cartao\":\"4444123412341234\",\"id\":\"" + id + "\",\"descricao\":{\"valor\":\"50.00\",\"dataHora\":\"01/05/2021 18:30:00\",\"estabelecimento\":\"PetShop Mundo cão\"},\"formaPagamento\":{\"tipo\":\"AVISTA\",\"parcelas\":\"1\"}}}";
      mockMvc.perform(post("/pagamentos").contentType(Objects.requireNonNull(MediaType.APPLICATION_JSON)).content(json))
        .andExpect(status().isCreated());
    }

    mockMvc.perform(get("/pagamentos/consulta").param("page", "0").param("rowsPerPage", "20"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.data.length()").value(20))
      .andExpect(jsonPath("$.rowsPerPage").value(20))
      .andExpect(jsonPath("$.page").value(0));

    long total = repository.count();
    int expectedSecondPage = (int) Math.min(20, Math.max(0, total - 20));
    mockMvc.perform(get("/pagamentos/consulta").param("page", "1").param("rowsPerPage", "20"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.data.length()").value(expectedSecondPage))
      .andExpect(jsonPath("$.rowsPerPage").value(20))
      .andExpect(jsonPath("$.page").value(1));
  }

  @Test
  void validacaoPaginacaoLimites() throws Exception {
    mockMvc.perform(get("/pagamentos/consulta").param("rowsPerPage", "0"))
      .andExpect(status().isBadRequest());
    mockMvc.perform(get("/pagamentos/consulta").param("rowsPerPage", "101"))
      .andExpect(status().isBadRequest());
    mockMvc.perform(get("/pagamentos/consulta").param("page", "-1"))
      .andExpect(status().isBadRequest());
  }

  @Test
  void validacaoIdFormato() throws Exception {
    mockMvc.perform(get("/pagamentos/consulta/{id}", "abc"))
      .andExpect(status().isBadRequest());
  }

  @Test
  void idNaoEncontrado() throws Exception {
    mockMvc.perform(get("/pagamentos/consulta/{id}", "999999999999999"))
      .andExpect(status().isNotFound());
  }
}