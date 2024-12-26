package com.traders.portfolio.service.dto;

import com.traders.portfolio.domain.OrderCategory;
import com.traders.portfolio.domain.OrderType;

public record TradeRequest(
        Double lotSize,
        OrderType orderType,
        OrderCategory orderCategory,
        Long stockId,
        Double price,
        Double stopLossPrice,
        Double targetPrice
) {
}
