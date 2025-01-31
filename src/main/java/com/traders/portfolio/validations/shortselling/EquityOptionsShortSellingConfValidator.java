package com.traders.portfolio.validations.shortselling;

import com.traders.portfolio.constants.IdentityKeysConst;
import com.traders.portfolio.domain.OrderValidity;
import com.traders.portfolio.service.UserConfigurationService;
import com.traders.portfolio.validations.AbstractConfigValidator;
import com.traders.portfolio.validations.exception.ValidationException;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;

@Service
public class EquityOptionsShortSellingConfValidator extends AbstractConfigValidator {
    private final Set<String> identityKeys = Set.of(
            IdentityKeysConst.OPTIONS_EQUITY_SHORT_SELL_ALLOWED,
            IdentityKeysConst.OPTIONS_EQUITY_SHORTSELL_BROKERAGE_TYPE,
            IdentityKeysConst.OPTIONS_EQUITY_SHORTSELL_BROKERAGE,
            IdentityKeysConst.MIN_LOT_SIZE_EQUITY_OPTIONS_SHORTSELL,
            IdentityKeysConst.MAX_LOT_SIZE_EQUITY_OPTIONS_SHORTSELL,
            IdentityKeysConst.MAX_LOT_SIZE_EQUITY_OPTIONS_SHORTSELL_OPEN_AT_A_TIME,
            IdentityKeysConst.MAX_SIZE_ALL_EQUITY_OPTIONS_SHORTSELL,
            IdentityKeysConst.INTRADAY_MARGIN_OPTIONS_EQUITY_SHORTSELL,
            IdentityKeysConst.HOLDING_MARGIN_OPTIONS_EQUITY_SHORTSELL
    );

    protected EquityOptionsShortSellingConfValidator(UserConfigurationService userConfigurationService) {
        super(userConfigurationService);
    }

    @Override
    public Set<String> getValidationKeys() {
        return identityKeys;
    }

    @Override
    public void validate(Long userId) throws ValidationException {

    }

    @Override
    public Double getMargin(Long userId, OrderValidity orderValidity) {
        return getMargin(userId,
                (orderValidity.equals(OrderValidity.INTRADAY) ?
                        IdentityKeysConst.INTRADAY_MARGIN_OPTIONS_EQUITY_SHORTSELL
                        : IdentityKeysConst.HOLDING_MARGIN_OPTIONS_EQUITY_SHORTSELL)
        );
    }


    public Double getMargin(Long userId, String identityKey) {
        Map<String, String> values = loadConfiguration(userId);
        if (values != null && values.containsKey(identityKey)) {
            return Double.valueOf(values.get(identityKey));
        }
        return 0.0;
    }
}
