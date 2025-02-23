package com.traders.portfolio.trades.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ActiveTradesResponseDTO {
    private Long id;
    private String scriptName;
    private String exchange;
    private String orderType;
    private Long userId;
    private String userName;
    private Double buyPrice;
    private Double sellPrice;
    private Double qty;
    private Double lotSize;
    private boolean completedTrade;
    private boolean shortSellTrade;
    private String sellAtTime;
    private String buyAtTime;
    private Long instrumentToken;
    private String exchangeSegment;
    private String tradingSymbol;
    private Double profitLoss;
    private Double margin;
}
