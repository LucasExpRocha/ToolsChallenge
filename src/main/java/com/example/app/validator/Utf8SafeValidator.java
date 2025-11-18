package com.example.app.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.nio.charset.StandardCharsets;

/**
 * Valida strings anotadas com {@link Utf8Safe}, rejeitando surrogates e
 * inconsistências de codificação/decodificação em UTF-8.
 */
public class Utf8SafeValidator implements ConstraintValidator<Utf8Safe, String> {
  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    if (value == null) return true;
    boolean hasSurrogates = value.codePoints().anyMatch(cp -> cp >= 0xD800 && cp <= 0xDFFF);
    if (hasSurrogates) return false;
    byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
    String decoded = new String(bytes, StandardCharsets.UTF_8);
    return decoded.equals(value);
  }
}