package com.traders.portfolio.service.dto;

import com.traders.portfolio.domain.OrderCategory;
import com.traders.portfolio.domain.OrderType;
import com.traders.portfolio.domain.OrderValidity;

public record TradeRequest(
        Double lotSize,
        OrderType orderType,
        OrderCategory orderCategory,
        Long stockId,
        Double askedPrice,
        Double stopLossPrice,
        Double targetPrice,
        OrderValidity orderValidity
) {
}
