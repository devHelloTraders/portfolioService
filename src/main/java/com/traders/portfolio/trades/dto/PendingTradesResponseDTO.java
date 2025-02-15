package com.traders.portfolio.trades.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PendingTradesResponseDTO {
    private Long id;
    private String requestedTime;
    private String exchange;
    private String scriptName;
    private String userId;
    private String userName;
    private String orderType;
    private Double rate;
    private Double lotSize;
    private Double qty;
    private Long instrumentToken;
}
