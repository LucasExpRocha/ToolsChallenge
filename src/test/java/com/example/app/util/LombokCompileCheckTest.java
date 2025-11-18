package com.example.app.util;

import lombok.Data;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LombokCompileCheckTest {
  @Data
  static class LombokPojo {
    private String field;
  }

  @Test
  void lombokGeneratesGettersSetters() {
    LombokPojo p = new LombokPojo();
    p.setField("ok");
    assertEquals("ok", p.getField());
  }
}