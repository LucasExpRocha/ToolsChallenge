package com.example.app.controller;

import com.example.app.dto.payment.PaymentRequest;
import com.example.app.dto.payment.PaymentResponse;
import com.example.app.service.PaymentService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import com.example.app.dto.payment.PaymentQueryResponse;
import com.example.app.exception.PaymentValidationException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.Parameter;

/**
 * Controlador REST para pagamentos.
 *
 * Expõe endpoints para processar e consultar pagamentos, delegando a lógica
 * de negócio ao `PaymentService` e definindo o status HTTP conforme o resultado.
 */
@RestController
@RequestMapping("/pagamentos")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {
  private final PaymentService service;
  private final ObjectMapper objectMapper;
  private static final Pattern ID_PATTERN = Pattern.compile("^[0-9]{15}$");

  

  @PostMapping
  @Operation(summary = "Processa pagamento",
    requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, content = @Content(
      schema = @Schema(implementation = com.example.app.dto.payment.PaymentEnvelope.class),
      examples = {
        @io.swagger.v3.oas.annotations.media.ExampleObject(
          name = "Exemplo JSON",
          value = "{\"transacao\":{\"cartao\":\"1234*********1234\",\"id\":\"100023568900001\",\"descricao\":{\"valor\":\"50.00\",\"dataHora\":\"01/05/2021 18:30:00\",\"estabelecimento\":\"PetShop Mundo cão\"},\"formaPagamento\":{\"tipo\":\"AVISTA\",\"parcelas\":\"1\"}}}"
        )
      }
    ))
  )
  @ApiResponses({
    @ApiResponse(responseCode = "201", description = "Created", content = @Content(schema = @Schema(implementation = PaymentResponse.class))), 
    @ApiResponse(responseCode = "402", description = "Payment Required", content = @Content(schema = @Schema(implementation = PaymentResponse.class))),
    @ApiResponse(responseCode = "422", description = "Unprocessable Entity", content = @Content(schema = @Schema(implementation = PaymentResponse.class))),
    @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(schema = @Schema(implementation = com.example.app.exception.ApiError.class)))
  })

  /**
   * Processa um pagamento.
   *
   * Aceita três formatos de payload: "transacao:{...}", objeto com raiz
   * "transacao" ou JSON direto com os campos da transação. O status HTTP
   * é mapeado conforme o resultado: 201 para "AUTORIZADO" e 402 para "NEGADO".
   */
  public ResponseEntity<PaymentResponse> process(@RequestBody String payload) {
    PaymentRequest request = parse(payload);
    PaymentResponse response = service.process(request);
    String cartao = request.getCartao();
    String masked = cartao.length() == 16 ? cartao.substring(0, 4) + "*********" + cartao.substring(12) : cartao.substring(0, 4) + "*********" + cartao.substring(cartao.length() - 4);
    response.setCartao(masked);
    log.info("Transação processada identificador={} status={}", response.getId(), response.getDescricao() != null ? response.getDescricao().getStatus() : null);
    if (response.getDescricao() != null && "NEGADO".equals(response.getDescricao().getStatus())) {
      return new ResponseEntity<>(response, HttpStatus.PAYMENT_REQUIRED);
    }
    return new ResponseEntity<>(response, HttpStatus.CREATED);
  }

  @Operation(summary = "Consulta pagamentos (paginado ou por ID)")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = PaymentQueryResponse.class), examples = {
      @io.swagger.v3.oas.annotations.media.ExampleObject(
        name = "Lista paginada",
        value = "{\"data\":[{\"cartao\":\"4444*********1234\",\"id\":\"100023568900300\",\"descricao\":{\"valor\":\"50.00\",\"dataHora\":\"01/05/2021 18:30:00\",\"estabelecimento\":\"PetShop Mundo cão\",\"nsu\":\"0536038040\",\"codigoAutorizacao\":\"140229194\",\"status\":\"AUTORIZADO\"},\"formaPagamento\":{\"tipo\":\"AVISTA\",\"parcelas\":\"1\"}}],\"rowsPerPage\":20,\"page\":0}"
      )
    })),
    @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = PaymentQueryResponse.class), examples = {
      @io.swagger.v3.oas.annotations.media.ExampleObject(
        name = "ID não encontrado",
        value = "{\"data\":[],\"rowsPerPage\":20,\"page\":0}"
      )
    })),
    @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = com.example.app.exception.ApiError.class))),
    @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(schema = @Schema(implementation = com.example.app.exception.ApiError.class)))
  })
  @GetMapping({"/consulta", "/consulta/{id}"})
  public ResponseEntity<PaymentQueryResponse> consultar(
      @PathVariable(value = "id", required = false) @Parameter(description = "ID externo (opcional)", example = "100023568900300") String identificador,
      @RequestParam(value = "page", required = false) @Parameter(description = "Página (>=0)", example = "0") Integer pagina,
      @RequestParam(value = "rowsPerPage", required = false) @Parameter(description = "Linhas por página (1-100)", example = "20") Integer linhasPorPagina) {
    int linhasPorPaginaEfetivas = linhasPorPagina == null ? 20 : linhasPorPagina;
    if (linhasPorPaginaEfetivas < 1 || linhasPorPaginaEfetivas > 100) {
      throw new PaymentValidationException("PAGINATION_INVALID", "rowsPerPage deve estar entre 1 e 100");
    }
    int paginaEfetiva = pagina == null ? 0 : pagina;
    if (paginaEfetiva < 0) {
      throw new PaymentValidationException("PAGINATION_INVALID", "page deve ser >= 0");
    }

    if (identificador != null) {
      String identificadorAjustado = identificador.trim();
      if (!ID_PATTERN.matcher(identificadorAjustado).matches()) {
        throw new PaymentValidationException("QUERY_ID_INVALID", "ID inválido");
      }
      Optional<PaymentResponse> opcao = service.findOne(identificadorAjustado);
      PaymentQueryResponse body = new PaymentQueryResponse();
      body.setRowsPerPage(linhasPorPaginaEfetivas);
      body.setPage(paginaEfetiva);
      if (opcao.isEmpty()) {
        body.setData(java.util.Collections.emptyList());
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
      }
      body.setData(java.util.List.of(opcao.get()));
      return ResponseEntity.ok(body);
    }

    List<PaymentResponse> list = service.list(paginaEfetiva, linhasPorPaginaEfetivas);
    PaymentQueryResponse body = new PaymentQueryResponse();
    body.setData(list);
    body.setRowsPerPage(linhasPorPaginaEfetivas);
    body.setPage(paginaEfetiva);
    return ResponseEntity.ok(body);
  }

  /**
   * Converte diferentes formatos de payload em {@link PaymentRequest}.
   *
   * Aceita: "transacao:{JSON}", objeto com raiz "transacao" ou JSON direto.
   * Em caso de JSON inválido, lança `PaymentValidationException`.
   */
  private PaymentRequest parse(String payload) {
    try {
      String conteudo = payload == null ? "" : payload.trim();
      if (conteudo.startsWith("transacao:")) {
        String json = conteudo.substring("transacao:".length()).trim();
        return objectMapper.readValue(json, PaymentRequest.class);
      }
      JsonNode node = objectMapper.readTree(conteudo);
      if (node.has("transacao") && node.get("transacao").isObject()) {
        return objectMapper.treeToValue(node.get("transacao"), PaymentRequest.class);
      }
      return objectMapper.readValue(conteudo, PaymentRequest.class);
    } catch (Exception exception) {
      throw new com.example.app.exception.PaymentValidationException("PAYMENT_VALIDATION_ERROR", "JSON inválido");
    }
  }
}