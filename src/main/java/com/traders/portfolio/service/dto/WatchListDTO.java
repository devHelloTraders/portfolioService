package com.traders.portfolio.service.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Getter
@Setter
public class WatchListDTO implements Serializable {

    private Long id= 0l;
    private long userId =0l;
    private List<WatchListStockDTO> watchListStocks = new ArrayList<>();

}
