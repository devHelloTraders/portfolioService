package com.traders.portfolio.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="wallet_transaction")
@Getter
@Setter
public class WalletTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long walletTransactionId;

    private Double amount;
    @Enumerated(EnumType.STRING)
    private WalletTransactionType transactionType;
    private String remarks;
    private String createdDateTime;
    private int deleteFlag;

    @ManyToOne
    @JoinColumn(name = "wallet_id",referencedColumnName = "walletId")
    private Wallet walletId;
}
