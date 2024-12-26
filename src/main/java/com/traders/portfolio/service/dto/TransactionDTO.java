package com.traders.portfolio.service.dto;

import com.traders.portfolio.domain.OrderCategory;
import com.traders.portfolio.domain.OrderType;
import com.traders.portfolio.domain.TransactionStatus;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
@Getter
@Setter
public class TransactionDTO implements Serializable {

    private Long id;
    private Double price;
    private LocalDateTime requestTimestamp = LocalDateTime.now();
    private LocalDateTime completedTimestamp;
    private Double completedQuantity;
    private OrderCategory orderCategory;
    private StockDTO stock;
    private TransactionStatus transactionStatus;
    private OrderType orderType;

}
