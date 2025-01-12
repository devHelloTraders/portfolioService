package com.traders.portfolio.service.dto;

import com.traders.portfolio.domain.TransactionStatus;
import lombok.Builder;

@Builder
public record TransactionUpdateRecord(
        Long id,
        Double price,
        TransactionStatus transactionStatus
)
{


}