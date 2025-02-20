package com.traders.portfolio.service.dto;


import lombok.Builder;

@Builder
public record CancelTransactionRecord(
        Long id
) {
}
