package com.traders.portfolio.domain;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum OrderType {
    BUY {
        @Override
        public Double getQuantity() {
            return quantity;
        }
    },SELL {
        @Override
        public Double getQuantity() {
            return -1* quantity;
        }
    };
    Double quantity;
    public void setQuantity(Double quantity){
        this.quantity = quantity;
    }
    public abstract Double getQuantity();
}