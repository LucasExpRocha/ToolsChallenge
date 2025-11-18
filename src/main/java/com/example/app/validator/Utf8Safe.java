package com.example.app.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Validação para garantir texto seguro em UTF-8 (sem surrogates).
 */
@Documented
@Constraint(validatedBy = Utf8SafeValidator.class)
@Target({FIELD})
@Retention(RUNTIME)
public @interface Utf8Safe {
  String message() default "Texto deve ser UTF-8 válido";
  Class<?>[] groups() default {};
  Class<? extends Payload>[] payload() default {};
}