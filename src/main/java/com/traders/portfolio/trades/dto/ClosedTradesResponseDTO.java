package com.traders.portfolio.trades.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClosedTradesResponseDTO {
    private Long id;
    private String scriptName;
    private String exchange;
    private Long userId;
    private String userName;
    private Double buyPrice;
    private Double sellPrice;
    private Double qty;
    private Double lotSize;
    private Double profitLoss;
    private int timeDiffInSeconds;
    private String sellAtTime;
    private String buyAtTime;
    private boolean shortTradeSell;
    private Long instrumentToken;
}
