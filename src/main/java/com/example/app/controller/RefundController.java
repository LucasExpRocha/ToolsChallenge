package com.example.app.controller;

import com.example.app.dto.refund.RefundResponse;
import com.example.app.dto.payment.PaymentResponse;
import com.example.app.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

@RestController
@RequestMapping("/estorno")
public class RefundController {
  private final PaymentService service;
  private final Logger logger = LoggerFactory.getLogger(RefundController.class);

  public RefundController(PaymentService service) {
    this.service = service;
  }

  @PatchMapping("/{id}")
  @Operation(summary = "Estorna pagamento", description = "Altera o status de 'AUTORIZADO' para 'CANCELADO'.")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = RefundResponse.class))),
    @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = com.example.app.exception.ApiError.class))),
    @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(schema = @Schema(implementation = com.example.app.exception.ApiError.class)))
  })
  /**
   * Realiza o estorno de uma transação previamente autorizada.
   *
   * Retorna a transação com status "CANCELADO". Caso o status atual não seja
   * "AUTORIZADO", responde com erro 400.
   */
  public ResponseEntity<RefundResponse> cancel(@PathVariable("id") String id) {
    logger.info("Solicitação de estorno id={}", id);
    PaymentResponse respostaPagamento = service.cancel(id);
    RefundResponse respostaEstorno = new RefundResponse();
    respostaEstorno.setTransacao(respostaPagamento);
    return ResponseEntity.ok(respostaEstorno);
  }
}