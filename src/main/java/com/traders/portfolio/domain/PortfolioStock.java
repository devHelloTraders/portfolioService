package com.traders.portfolio.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
@Entity
@Table(
        name = "portfolio_stocks_detail",
        uniqueConstraints = @UniqueConstraint(columnNames = {"portfolio_id", "stock_id"})
)
@Getter
@Setter
public class PortfolioStock extends AbstractAuditingEntity<Long> implements Serializable {

    public PortfolioStock(){

    }
    public PortfolioStock(Portfolio portfolio,Stock stock){
        this.portfolio =portfolio;
        this.stock = stock;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double averageCost =0.0;
    @Getter
    private Double quantity=0.0;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "stock_id", referencedColumnName = "id")
    Stock stock;
    @ManyToOne
    @JoinColumn(name = "portfolio_id", nullable = false)
    private Portfolio portfolio;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "portfoliostock_id")
    @OrderBy("ID DESC")
    private Set<Transaction> transactions = new HashSet<>();

    private int deleteflag;


    public void addQuantity(Double quantity, Double cost){
        Double txnCost =quantity * cost;
        Double currentAveragePrice = getAverageCost() * getQuantity();
        setQuantity(getQuantity()+quantity);
        setAverageCost(getQuantity() >0 ?  (currentAveragePrice+txnCost)/getQuantity() : 0);
    }
}
