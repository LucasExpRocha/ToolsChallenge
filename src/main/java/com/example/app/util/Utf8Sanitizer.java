package com.example.app.util;

import java.text.Normalizer;
import java.nio.charset.StandardCharsets;

/**
 * Sanitiza texto para uso seguro em UTF-8.
 *
 * Normaliza para NFC e remove surrogates e alguns caracteres/trechos
 * potencialmente problemáticos para persistência e exibição.
 */

public final class Utf8Sanitizer {
  private Utf8Sanitizer() {}
  /**
   * Retorna o texto sanitizado ou `null` se a entrada for `null`.
   */
  public static String sanitize(String s) {
    if (s == null) return null;
    String normalized = Normalizer.normalize(s, Normalizer.Form.NFC);
    String filtered = normalized.codePoints()
      .filter(cp -> cp < 0xD800 || cp > 0xDFFF)
      .filter(cp -> cp != '\'' && cp != ';' && cp != '(' && cp != ')')
      .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
      .toString();
    String stripped = filtered.replace("--", "").replace("/*", "").replace("*/", "");
    String withoutKeywords = stripped.replaceAll("(?i)\\b(drop|table|insert|update|delete|union|select|where|or|and|exec|execute|into|values|payment)\\b", "");
    String compact = withoutKeywords.replaceAll("\\s+", " ").trim();
    byte[] bytes = compact.getBytes(StandardCharsets.UTF_8);
    return new String(bytes, StandardCharsets.UTF_8);
  }
}