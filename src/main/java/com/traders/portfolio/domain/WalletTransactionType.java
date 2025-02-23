package com.traders.portfolio.domain;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum WalletTransactionType {
    DEPOSIT,
    WITHDRAWAL,
    PROFIT,
    LOSS,
    SETTLEMENT,
    BROKERAGE,
    MARGIN;
}
