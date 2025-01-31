package com.traders.portfolio.validations.future;

import com.traders.portfolio.constants.IdentityKeysConst;
import com.traders.portfolio.domain.OrderValidity;
import com.traders.portfolio.service.UserConfigurationService;
import com.traders.portfolio.validations.AbstractConfigValidator;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;

@Service
public class MCXFutureConfValidator extends AbstractConfigValidator {

    private final Set<String> identityKeys = Set.of(
            IdentityKeysConst.MCX_TRADING,
            IdentityKeysConst.MIN_LOT_SIZE_MCX,
            IdentityKeysConst.MAX_LOT_SIZE_MCX,
            IdentityKeysConst.MAX_LOT_SIZE_MCX_SCRIPT_OPEN_AT_A_TIME,
            IdentityKeysConst.MAX_SIZE_ALL_COMMODITY,
            IdentityKeysConst.MCX_BROKERAGE_TYPE,
            IdentityKeysConst.MCX_BROKERAGE,
            IdentityKeysConst.EXPOSE_MCX_TYPE,
            IdentityKeysConst.INTRADAY_MARGIN_MCX,
            IdentityKeysConst.HOLDING_MARGIN_MCX
    );


    protected MCXFutureConfValidator(UserConfigurationService userConfigurationService) {
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
                        IdentityKeysConst.INTRADAY_MARGIN_MCX
                        : IdentityKeysConst.HOLDING_MARGIN_MCX)
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
