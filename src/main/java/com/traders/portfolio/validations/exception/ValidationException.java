package com.traders.portfolio.validations.exception;

import java.io.Serial;

public class ValidationException extends Exception {
    @Serial
    private static final long serialVersionUID = 1L;

    public ValidationException(String message) {
        super(message);
    }


}
