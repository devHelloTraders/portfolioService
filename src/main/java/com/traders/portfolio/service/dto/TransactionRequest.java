package com.traders.portfolio.service.dto;

import com.traders.portfolio.domain.Stock;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransactionRequest {
    private Long userId;
    private Stock stock;
    private boolean isShortSell;
    private Double askedLotSize;
}
