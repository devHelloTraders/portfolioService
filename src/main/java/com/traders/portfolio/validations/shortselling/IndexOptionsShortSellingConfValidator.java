package com.traders.portfolio.validations.shortselling;

import com.traders.portfolio.constants.IdentityKeysConst;
import com.traders.portfolio.domain.OrderValidity;
import com.traders.portfolio.service.UserConfigurationService;
import com.traders.portfolio.validations.AbstractConfigValidator;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;

@Service
public class IndexOptionsShortSellingConfValidator extends AbstractConfigValidator {

    private final Set<String> identityKeys = Set.of(
            IdentityKeysConst.OPTIONS_INDEX_SHORT_SELL_ALLOWED,
            IdentityKeysConst.OPTIONS_INDEX_SHORTSELL_BROKERAGE_TYPE,
            IdentityKeysConst.OPTIONS_INDEX_SHORTSELL_BROKERAGE,
            IdentityKeysConst.MAX_SIZE_ALL_INDEX_OPTIONS_SHORTSELL,
            IdentityKeysConst.INTRADAY_MARGIN_OPTIONS_INDEX_SHORTSELL,
            IdentityKeysConst.HOLDING_MARGIN_OPTIONS_INDEX_SHORTSELL
    );

    protected IndexOptionsShortSellingConfValidator(UserConfigurationService userConfigurationService) {
        super(userConfigurationService);
    }

    @Override
    public Set<String> getValidationKeys() {
        return identityKeys;
    }

    @Override
    public void validate(Long userId) {

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
