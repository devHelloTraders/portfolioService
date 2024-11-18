package com.traders.portfolio.service.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
public class WatchListStockDTO implements Serializable {

    private long id;
  //  private int orderNum;
    private StockDTO stock;
    private Double currentPrice;
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WatchListStockDTO that = (WatchListStockDTO) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
