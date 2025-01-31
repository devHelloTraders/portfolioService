package com.traders.portfolio.domain;

import java.util.List;

import com.traders.portfolio.constants.IdentityKeysConst;

public enum ConfigurationIdentityKey {
    USER_CONFIGURATION {
        @Override
        public List<String> getIdentityKeys() {
            return List.of(
                    IdentityKeysConst.IS_DEMO_ACCOUNT,
                    IdentityKeysConst.ALLOW_FRESH_ENTRY_ORDER,
                    IdentityKeysConst.ALLOW_ORDERS_BETWEEN_HIGH_LOW,
                    IdentityKeysConst.TRADE_EQUITY_AS_UNITS,
                    IdentityKeysConst.ACCOUNT_STATUS,
                    IdentityKeysConst.AUTO_CLOSE_TRADES_IF_CONDITION_MET,
                    IdentityKeysConst.AUTO_CLOSE_ALL_ACTIVE_TRADES_ON_LOSSES_PERCENTAGE,
                    IdentityKeysConst.NOTIFY_CLIENT_ON_LOSSES_PERCENTAGE
            );
        }
    },
    MCX_FUTURES {
        @Override
        public List<String> getIdentityKeys() {
            return List.of(
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
        }
    },
    EQUITY_FUTURES {
        @Override
        public List<String> getIdentityKeys() {
            return List.of(
                    IdentityKeysConst.EQUITY_TRADING,
                    IdentityKeysConst.EQUITY_BROKERAGE_PER_CRORE,
                    IdentityKeysConst.MINIMUM_LOT_SIZE_PER_EQUITY_TRADE,
                    IdentityKeysConst.MAXIMUM_LOT_SIZE_PER_EQUITY_TRADE,
                    IdentityKeysConst.MINIMUM_LOT_SIZE_PER_EQUITY_INDEX_TRADE,
                    IdentityKeysConst.MAXIMUM_LOT_SIZE_PER_EQUITY_INDEX_TRADE,
                    IdentityKeysConst.MAX_LOT_SIZE_EQUITY_SCRIPT_OPEN_AT_A_TIME,
                    IdentityKeysConst.MAX_LOT_SIZE_EQUITY_INDEX_SCRIPT_OPEN_AT_A_TIME,
                    IdentityKeysConst.MAX_SIZE_ALL_EQUITY,
                    IdentityKeysConst.MAX_SIZE_ALL_INDEX,
                    IdentityKeysConst.INTRADAY_MARGIN_EQUITY,
                    IdentityKeysConst.HOLDING_MARGIN_EQUITY,
                    IdentityKeysConst.ORDER_PRICE_MARGIN_EQUITY
            );
        }
    },
    MCX_OPTIONS_CONFIG {
        @Override
        public List<String> getIdentityKeys() {
            return List.of(
                    IdentityKeysConst.MCX_OPTIONS_TRADING,
                    IdentityKeysConst.OPTIONS_MCX_BROKERAGE_TYPE,
                    IdentityKeysConst.OPTIONS_MCX_BROKERAGE,
                    IdentityKeysConst.OPTIONS_MIN_BID_PRICE,
                    IdentityKeysConst.MIN_LOT_SIZE_MCX_OPTIONS,
                    IdentityKeysConst.MAX_LOT_SIZE_MCX_OPTIONS,
                    IdentityKeysConst.MAX_LOT_SIZE_MCX_OPTIONS_OPEN_AT_A_TIME,
                    IdentityKeysConst.MAX_SIZE_ALL_MCX_OPTIONS,
                    IdentityKeysConst.INTRADAY_MARGIN_OPTIONS_MCX,
                    IdentityKeysConst.HOLDING_MARGIN_OPTIONS_MCX,
                    IdentityKeysConst.ORDER_PRICE_MARGIN_OPTIONS);
        }
    },
    EQUITY_OPTIONS_CONFIG {
        @Override
        public List<String> getIdentityKeys() {
            return List.of(
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
        }
    },
    EQUITY_INDEX_OPTIONS_CONFIG {
        @Override
        public List<String> getIdentityKeys() {
            return List.of(
                    IdentityKeysConst.EQUITY_OPTIONS_TRADING,
                    IdentityKeysConst.MINIMUM_LOT_SIZE_PER_EQUITY_INDEX_TRADE,
                    IdentityKeysConst.MAXIMUM_LOT_SIZE_PER_EQUITY_INDEX_TRADE,
                    IdentityKeysConst.MAX_LOT_SIZE_EQUITY_INDEX_OPTIONS_OPEN_AT_A_TIME,
                    IdentityKeysConst.MAX_SIZE_ALL_INDEX_OPTIONS,
                    IdentityKeysConst.INTRADAY_MARGIN_OPTIONS_INDEX,
                    IdentityKeysConst.HOLDING_MARGIN_OPTIONS_INDEX,
                    IdentityKeysConst.ORDER_PRICE_MARGIN_OPTIONS,
                    IdentityKeysConst.MIN_LOT_SIZE_EQUITY_INDEX_OPTIONS,
                    IdentityKeysConst.MAX_LOT_SIZE_EQUITY_INDEX_OPTIONS
            );
        }
    },
    INDEX_OPTIONS_CONFIG {
        @Override
        public List<String> getIdentityKeys() {
            return List.of(
                    IdentityKeysConst.INDEX_OPTIONS_TRADING,
                    IdentityKeysConst.OPTIONS_INDEX_BROKERAGE_TYPE,
                    IdentityKeysConst.OPTIONS_INDEX_BROKERAGE,
                    IdentityKeysConst.MAX_LOT_SIZE_EQUITY_INDEX_OPTIONS_OPEN_AT_A_TIME,
                    IdentityKeysConst.MAX_SIZE_ALL_INDEX_OPTIONS,
                    IdentityKeysConst.INTRADAY_MARGIN_OPTIONS_INDEX,
                    IdentityKeysConst.HOLDING_MARGIN_OPTIONS_INDEX,
                    IdentityKeysConst.ORDER_PRICE_MARGIN_OPTIONS
            );
        }
    },
    MCX_OPTIONS_SHORT_SELLING {
        @Override
        public List<String> getIdentityKeys() {
            return List.of(
                    IdentityKeysConst.MCX_OPTIONS_SHORT_SELL_ALLOWED,
                    IdentityKeysConst.OPTIONS_MCX_SHORTSELL_BROKERAGE_TYPE,
                    IdentityKeysConst.OPTIONS_MCX_SHORTSELL_BROKERAGE,
                    IdentityKeysConst.MIN_LOT_SIZE_MCX_OPTIONS_SHORTSELL,
                    IdentityKeysConst.MAX_LOT_SIZE_MCX_OPTIONS_SHORTSELL,
                    IdentityKeysConst.MAX_LOT_SIZE_MCX_OPTIONS_SHORTSELL_OPEN_AT_A_TIME,
                    IdentityKeysConst.MAX_SIZE_ALL_MCX_OPTIONS_SHORTSELL,
                    IdentityKeysConst.INTRADAY_MARGIN_OPTIONS_MCX_SHORTSELL,
                    IdentityKeysConst.HOLDING_MARGIN_OPTIONS_MCX_SHORTSELL
            );
        }
    },
    EQUITY_OPTIONS_SHORT_SELLING {
        @Override
        public List<String> getIdentityKeys() {
            return List.of(
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
        }
    },
    EQUITY_INDEX_OPTIONS_SHORT_SELLING {
        @Override
        public List<String> getIdentityKeys() {
            return List.of(
                    IdentityKeysConst.MIN_LOT_SIZE_EQUITY_INDEX_OPTIONS_SHORTSELL,
                    IdentityKeysConst.MAX_LOT_SIZE_EQUITY_INDEX_OPTIONS_SHORTSELL,
                    IdentityKeysConst.MAX_LOT_SIZE_EQUITY_INDEX_OPTIONS_SHORTSELL_OPEN_AT_A_TIME,
                    IdentityKeysConst.MAX_SIZE_ALL_INDEX_OPTIONS_SHORTSELL,
                    IdentityKeysConst.INTRADAY_MARGIN_OPTIONS_INDEX_SHORTSELL,
                    IdentityKeysConst.HOLDING_MARGIN_OPTIONS_INDEX_SHORTSELL
            );
        }
    },
    INDEX_OPTIONS_SHORT_SELLING {
        @Override
        public List<String> getIdentityKeys() {
            return List.of(
                    IdentityKeysConst.OPTIONS_INDEX_SHORT_SELL_ALLOWED,
                    IdentityKeysConst.OPTIONS_INDEX_SHORTSELL_BROKERAGE_TYPE,
                    IdentityKeysConst.OPTIONS_INDEX_SHORTSELL_BROKERAGE,
                    IdentityKeysConst.MAX_SIZE_ALL_INDEX_OPTIONS_SHORTSELL,
                    IdentityKeysConst.INTRADAY_MARGIN_OPTIONS_INDEX_SHORTSELL,
                    IdentityKeysConst.HOLDING_MARGIN_OPTIONS_INDEX_SHORTSELL
            );
        }
    };

    public abstract List<String> getIdentityKeys();
}
