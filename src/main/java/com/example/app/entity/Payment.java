package com.example.app.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * Entidade de pagamento persistida.
 *
 * `externalId` é único (idempotência). `nsu` e `codigoAutorizacao`
 * são gerados no serviço no momento da autorização.
 */
@Entity
@Getter
@Setter
public class Payment {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(name = "external_id", unique = true)
  private String externalId;
  @Column(name = "cartao")
  private String cartao;
  private String tipo;
  private Integer parcelas;
  private BigDecimal valor;
  private LocalDateTime dataHora;
  private String estabelecimento;
  private String nsu;
  private String codigoAutorizacao;
  private String status;
  private LocalDateTime canceladoEm;
}