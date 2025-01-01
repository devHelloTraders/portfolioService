package com.traders.portfolio.service.dto;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.tuple.Pair;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
public class PortfolioDTO implements Serializable {

    private Long id= 0l;
    private Long userId =0l;
    private Double investment;
    private Double currentReturn;
    private Double profit;
    private Float profitPercentage;
    private List<PortfolioStockDTO> stocks = new ArrayList<>();


    public void calculatePortfolio(Pair<Double,Double> portfolioDetails){
        setInvestment(portfolioDetails.getLeft());
        setCurrentReturn(portfolioDetails.getRight());
        setProfit(getCurrentReturn()-getInvestment());
        setProfitPercentage((float) ((getInvestment() > 0) ? ((getCurrentReturn() - getInvestment()) / getInvestment()) * 100 : 0.0));
    }

}
