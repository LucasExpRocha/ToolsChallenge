package com.example.app.exception;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BusinessExceptionTest {
  @Test
  void getterReturnsProvidedCode() {
    BusinessException exception = new BusinessException("CODE_X", "mensagem");
    assertEquals("CODE_X", exception.getCode());
    assertEquals("mensagem", exception.getMessage());
  }
}