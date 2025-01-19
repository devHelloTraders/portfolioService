package com.traders.portfolio.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity()
@Table(name = "deposit_request")
public class DepositRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long depositRequestId;

    private double amount;

    @Column(name = "request_datetime")
    private String requestDateTime;

    @Enumerated(EnumType.STRING)
    private WalletRequestStatus requestStatus;

    @ManyToOne
    @JoinColumn(name="wallet_id",referencedColumnName = "walletId")
    private Wallet walletId;

    @Column(name="approved_datetime")
    private String approvedDateTime;
}
