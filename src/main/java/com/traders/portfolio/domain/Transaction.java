package com.traders.portfolio.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Entity
@Table(name="transaction")
@Getter
@Setter
public class Transaction extends AbstractAuditingEntity<Long> implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double price;
    private String requestTimestamp;
    private String completedTimestamp;
    private Double qty;
    private Double executedPrice;
    private Double tradedQty;
    @Enumerated(EnumType.STRING)
    private OrderType orderType;

    @Enumerated(EnumType.STRING)
    private OrderCategory orderCategory;

    @Enumerated(EnumType.STRING)
    private TransactionStatus transactionStatus;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "portfoliostock_id", updatable = false) // Read-only mapping
    private PortfolioStock portfolioStock;

    @ManyToOne
    @JoinColumn(name = "parent_transaction_id",referencedColumnName = "id")
    @JsonBackReference
    private Transaction parentTransaction;

    @OneToMany(mappedBy = "parentTransaction")
    @JsonManagedReference
    private List<Transaction> childTransactions;

    private int deleteflag;
    private double margin;

    @Transient
    private Double profitLoss;
}