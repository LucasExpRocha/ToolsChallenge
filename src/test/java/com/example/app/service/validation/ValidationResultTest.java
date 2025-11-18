package com.example.app.service.validation;

import com.example.app.service.model.ValidatedData;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ValidationResultTest {
  @Test
  void okPreencheDadosENaoErro() {
    ValidatedData dados = new ValidatedData(
        "100023568900001",
        "4444123412341234",
        new BigDecimal("10.00"),
        LocalDateTime.of(2021, 5, 1, 18, 30, 0),
        "AVISTA",
        1,
        "Loja X"
    );
    ValidationResult r = ValidationResult.ok(dados);
    assertTrue(r.isOk());
    assertNull(r.getErro());
    assertNotNull(r.getDados());
    assertEquals("100023568900001", r.getDados().getId());
  }

  @Test
  void erroPreencheMensagemESemDados() {
    ValidationResult r = ValidationResult.erro("ID inválido");
    assertFalse(r.isOk());
    assertEquals("ID inválido", r.getErro());
    assertNull(r.getDados());
  }
}