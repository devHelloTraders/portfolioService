package com.traders.portfolio.domain;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Set;

@Entity
@Table(name = "stock_details")
public class Stock extends AbstractAuditingEntity<Long> implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String symbol;
    private Double lastKnownPrice;
    private String currentPriceKey;

    @OneToMany(mappedBy = "stock", cascade = CascadeType.ALL)
    private Set<Transaction> transactions;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public Double getLastKnownPrice() {
        return lastKnownPrice;
    }

    public void setLastKnownPrice(Double lastKnownPrice) {
        this.lastKnownPrice = lastKnownPrice;
    }

    public String getCurrentPriceKey() {
        return currentPriceKey;
    }

    public void setCurrentPriceKey(String currentPriceKey) {
        this.currentPriceKey = currentPriceKey;
    }

}
