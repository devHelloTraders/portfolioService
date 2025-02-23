package com.traders.portfolio.validations.account;

import com.traders.portfolio.constants.IdentityKeysConst;
import com.traders.portfolio.domain.OrderValidity;
import com.traders.portfolio.service.UserConfigurationService;
import com.traders.portfolio.service.dto.TransactionRequest;
import com.traders.portfolio.validations.AbstractConfigValidator;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;

@Service
public class TradingClientDefaultValidator extends AbstractConfigValidator {

    private final Set<String> identityKeys = Set.of(
            IdentityKeysConst.IS_DEMO_ACCOUNT,
            IdentityKeysConst.ALLOW_FRESH_ENTRY_ORDER,
            IdentityKeysConst.ALLOW_ORDERS_BETWEEN_HIGH_LOW,
            IdentityKeysConst.TRADE_EQUITY_AS_UNITS,
            IdentityKeysConst.ACCOUNT_STATUS,
            IdentityKeysConst.AUTO_CLOSE_TRADES_IF_CONDITION_MET,
            IdentityKeysConst.AUTO_CLOSE_ALL_ACTIVE_TRADES_ON_LOSSES_PERCENTAGE,
            IdentityKeysConst.NOTIFY_CLIENT_ON_LOSSES_PERCENTAGE
    );

    protected TradingClientDefaultValidator(UserConfigurationService userConfigurationService) {
        super(userConfigurationService);
    }

    @Override
    public Set<String> getValidationKeys() {
        return identityKeys;
    }

    @Override
    public void validate(TransactionRequest transactionRequest) {
        Map<String, String> confValues = loadConfiguration(transactionRequest.getUserId());
    }

    @Override
    public Double getMargin(Long userId, OrderValidity orderValidity) {
        return 0.0;
    }
}
