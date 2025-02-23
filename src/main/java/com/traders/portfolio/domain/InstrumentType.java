package com.traders.portfolio.domain;

import com.traders.portfolio.utils.PortfolioServiceContext;
import com.traders.portfolio.validations.AbstractConfigValidator;
import com.traders.portfolio.validations.future.EquityFutureConfValidator;
import com.traders.portfolio.validations.future.MCXFutureConfValidator;
import com.traders.portfolio.validations.options.EquityIndexOptionsConfValidator;
import com.traders.portfolio.validations.options.EquityOptionsConfValidator;
import com.traders.portfolio.validations.options.MCXOptionsConfValidator;

public enum InstrumentType {
    FUTCOM {
        @Override
        AbstractConfigValidator getValidator(boolean isShortSell) {
            return PortfolioServiceContext.getBean(MCXFutureConfValidator.class);
        }
    },
    OPTFUT {
        @Override
        AbstractConfigValidator getValidator(boolean isShortSell) {
            return PortfolioServiceContext.getBean(MCXOptionsConfValidator.class);
        }
    },
    FUTSTK {
        @Override
        AbstractConfigValidator getValidator(boolean isShortSell) {
            return PortfolioServiceContext.getBean(EquityFutureConfValidator.class);
        }
    },
    OPTSTK {
        @Override
        AbstractConfigValidator getValidator(boolean isShortSell) {
            return PortfolioServiceContext.getBean(EquityOptionsConfValidator.class);
        }
    },
    OPTIDX {
        @Override
        AbstractConfigValidator getValidator(boolean isShortSell) {
            return PortfolioServiceContext.getBean(EquityIndexOptionsConfValidator.class);
        }
    };

    abstract AbstractConfigValidator getValidator(boolean isShortSell);
}
