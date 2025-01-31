package com.traders.portfolio.profile.service;

import com.traders.portfolio.profile.dto.ExposureConfigurationDTO;
import com.traders.portfolio.profile.model.ExchangeExposureIdentityKey;
import com.traders.portfolio.service.UserConfigurationService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ClientProfileService {
    private final UserConfigurationService userConfigurationService;

    public ClientProfileService(UserConfigurationService userConfigurationService) {
        this.userConfigurationService = userConfigurationService;
    }

    public List<ExposureConfigurationDTO> getClientExposure(Long userId) {
        List<ExchangeExposureIdentityKey> exposureConfigurationKeys = List.of(
                ExchangeExposureIdentityKey.EQUITY_FUTURE,
                ExchangeExposureIdentityKey.EQUITY_INDEX_OPTION,
                ExchangeExposureIdentityKey.EQUITY_OPTION,
                ExchangeExposureIdentityKey.MCX_FUTURE,
                ExchangeExposureIdentityKey.MCX_OPTION
        );

       return exposureConfigurationKeys.stream().map(configuration -> {
            Map<String, String> values = userConfigurationService.getUserConfValues(userId, configuration.getSegmentIdentityKeys());
            return new ExposureConfigurationDTO(
                    configuration.getSegmentName(),
                    configuration.getSegmentConfigurationDetails(values),
                    configuration.isTradingEnabled(values)
            );
        }).toList();
    }

}
