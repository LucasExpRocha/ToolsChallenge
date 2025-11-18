package com.example.app.service.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ValidatedDataTest {
  @Test
  void gettersRetornamValoresCorretos() {
    LocalDateTime dataHora = LocalDateTime.of(2021, 5, 1, 18, 30, 0);
    ValidatedData dados = new ValidatedData(
        "100023568900999",
        "4444123412341234",
        new BigDecimal("50.00"),
        dataHora,
        "AVISTA",
        1,
        "PetShop Mundo cão"
    );
    assertEquals("100023568900999", dados.getId());
    assertEquals("4444123412341234", dados.getTransacao());
    assertEquals(new BigDecimal("50.00"), dados.getValor());
    assertEquals(dataHora, dados.getDataHora());
    assertEquals("AVISTA", dados.getTipo());
    assertEquals(Integer.valueOf(1), dados.getParcelas());
    assertEquals("PetShop Mundo cão", dados.getEstabelecimento());
  }
}