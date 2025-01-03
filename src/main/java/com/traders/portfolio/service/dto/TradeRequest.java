package com.traders.portfolio.service.dto;

import com.traders.portfolio.domain.OrderCategory;
import com.traders.portfolio.domain.OrderType;
import com.traders.portfolio.domain.OrderValidity;

public record TradeRequest(
        Double lotSize,
        OrderType orderType,
        OrderCategory orderCategory,
        OrderValidity orderValidity,
        Long stockId,
        Double price,
        Double stopLossPrice,
        Double targetPrice
) {
}
