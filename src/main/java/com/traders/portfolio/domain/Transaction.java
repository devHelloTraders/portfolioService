package com.traders.portfolio.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name="transaction")
@Getter
@Setter
public class Transaction extends AbstractAuditingEntity<Long> implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double price;
    private LocalDateTime requestTimestamp;
    private LocalDateTime completedTimestamp;
    private Integer completedQuantity;
    private Double completedPrice;
    @Enumerated(EnumType.STRING)
    private OrderType orderType;

    @Enumerated(EnumType.STRING)
    private OrderCategory orderCategory;

    @Enumerated(EnumType.STRING)
    private TransactionStatus transactionStatus;

    @ManyToOne
    @JoinColumn(name = "stock_id", referencedColumnName = "id")
    private Stock stock;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "portfoliostock_id", insertable = false, updatable = false) // Read-only mapping
    private PortfolioStock portfolioStock;




}