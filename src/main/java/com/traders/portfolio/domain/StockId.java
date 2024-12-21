package com.traders.portfolio.domain;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
public class StockId implements Serializable {
    private String tradingSymbol;
    private String exchange;

    public StockId() {
    }

    public StockId(String tradingSymbol, String exchange) {
        this.tradingSymbol = tradingSymbol;
        this.exchange = exchange;
    }





    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StockId stockId = (StockId) o;
        return Objects.equals(tradingSymbol, stockId.tradingSymbol) &&
               Objects.equals(exchange, stockId.exchange);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tradingSymbol, exchange);
    }
}
