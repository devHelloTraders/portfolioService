package com.traders.portfolio.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserConfigResponse {
    private String identityKey;
    private String value;
}
