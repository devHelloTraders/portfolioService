package com.traders.portfolio.validations.options;

import com.traders.portfolio.constants.IdentityKeysConst;
import com.traders.portfolio.domain.OrderValidity;
import com.traders.portfolio.service.UserConfigurationService;
import com.traders.portfolio.service.dto.TransactionRequest;
import com.traders.portfolio.validations.AbstractConfigValidator;
import com.traders.portfolio.validations.exception.TradeValidationException;
import com.traders.portfolio.web.rest.errors.TradeValidationErrorCode;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;

@Service
public class EquityOptionsConfValidator extends AbstractConfigValidator {
    private final Set<String> identityKeys = Set.of(
            IdentityKeysConst.EQUITY_OPTIONS_TRADING,
            IdentityKeysConst.OPTIONS_EQUITY_BROKERAGE_TYPE,
            IdentityKeysConst.OPTIONS_EQUITY_BROKERAGE,
            IdentityKeysConst.MIN_LOT_SIZE_EQUITY_OPTIONS,
            IdentityKeysConst.MAX_LOT_SIZE_EQUITY_OPTIONS,
            IdentityKeysConst.MAX_LOT_SIZE_EQUITY_OPTIONS_OPEN_AT_A_TIME,
            IdentityKeysConst.MAX_SIZE_ALL_EQUITY_OPTIONS,
            IdentityKeysConst.INTRADAY_MARGIN_OPTIONS_EQUITY,
            IdentityKeysConst.HOLDING_MARGIN_OPTIONS_EQUITY,
            IdentityKeysConst.ORDER_PRICE_MARGIN_OPTIONS
    );


    protected EquityOptionsConfValidator(UserConfigurationService userConfigurationService) {
        super(userConfigurationService);
    }

    @Override
    public Set<String> getValidationKeys() {
        return identityKeys;
    }

    @Override
    public void validate(TransactionRequest transactionRequest) throws TradeValidationException {
        Map<String,String> values=loadConfiguration(transactionRequest.getUserId());
        if(values!=null && !values.isEmpty()) {
            boolean tradingDisabled= "0".equalsIgnoreCase(values.getOrDefault(IdentityKeysConst.EQUITY_OPTIONS_TRADING,"0"));
            Double minimumQtyRequired = Double.valueOf(values.getOrDefault(IdentityKeysConst.MIN_LOT_SIZE_EQUITY_OPTIONS,"0"));
            Double maximumQtyRequired = Double.valueOf(values.getOrDefault(IdentityKeysConst.MAX_LOT_SIZE_EQUITY_OPTIONS,"0"));

            if(tradingDisabled)
                throw new TradeValidationException(TradeValidationErrorCode.EQUITY_OPTION_TRADING_DISABLED);

            if(transactionRequest.getAskedLotSize() < minimumQtyRequired)
                throw new TradeValidationException(TradeValidationErrorCode.EQUITY_OPTION_TRADING_MINIMUM_QTY_NEEDED,
                        String.format("Minimum %f qty/lot size is required",minimumQtyRequired));

            if(transactionRequest.getAskedLotSize() > maximumQtyRequired)
                throw new TradeValidationException(TradeValidationErrorCode.EQUITY_OPTION_TRADING_MAX_QTY_LIMIT,
                        String.format("Maximum allowed qty/lot size is %f",maximumQtyRequired));
        }
    }

    @Override
    public Double getMargin(Long userId, OrderValidity orderValidity) {
        return getMargin(userId,
                (orderValidity.equals(OrderValidity.INTRADAY) ?
                        IdentityKeysConst.INTRADAY_MARGIN_OPTIONS_EQUITY
                        : IdentityKeysConst.HOLDING_MARGIN_OPTIONS_EQUITY)
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
