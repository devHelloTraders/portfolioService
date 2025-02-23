package com.traders.portfolio.web.rest.errors;

public enum TradeValidationErrorCode {
    INVALID_EXCHANGE_FOUND(90,"Invalid Exchange Found for traded script"),

    INSUFFICIENT_MARGIN(91,"Insufficient balance to place the order"),

    EQUITY_TRADING_DISABLED(11,"Equity trading is disabled for you"),
    EQUITY_TRADING_MINIMUM_QTY_NEEDED(12,"Minimum one qty/lot size is required"),
    EQUITY_TRADING_MAX_QTY_LIMIT(13,"Qty/lot Size is more than allowed Qty/lot size"),


    EQUITY_OPTION_TRADING_DISABLED(14,"Equity Options trading is disabled for you"),
    EQUITY_INDEX_OPTION_TRADING_MINIMUM_QTY_NEEDED(15,"Minimum one qty/lot size is required"),
    EQUITY_INDEX_OPTION_TRADING_MAX_QTY_LIMIT(16,"Qty/lot Size is more than allowed Qty/lot size"),

    EQUITY_INDEX_OPTION_SHORTSELLING_TRADING_MINIMUM_QTY_NEEDED(17,"Minimum one qty/lot size is required"),
    EQUITY_INDEX_OPTION_SHORTSELLING_TRADING_MAX_QTY_LIMIT(18,"Qty/lot Size is more than allowed Qty/lot size"),

    EQUITY_OPTION_TRADING_MINIMUM_QTY_NEEDED(19,"Minimum one qty/lot size is required"),
    EQUITY_OPTION_TRADING_MAX_QTY_LIMIT(20,"Qty/lot Size is more than allowed Qty/lot size"),

    EQUITY_OPTIONS_SHORTSELLING_TRADING_DISABLED(21,"Equity Options short selling is disabled for you"),
    EQUITY_OPTIONS_SHORTSELL_TRADING_MINIMUM_QTY_NEEDED(22,"Minimum one qty/lot size is required"),
    EQUITY_OPTIONS_SHORTSELL_TRADING_MAX_QTY_LIMIT(23,"Qty/lot Size is more than allowed Qty/lot size"),

    INDEX_OPTION_DISABLE(24,"Index option is disabled for you"),
    INDEX_OPTION_TRADING_MAX_QTY_LIMIT(25,"Qty/lot Size is more than allowed Qty/lot size"),

    INDEX_OPTION_SHORTSELLING_DISABLE(24,"Option Index short selling option is disabled for you"),
    INDEX_OPTION_SHORTSELLING_TRADING_MAX_QTY_LIMIT(25,"Qty/lot Size is more than allowed Qty/lot size"),

    MCX_TRADING_DISABLED(26,"MCX Trading is disabled for you"),
    MCX_TRADING_MINIMUM_QTY_NEEDED(27,"Minimum one qty/lot size is required"),
    MCX_TRADING_MAXIMUM_QTY_LIMIT(28,"Maximum one qty/lot size is required"),

    MCX_OPTION_TRADING_DISABLED(29,"MCX Options trading is disabled for you"),
    MCX_OPTION_TRADING_QTY_NEEDED(30,"Minimum one qty/lot size is required"),
    MCX_OPTION_TRADING_QTY_LIMIT(31,"Maximum one qty/lot size is required"),

    MCX_OPTION_SHORTSELL_TRADING_DISABLED(32,"MCX Options short selling option is disabled for you"),
    MCX_OPTION_SHORTSELL_QTY_NEEDED(33,"Minimum one qty/lot size is required"),
    MCX_OPTION_SHORTSELL_QTY_LIMIT(34,"Maximum one qty/lot size is required"),
    ;

    private final String errorMessage;
    private final int errorCode;

    TradeValidationErrorCode(int errorCode,String errorMessage) {
        this.errorMessage = errorMessage;
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public int getErrorCode() {
        return errorCode;
    }
}
