package com.example.app.validator;

import com.example.app.dto.payment.PaymentRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;

/**
 * Valida campos obrigatórios, formatos e conteúdo do {@link PaymentRequest}.
 * Retorna string vazia quando válido; caso contrário, uma mensagem de erro.
 */
@RequiredArgsConstructor
public class PaymentRequestValidator {
  private final PaymentRequest request;
  private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
  private static final Pattern VALOR_PATTERN = Pattern.compile("^\\d+\\.\\d{2}$");
  private static final Pattern CARTAO_PATTERN = Pattern.compile("^(?:[0-9]{16}|[0-9]{4}\\*{8,9}[0-9]{4})$");
  private static final Set<String> ALLOWED_TYPES = Set.of("AVISTA", "PARCELADO LOJA", "PARCELADO EMISSOR");

  


  /**
   * Executa a validação completa. Retorna "" se válido ou a mensagem de erro.
   */
  public String validate() {
    // Valida campos obrigatórios e formatos básicos
    String mensagem = validateRequired();
    if (mensagem != null) return mensagem;

    // Valida data/hora com formato específico
    try {
      LocalDateTime.parse(request.getDescricao().getDataHora(), DATE_TIME_FORMATTER);
    } catch (java.time.format.DateTimeParseException e) {
      return "Data e hora inválidas";
    }

    // Valida valor monetário no formato NNNN.NN e maior que zero
    try {
      String valorString = request.getDescricao().getValor();
      if (!VALOR_PATTERN.matcher(valorString).matches()) return "Valor inválido";
      double valorNumerico = Double.parseDouble(valorString);
      if (valorNumerico <= 0) return "Valor inválido";
    } catch (NumberFormatException e) {
      return "Valor inválido";
    }

    // Valida número de parcelas como inteiro positivo
    try {
      int parcela = Integer.parseInt(request.getFormaPagamento().getParcelas());
      if (parcela <= 0) return "Parcelas inválidas";
    } catch (NumberFormatException e) {
      return "Parcelas inválidas";
    }

    // Valida tipo de pagamento contra conjunto permitido
    String tipo = request.getFormaPagamento().getTipo();
    if (tipo == null || !ALLOWED_TYPES.contains(tipo.toUpperCase(Locale.ROOT))) return "Tipo de pagamento inválido";

    return "";
  }

  /**
   * Valida obrigatórios e formatos básicos. Retorna mensagem de erro ou null.
   */
  private String validateRequired() {
    if (request == null) return "Transação não pode ser nula";
    if (request.getId() == null || request.getId().isEmpty()) return "Verificar id da transação";
    if (request.getCartao() == null || request.getCartao().isEmpty()) return "Verificar cartão";
    if (!CARTAO_PATTERN.matcher(request.getCartao()).matches()) return "Cartão inválido";
    if (request.getDescricao() == null) return "Verificar descrição da transação";
    if (request.getDescricao().getValor() == null || request.getDescricao().getValor().isEmpty()) return "Verificar valor da transação";
    if (request.getDescricao().getDataHora() == null || request.getDescricao().getDataHora().isEmpty()) return "Verificar data e hora da transação";
    if (request.getDescricao().getEstabelecimento() == null || request.getDescricao().getEstabelecimento().isEmpty()) return "Verificar estabelecimento da transação";
    if (request.getFormaPagamento() == null) return "Verificar forma de pagamento da transação";
    if (request.getFormaPagamento().getParcelas() == null || request.getFormaPagamento().getParcelas().isEmpty()) return "Verificar parcelas da transação";
    if (request.getFormaPagamento().getTipo() == null) return "Verificar tipo da forma de pagamento";
    return null;
  }
}