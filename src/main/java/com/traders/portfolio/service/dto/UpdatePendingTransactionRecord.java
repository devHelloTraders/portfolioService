package com.traders.portfolio.service.dto;

public record UpdatePendingTransactionRecord(
        Long transactionId,
        Double lotSize,
        Double price
) {
}
