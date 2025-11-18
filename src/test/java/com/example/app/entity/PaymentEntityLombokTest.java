package com.example.app.entity;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class PaymentEntityLombokTest {
  @Test
  void gettersAndSettersWork() {
    Payment p = new Payment();
    p.setExternalId("abc123");
    p.setCartao("4444*********1234");
    p.setTipo("AVISTA");
    p.setParcelas(1);
    p.setValor(new BigDecimal("10.50"));
    p.setDataHora(LocalDateTime.now());
    p.setEstabelecimento("Loja");
    p.setNsu("0000000001");
    p.setCodigoAutorizacao("000000001");
    p.setStatus("AUTORIZADO");

    assertEquals("abc123", p.getExternalId());
    assertEquals("4444*********1234", p.getCartao());
    assertEquals("AVISTA", p.getTipo());
    assertEquals(1, p.getParcelas());
    assertEquals(new BigDecimal("10.50"), p.getValor());
    assertEquals("Loja", p.getEstabelecimento());
    assertEquals("0000000001", p.getNsu());
    assertEquals("000000001", p.getCodigoAutorizacao());
    assertEquals("AUTORIZADO", p.getStatus());
  }
}