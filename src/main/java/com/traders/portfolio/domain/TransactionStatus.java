package com.traders.portfolio.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum TransactionStatus {

    COMPLETED {
        @Override
        public LocalDateTime completedTime() {
            return LocalDateTime.now();
        }
    },
    PENDING,
    CANCELLED;

    private Double executedPrice; // Nullable, no default value

    public LocalDateTime completedTime() {
        return null;
    }

    public Double getExecutedPrice() {
        return executedPrice;
    }

    public void setExecutedPrice(Double executedPrice) {
        this.executedPrice = executedPrice;
    }

    // Serialize enum as JSON with its properties
    @JsonValue
    public Map<String, Object> toJson() {
        Map<String, Object> json = new HashMap<>();
        json.put("name", this.name());
        json.put("executedPrice", this.executedPrice);
        return json;
    }

    // Deserialize JSON to enum
    @JsonCreator
    public static TransactionStatus fromJson(Map<String, Object> json) {
        String name = (String) json.get("name");
        TransactionStatus status = TransactionStatus.valueOf(name);
        if (json.containsKey("executedPrice")) {
            status.setExecutedPrice((Double) json.get("executedPrice"));
        }
        return status;
    }
}
