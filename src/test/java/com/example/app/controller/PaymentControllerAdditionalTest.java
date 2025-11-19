package com.example.app.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import java.util.Objects;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class PaymentControllerAdditionalTest {
  @Autowired
  private MockMvc mockMvc;

  @Test
  void aceitaStringPrefixadaTransacao() throws Exception {
    String body = "transacao:{\"cartao\":\"4444123412341234\",\"id\":\"100023568900040\",\"descricao\":{\"valor\":\"50.00\",\"dataHora\":\"01/05/2021 18:30:00\",\"estabelecimento\":\"PetShop Mundo cão\"},\"formaPagamento\":{\"tipo\":\"AVISTA\",\"parcelas\":\"1\"}}";
    mockMvc.perform(post("/pagamentos").contentType(Objects.requireNonNull(MediaType.TEXT_PLAIN)).content(body))
      .andExpect(status().isCreated())
      .andExpect(jsonPath("$.descricao.status").value("AUTORIZADO"));
  }

  @Test
  void aceitaObjetoDiretoCartao() throws Exception {
    String body = "{\"cartao\":\"4444123412341234\",\"id\":\"100023568900041\",\"descricao\":{\"valor\":\"50.00\",\"dataHora\":\"01/05/2021 18:30:00\",\"estabelecimento\":\"PetShop Mundo cão\"},\"formaPagamento\":{\"tipo\":\"AVISTA\",\"parcelas\":\"1\"}}";
    mockMvc.perform(post("/pagamentos").contentType(Objects.requireNonNull(MediaType.APPLICATION_JSON)).content(body))
      .andExpect(status().isCreated())
      .andExpect(jsonPath("$.descricao.status").value("AUTORIZADO"));
  }

  @Test
  void aceitaCartaoMascaradoNoveAsteriscos() throws Exception {
    String body = "{\"transacao\":{\"cartao\":\"4444*********1234\",\"id\":\"100023568900042\",\"descricao\":{\"valor\":\"50.00\",\"dataHora\":\"01/05/2021 18:30:00\",\"estabelecimento\":\"PetShop Mundo cão\"},\"formaPagamento\":{\"tipo\":\"AVISTA\",\"parcelas\":\"1\"}}}";
    mockMvc.perform(post("/pagamentos").contentType(Objects.requireNonNull(MediaType.APPLICATION_JSON)).content(body))
      .andExpect(status().isCreated())
      .andExpect(jsonPath("$.cartao").value("4444*********1234"))
      .andExpect(jsonPath("$.descricao.status").value("AUTORIZADO"));
  }

  @Test
  void avistaComParcelasDiferenteDeUmNegado() throws Exception {
    String body = "{\"transacao\":{\"cartao\":\"4444123412341234\",\"id\":\"100023568900043\",\"descricao\":{\"valor\":\"50.00\",\"dataHora\":\"01/05/2021 18:30:00\",\"estabelecimento\":\"PetShop Mundo cão\"},\"formaPagamento\":{\"tipo\":\"AVISTA\",\"parcelas\":\"2\"}}}";
    mockMvc.perform(post("/pagamentos").contentType(Objects.requireNonNull(MediaType.APPLICATION_JSON)).content(body))
      .andExpect(status().is(402))
      .andExpect(jsonPath("$.descricao.status").value("NEGADO"))
      .andExpect(jsonPath("$.descricao.mensagem").value("Pagamento à vista deve ter exatamente 1 parcela"));
  }
}