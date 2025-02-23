package com.traders.portfolio.validations;

import com.traders.portfolio.domain.OrderValidity;
import com.traders.portfolio.service.UserConfigurationService;
import com.traders.portfolio.service.dto.TransactionRequest;
import com.traders.portfolio.validations.exception.TradeValidationException;

import java.util.Map;
import java.util.Set;

public abstract class AbstractConfigValidator {
    private final UserConfigurationService userConfigurationService;

    protected AbstractConfigValidator(UserConfigurationService userConfigurationService) {
        this.userConfigurationService = userConfigurationService;
    }

    protected Map<String,String> loadConfiguration(Long userId){
        return userConfigurationService.getUserConfValues(userId,getValidationKeys());
    }


    public abstract Set<String> getValidationKeys();
    public abstract void validate(TransactionRequest transactionRequest) throws TradeValidationException;
    public abstract Double getMargin(Long userId, OrderValidity orderValidity);
    //public abstract String getValueForIdentityKey(Long userId, String key) throws Exception;
}
