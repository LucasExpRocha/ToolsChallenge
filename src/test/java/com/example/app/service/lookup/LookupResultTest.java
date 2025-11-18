package com.example.app.service.lookup;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LookupResultTest {
  @Test
  void foundRetornaTrue() {
    LookupResult r = LookupResult.found();
    assertTrue(r.isFound());
  }

  @Test
  void notFoundRetornaFalse() {
    LookupResult r = LookupResult.notFound();
    assertFalse(r.isFound());
  }
}