package com.todoappma.apigateway.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(GatewayException.class)
    public ResponseEntity<Map<String, String>> handleGatewayException(GatewayException ex) {
        return ResponseEntity.status(ex.getStatus())
                .body(Map.of("errorCode", ex.getErrorCode(), "message", ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGeneral(Exception ex) {
        return ResponseEntity.internalServerError()
                .body(Map.of("errorCode", "GATEWAY_500", "message", "An unexpected error occurred"));
    }
}
