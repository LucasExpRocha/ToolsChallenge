package com.example.app.service;

import com.example.app.dto.payment.*;
import com.example.app.entity.Payment;
import com.example.app.repository.PaymentRepository;
import com.example.app.util.Utf8Sanitizer;
import com.example.app.validator.PaymentRequestValidator;
import com.example.app.exception.PaymentProcessingException;
import com.example.app.exception.PaymentValidationException;
import com.example.app.exception.DuplicatePaymentException;
import com.example.app.exception.PaymentCreationException;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Page;
import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Serviço de processamento de pagamentos.
 *
 * Valida dados, gera identificadores e persiste a transação, mantendo
 * compatibilidade com a API existente.
 */
@Service
@Slf4j
public class PaymentService {
  private final PaymentRepository repository;
  private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
  private final SecureRandom random;

  /**
   * Construtor padrão com injeção do repositório.
   * Inicializa o gerador seguro de números aleatórios.
   */
  @Autowired
  public PaymentService(PaymentRepository repository) {
    this(repository, new SecureRandom());
  }

  /**
   * Construtor com injeção de {@link SecureRandom} para permitir testes.
   */
  public PaymentService(PaymentRepository repository, SecureRandom random) {
    this.repository = repository;
    this.random = random;
  }

  /**
   * Processa uma requisição de pagamento: valida, sanitiza, gera identificadores
   * e persiste a transação. Em falha de validação, retorna status "NEGADO".
   */
  @Transactional
  public PaymentResponse process(PaymentRequest request) {
    PaymentResponse response = initializeResponse(request);
    try {
      String erro = new PaymentRequestValidator(request).validate();
      if (!erro.isEmpty()) {
        throw new PaymentValidationException("PAYMENT_VALIDATION_ERROR", erro);
      }

      String identificador = request.getId();
      boolean existe = repository.findByExternalId(identificador).isPresent();
      if (existe) {
        throw new DuplicatePaymentException("PAYMENT_DUPLICATE", "Transação já processada para identificador=" + identificador);
      }

      BigDecimal valor = new BigDecimal(request.getDescricao().getValor());
      LocalDateTime dataHora = LocalDateTime.parse(request.getDescricao().getDataHora(), formatter);
      String tipo = request.getFormaPagamento().getTipo().toUpperCase(Locale.ROOT);
      Integer parcelas = Integer.valueOf(request.getFormaPagamento().getParcelas());
      String estabelecimento = Utf8Sanitizer.sanitize(request.getDescricao().getEstabelecimento());

      Payment payment = criarTransacao(identificador, request.getCartao(), valor, dataHora, tipo, parcelas, estabelecimento);

      preencherRespostaAutorizada(response, payment.getNsu(), payment.getCodigoAutorizacao());
      log.info("Pagamento autorizado identificador={} nsu={} codigo={}", payment.getExternalId(),
          payment.getNsu(), payment.getCodigoAutorizacao());
      return response;
    } catch (PaymentProcessingException exception) {
      log.warn("Pagamento negado identificador={} codigo={} motivo={}",
          request != null ? request.getId() : null,
          exception.getCode(),
          exception.getMessage());
      return negado(response, exception.getMessage());
    } catch (Exception exception) {
      log.error("Erro inesperado no processamento identificador={}", request != null ? request.getId() : null, exception);
      return negado(response, "Erro inesperado");
    }
  }

  /**
   * Inicializa a resposta com os dados básicos e sanitiza o estabelecimento.
   */
  private PaymentResponse initializeResponse(PaymentRequest request) {
    PaymentResponse response = new PaymentResponse();
    response.setCartao(request != null ? request.getCartao() : null);
    response.setId(request != null ? request.getId() : null);
    PaymentDescricaoResponse descricao = new PaymentDescricaoResponse();
    if (request != null && request.getDescricao() != null) {
      descricao.setValor(request.getDescricao().getValor());
      descricao.setDataHora(request.getDescricao().getDataHora());
      String estabSan = Utf8Sanitizer.sanitize(request.getDescricao().getEstabelecimento());
      descricao.setEstabelecimento(estabSan);
    }
    response.setDescricao(descricao);
    response.setFormaPagamento(request != null ? request.getFormaPagamento() : null);
    return response;
  }

  /**
   * Gera o NSU (10 dígitos) usando {@link SecureRandom}.
   */
  private String gerarNsu() {
    long v = random.nextLong(1_000_000_0000L);
    return String.format("%010d", v);
  }

  /**
   * Gera o código de autorização (9 dígitos).
   */
  private String gerarCodigoAutorizacao() {
    int v = random.nextInt(1_000_000_000);
    return String.format("%09d", v);
  }

  /**
   * Preenche a resposta com dados de autorização e define status "AUTORIZADO".
   */
  private void preencherRespostaAutorizada(PaymentResponse response, String nsu, String codigo) {
    PaymentDescricaoResponse descricao = response.getDescricao();
    descricao.setNsu(nsu);
    descricao.setCodigoAutorizacao(codigo);
    descricao.setStatus("AUTORIZADO");
  }

  /**
   * Valida o request e retorna dados normalizados ou o motivo do erro.
   */
  

  /**
   * Verifica se já existe pagamento para o `externalId` informado.
   */
  

  /**
   * Cria e persiste a transação autorizada.
   * Em caso de falha, lança {@link PaymentCreationException}.
   */
  private Payment criarTransacao(String id, String cartao, BigDecimal valor, LocalDateTime dataHora, String tipo, Integer parcelas, String estabelecimento) {
    try {
      String nsu = gerarNsu();
      String codigo = gerarCodigoAutorizacao();
      Payment pagamento = new Payment();
      pagamento.setExternalId(id);
      pagamento.setCartao(cartao);
      pagamento.setTipo(tipo);
      pagamento.setParcelas(parcelas);
      pagamento.setValor(valor);
      pagamento.setDataHora(dataHora);
      pagamento.setEstabelecimento(estabelecimento);
      pagamento.setNsu(nsu);
      pagamento.setCodigoAutorizacao(codigo);
      pagamento.setStatus("AUTORIZADO");
      return repository.save(pagamento);
    } catch (RuntimeException e) {
      throw new PaymentCreationException("PAYMENT_CREATION_ERROR", "Falha ao criar transação");
    }
  }


  
  /**
   * Estorna transacao previamente autorizada.
   *
   * Parametros:
   * - externalId (String): identificador externo unico da transacao.
   *
   * Retorno:
   * - PaymentResponse: resposta ecoando dados com status "CANCELADO".
   *
   * Exemplo:
   * /*
   *   PaymentResponse r = cancel("100023568900220");
   *
   * Observacoes:
   * - Operacao atomica via {@link Transactional}.
   * - Apenas transacoes com status "AUTORIZADO" podem ser estornadas.
   */
  @Transactional
  public PaymentResponse cancel(String externalId) {
    if (externalId == null || externalId.trim().isEmpty()) {
      throw new PaymentValidationException("REFUND_VALIDATION_ERROR", "ID inválido");
    }
    Payment pagamento = repository.findByExternalId(externalId)
        .orElseThrow(() -> new PaymentValidationException("REFUND_VALIDATION_ERROR", "Transação não encontrada"));
    if (!"AUTORIZADO".equals(pagamento.getStatus())) {
      log.warn("Estorno rejeitado identificador={} status_atual={}", externalId, pagamento.getStatus());
      throw new PaymentValidationException("REFUND_STATUS_INVALID", "Status atual não permite estorno");
    }
    LocalDateTime now = LocalDateTime.now();
    pagamento.setStatus("CANCELADO");
    pagamento.setCanceladoEm(now);
    repository.save(pagamento);
    PaymentResponse response = new PaymentResponse();
    String cartao = pagamento.getCartao();
    if (cartao != null) {
      String masked = cartao.length() == 16 ? cartao.substring(0, 4) + "*********" + cartao.substring(12) : cartao.substring(0, 4) + "*********" + cartao.substring(cartao.length() - 4);
      response.setCartao(masked);
    }
    response.setId(pagamento.getExternalId());
    PaymentDescricaoResponse descricao = new PaymentDescricaoResponse();
    descricao.setValor(pagamento.getValor() != null ? pagamento.getValor().toPlainString() : null);
    descricao.setDataHora(formatter.format(now));
    descricao.setEstabelecimento(Utf8Sanitizer.sanitize(pagamento.getEstabelecimento()));
    descricao.setNsu(pagamento.getNsu());
    descricao.setCodigoAutorizacao(pagamento.getCodigoAutorizacao());
    descricao.setStatus("CANCELADO");
    response.setDescricao(descricao);
    PaymentFormaPagamento formaPagamento = new PaymentFormaPagamento();
    formaPagamento.setTipo(pagamento.getTipo());
    formaPagamento.setParcelas(pagamento.getParcelas() != null ? pagamento.getParcelas().toString() : null);
    response.setFormaPagamento(formaPagamento);
    log.info("Estorno realizado identificador={} canceladoEm={}", externalId, formatter.format(now));
    return response;
  }


  /**
   * Marca resposta como NEGADO.
   *
   * @param response resposta
   * @return resposta com status NEGADO
   */
  private PaymentResponse negado(PaymentResponse response, String mensagem) {
    PaymentDescricaoResponse descricao = response.getDescricao();
    if (descricao == null) {
      descricao = new PaymentDescricaoResponse();
      response.setDescricao(descricao);
    }
    descricao.setStatus("NEGADO");
    descricao.setMensagem(mensagem);
    return response;
  }

  public PaymentResponse toResponse(Payment pagamento) {
    PaymentResponse response = new PaymentResponse();
    String cartao = pagamento.getCartao();
    if (cartao != null) {
      String masked = cartao.length() == 16 ? cartao.substring(0, 4) + "*********" + cartao.substring(12) : cartao.substring(0, 4) + "*********" + cartao.substring(cartao.length() - 4);
      response.setCartao(masked);
    }
    response.setId(pagamento.getExternalId());
    PaymentDescricaoResponse descricao = new PaymentDescricaoResponse();
    descricao.setValor(pagamento.getValor() != null ? pagamento.getValor().toPlainString() : null);
    descricao.setDataHora(pagamento.getDataHora() != null ? formatter.format(pagamento.getDataHora()) : null);
    descricao.setEstabelecimento(Utf8Sanitizer.sanitize(pagamento.getEstabelecimento()));
    descricao.setNsu(pagamento.getNsu());
    descricao.setCodigoAutorizacao(pagamento.getCodigoAutorizacao());
    descricao.setStatus(pagamento.getStatus());
    response.setDescricao(descricao);
    PaymentFormaPagamento formaPagamento = new PaymentFormaPagamento();
    formaPagamento.setTipo(pagamento.getTipo());
    formaPagamento.setParcelas(pagamento.getParcelas() != null ? pagamento.getParcelas().toString() : null);
    response.setFormaPagamento(formaPagamento);
    return response;
  }

  public java.util.Optional<PaymentResponse> findOne(String externalId) {
    return repository.findByExternalId(externalId).map(this::toResponse);
  }

  public java.util.List<PaymentResponse> list(int page, int rowsPerPage) {
    Page<Payment> result = repository.findAll(PageRequest.of(page, rowsPerPage, Sort.by("id").descending()));
    return result.getContent().stream().map(this::toResponse).collect(java.util.stream.Collectors.toList());
  }

  
}
