package com.traders.portfolio.domain;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum TransactionStatus {

    COMPLETED{
        @Override
        public LocalDateTime completedTime(){
            return LocalDateTime.now();
        }
    },PENDING,CANCELLED;

    private double completedPrice;

    public LocalDateTime completedTime(){
        return null;
    }

    public double getCompletedPrice() {
        return completedPrice;
    }

    public void setCompletedPrice(double completedPrice) {
        this.completedPrice = completedPrice;
    }
}