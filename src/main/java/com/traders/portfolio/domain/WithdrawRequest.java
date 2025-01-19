package com.traders.portfolio.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name="withdraw_request")
public class WithdrawRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long withdrawRequestId;

    private double amount;

    @Column(name = "request_datetime")
    private String requestDateTime;

    @Enumerated(EnumType.STRING)
    private WalletRequestStatus requestStatus;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    private String upiId;
    private String accountHolderName;
    private String accountNumber;
    private String ifsc;

    @ManyToOne
    @JoinColumn(name="wallet_id",referencedColumnName = "walletId")
    @JsonBackReference
    private Wallet walletId;
}
