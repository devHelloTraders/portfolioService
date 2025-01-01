package com.traders.portfolio.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Where;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "portfolio")
@Getter
@Setter
public class Portfolio  extends AbstractAuditingEntity<Long> implements Serializable {

    public Portfolio(){

    }

    public Portfolio(Long userId){
        this.userId = userId;
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private long userId;

    @OneToMany(mappedBy = "portfolio", cascade = CascadeType.ALL, orphanRemoval = true)
    @Where(clause = "deleteflag != 1")

    private List<PortfolioStock> portfolioStocks = new ArrayList<>();

}
