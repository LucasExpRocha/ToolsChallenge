package com.example.app.exception;

import java.time.LocalDateTime;
import lombok.Data;

/**
 * Payload de erro retornado pela API.
 *
 * Contém `timestamp`, `status`, `error`, `message` e `path`.
 */
@Data
public class ApiError {
  private LocalDateTime timestamp;
  private int status;
  private String error;
  private String message;
  private String path;
}