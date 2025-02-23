package com.traders.portfolio.validations.exception;

import com.traders.portfolio.web.rest.errors.TradeValidationErrorCode;
import lombok.Getter;

import java.io.Serial;

@Getter
public class TradeValidationException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    private final int statusCode;

    public TradeValidationException(TradeValidationErrorCode errorCode) {
        super(errorCode.getErrorMessage());
        this.statusCode = errorCode.getErrorCode();
    }

    public TradeValidationException(TradeValidationErrorCode errorCode, String message) {
        super(message);
        this.statusCode = errorCode.getErrorCode();
    }
}
