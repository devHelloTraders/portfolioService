package com.traders.portfolio.profile.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ExposureConfigurationDTO {
    String segment;
    List<ConfigurationDetails> details;
    boolean isEnabled;
}

