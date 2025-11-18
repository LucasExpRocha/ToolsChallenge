package com.example.app.repository;

import com.example.app.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/**
 * Repositório JPA de pagamentos.
 *
 * Permite persistir e consultar transações e fornece busca por `externalId`
 * para apoiar a idempotência.
 */
public interface PaymentRepository extends JpaRepository<Payment, Long> {
  /**
   * Busca um pagamento pelo identificador externo.
   */
  Optional<Payment> findByExternalId(String externalId);
}