package com.traders.portfolio.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity(name="wallet")
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long walletId;

    private String createdAt;

    private long userId;
    
    private Double balance;

    @OneToMany(mappedBy = "walletId")
    @JsonManagedReference
    private List<DepositRequest> depositRequest;
}
