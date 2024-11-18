package com.traders.portfolio.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class StockDTO implements Serializable {

    private Long id;
    private String symbol;
    @JsonIgnore
    private String currentPriceKey;

}
