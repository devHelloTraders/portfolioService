package com.traders.portfolio.service.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class PortfolioStockDTO implements Serializable {

    private Long id;
    private Double averageCost;
    private Integer quantity;
    private StockDTO stock;
    private Double currentPrice;
    private Set<TransactionDTO> transactions = new HashSet<>();
}
