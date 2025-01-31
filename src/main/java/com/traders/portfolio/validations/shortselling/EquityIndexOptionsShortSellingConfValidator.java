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
public class EquityIndexOptionsShortSellingConfValidator extends AbstractConfigValidator {
    private final Set<String> identityKeys = Set.of(
            IdentityKeysConst.MIN_LOT_SIZE_EQUITY_INDEX_OPTIONS_SHORTSELL,
            IdentityKeysConst.MAX_LOT_SIZE_EQUITY_INDEX_OPTIONS_SHORTSELL,
            IdentityKeysConst.MAX_LOT_SIZE_EQUITY_INDEX_OPTIONS_SHORTSELL_OPEN_AT_A_TIME,
            IdentityKeysConst.MAX_SIZE_ALL_INDEX_OPTIONS_SHORTSELL,
            IdentityKeysConst.INTRADAY_MARGIN_OPTIONS_INDEX_SHORTSELL,
            IdentityKeysConst.HOLDING_MARGIN_OPTIONS_INDEX_SHORTSELL
    );

    protected EquityIndexOptionsShortSellingConfValidator(UserConfigurationService userConfigurationService) {
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
                        IdentityKeysConst.INTRADAY_MARGIN_OPTIONS_INDEX_SHORTSELL
                        : IdentityKeysConst.HOLDING_MARGIN_OPTIONS_INDEX_SHORTSELL)
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
