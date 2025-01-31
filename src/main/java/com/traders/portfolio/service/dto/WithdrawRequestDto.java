package com.traders.portfolio.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WithdrawRequestDto {
    private Long withdrawRequestId;
    private double amount;
    private String requestedDateTime;
    private String paymentMethod;
    private String requestStatus;
    private String upiId;
    private String accountHolderName;
    private String accountNumber;
    private String ifsc;
    private String approvedBy;
}
