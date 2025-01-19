package com.traders.portfolio.service.dto;

public record FundDepositRequest(
        Double amount,
        String fileContent
) {
}
