package com.traders.portfolio.service.dto;

import com.traders.portfolio.domain.PaymentMethod;

public record FundWithdrawRequest(
        Double amount,
        PaymentMethod paymentMethod,
        String upiId,
        String accountHolderName,
        String accountNumber,
        String ifsc
) {
}
