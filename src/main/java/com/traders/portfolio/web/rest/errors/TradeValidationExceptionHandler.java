package com.traders.portfolio.web.rest.errors;

import com.traders.portfolio.validations.exception.TradeValidationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class TradeValidationExceptionHandler {

    @ExceptionHandler(TradeValidationException.class)
    public ResponseEntity<Map<String,Object>> handleValidationException(TradeValidationException ex){
        Map<String,Object> errorResponse
                = new HashMap<>();
        errorResponse.put("message", ex.getMessage());
        errorResponse.put("status",ex.getStatusCode());
        return ResponseEntity.status(ex.getStatusCode()).body(errorResponse);
    }
}
