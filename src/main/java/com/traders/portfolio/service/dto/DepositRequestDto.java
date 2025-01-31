package com.traders.portfolio.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class DepositRequestDto {
    private Long depositRequestId;
    private double amount;
    private String requestDateTime;
    private String requestStatus;
    private String approvedDateTime;
}
