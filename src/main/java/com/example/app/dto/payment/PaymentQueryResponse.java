package com.example.app.dto.payment;

import java.util.List;
import lombok.Data;

@Data
public class PaymentQueryResponse {
  private List<PaymentResponse> data;
  private int rowsPerPage;
  private int page;
}