package com.traders.portfolio.domain;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum OrderCategory {
    LIMIT, STOP_LOSS, FORCE, REGULAR
}