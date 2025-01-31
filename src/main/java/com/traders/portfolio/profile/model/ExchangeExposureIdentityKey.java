package com.traders.portfolio.profile.model;

import com.traders.portfolio.constants.IdentityKeysConst;
import com.traders.portfolio.profile.dto.ConfigurationDetails;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public enum ExchangeExposureIdentityKey {
    EQUITY_FUTURE {
        @Override
        public String getSegmentName() {
            return "NSE";
        }

        @Override
        public Set<String> getSegmentIdentityKeys() {
            return Set.of(
                    IdentityKeysConst.EQUITY_TRADING,
                    IdentityKeysConst.EQUITY_BROKERAGE_PER_CRORE,
                    IdentityKeysConst.INTRADAY_MARGIN_EQUITY,
                    IdentityKeysConst.HOLDING_MARGIN_EQUITY
            );
        }

        @Override
        public List<ConfigurationDetails> getSegmentConfigurationDetails(Map<String, String> confValues) {
            List<ConfigurationDetails> configurationDetails = new ArrayList<>();
            configurationDetails.add(new ConfigurationDetails("Brokerage",
                    String.format("%s %s", confValues.getOrDefault(IdentityKeysConst.EQUITY_BROKERAGE_PER_CRORE, ""), "Per Crore")
            ));
            configurationDetails.add(new ConfigurationDetails("Margin Intraday",
                    String.format("Turnover / %s", confValues.getOrDefault(IdentityKeysConst.INTRADAY_MARGIN_EQUITY, "")))
            );
            configurationDetails.add(new ConfigurationDetails("Margin Holding",
                    String.format("Turnover / %s", confValues.getOrDefault(IdentityKeysConst.HOLDING_MARGIN_EQUITY, "")))
            );
            return configurationDetails;
        }

        @Override
        public boolean isTradingEnabled(Map<String,String> confValues) {
            return confValues.getOrDefault(IdentityKeysConst.EQUITY_TRADING, "0").equalsIgnoreCase("1");
        }


    },
    EQUITY_OPTION {
        @Override
        public String getSegmentName() {
            return "Stock Options";
        }

        @Override
        public Set<String> getSegmentIdentityKeys() {
            return Set.of(
                    IdentityKeysConst.EQUITY_OPTIONS_TRADING,
                    IdentityKeysConst.INTRADAY_MARGIN_OPTIONS_EQUITY,
                    IdentityKeysConst.HOLDING_MARGIN_OPTIONS_EQUITY,
                    IdentityKeysConst.OPTIONS_EQUITY_BROKERAGE_TYPE,
                    IdentityKeysConst.OPTIONS_EQUITY_BROKERAGE
            );
        }

        @Override
        public List<ConfigurationDetails> getSegmentConfigurationDetails(Map<String, String> confValues) {
            List<ConfigurationDetails> configurationDetails = new ArrayList<>();
            configurationDetails.add(new ConfigurationDetails("Brokerage",
                    String.format("%s %s", confValues.getOrDefault(IdentityKeysConst.OPTIONS_EQUITY_BROKERAGE, ""),
                            confValues.getOrDefault(IdentityKeysConst.OPTIONS_EQUITY_BROKERAGE_TYPE, ""))
            ));
            configurationDetails.add(new ConfigurationDetails("Margin Intraday",
                    String.format("Turnover / %s", confValues.getOrDefault(IdentityKeysConst.INTRADAY_MARGIN_OPTIONS_EQUITY, "")))
            );
            configurationDetails.add(new ConfigurationDetails("Margin Holding",
                    String.format("Turnover / %s", confValues.getOrDefault(IdentityKeysConst.HOLDING_MARGIN_OPTIONS_EQUITY, "")))
            );
            return configurationDetails;
        }

        @Override
        public boolean isTradingEnabled(Map<String,String> confValues) {
            return confValues.getOrDefault(IdentityKeysConst.EQUITY_OPTIONS_TRADING, "0").equalsIgnoreCase("1");
        }


    },
    EQUITY_INDEX_OPTION {
        @Override
        public String getSegmentName() {
            return "Index Options";
        }

        @Override
        public Set<String> getSegmentIdentityKeys() {
            return Set.of(
                    IdentityKeysConst.INDEX_OPTIONS_TRADING,
                    IdentityKeysConst.OPTIONS_INDEX_BROKERAGE_TYPE,
                    IdentityKeysConst.OPTIONS_INDEX_BROKERAGE,
                    IdentityKeysConst.INTRADAY_MARGIN_OPTIONS_INDEX,
                    IdentityKeysConst.HOLDING_MARGIN_OPTIONS_INDEX
            );
        }

        @Override
        public List<ConfigurationDetails> getSegmentConfigurationDetails(Map<String, String> confValues) {
            List<ConfigurationDetails> configurationDetails = new ArrayList<>();
            configurationDetails.add(new ConfigurationDetails("Brokerage",
                    String.format("%s %s", confValues.getOrDefault(IdentityKeysConst.OPTIONS_INDEX_BROKERAGE, ""),
                            confValues.getOrDefault(IdentityKeysConst.OPTIONS_INDEX_BROKERAGE_TYPE, ""))
            ));
            configurationDetails.add(new ConfigurationDetails("Margin Intraday",
                    String.format("Turnover / %s", confValues.getOrDefault(IdentityKeysConst.INTRADAY_MARGIN_OPTIONS_INDEX, "")))
            );
            configurationDetails.add(new ConfigurationDetails("Margin Holding",
                    String.format("Turnover / %s", confValues.getOrDefault(IdentityKeysConst.HOLDING_MARGIN_OPTIONS_INDEX, "")))
            );
            return configurationDetails;
        }

        @Override
        public boolean isTradingEnabled(Map<String,String> confValues) {
            return confValues.getOrDefault(IdentityKeysConst.INDEX_OPTIONS_TRADING, "0").equalsIgnoreCase("1");
        }


    },
    MCX_OPTION {
        @Override
        public String getSegmentName() {
            return "MCX Options";
        }

        @Override
        public Set<String> getSegmentIdentityKeys() {
            return Set.of(
                    IdentityKeysConst.MCX_OPTIONS_TRADING,
                    IdentityKeysConst.OPTIONS_MCX_BROKERAGE_TYPE,
                    IdentityKeysConst.OPTIONS_MCX_BROKERAGE,
                    IdentityKeysConst.INTRADAY_MARGIN_OPTIONS_MCX,
                    IdentityKeysConst.HOLDING_MARGIN_OPTIONS_MCX
            );
        }

        @Override
        public List<ConfigurationDetails> getSegmentConfigurationDetails(Map<String, String> confValues) {
            List<ConfigurationDetails> configurationDetails = new ArrayList<>();
            configurationDetails.add(new ConfigurationDetails("Brokerage",
                    String.format("%s %s", confValues.getOrDefault(IdentityKeysConst.OPTIONS_MCX_BROKERAGE, ""),
                            confValues.getOrDefault(IdentityKeysConst.OPTIONS_MCX_BROKERAGE_TYPE, ""))
            ));
            configurationDetails.add(new ConfigurationDetails("Margin Intraday",
                    String.format("Turnover / %s", confValues.getOrDefault(IdentityKeysConst.INTRADAY_MARGIN_OPTIONS_MCX, "")))
            );
            configurationDetails.add(new ConfigurationDetails("Margin Holding",
                    String.format("Turnover / %s", confValues.getOrDefault(IdentityKeysConst.HOLDING_MARGIN_OPTIONS_MCX, "")))
            );
            return configurationDetails;
        }

        @Override
        public boolean isTradingEnabled(Map<String,String> confValues) {
            return confValues.getOrDefault(IdentityKeysConst.MCX_OPTIONS_TRADING, "0").equalsIgnoreCase("1");
        }
    },
    MCX_FUTURE {
        @Override
        public String getSegmentName() {
            return "MCX";
        }

        @Override
        public Set<String> getSegmentIdentityKeys() {
            return Set.of(
                    IdentityKeysConst.MCX_TRADING,
                    IdentityKeysConst.MCX_BROKERAGE_TYPE,
                    IdentityKeysConst.MCX_BROKERAGE,
                    IdentityKeysConst.INTRADAY_MARGIN_MCX,
                    IdentityKeysConst.HOLDING_MARGIN_MCX,
                    IdentityKeysConst.EXPOSE_MCX_TYPE
            );
        }

        @Override
        public List<ConfigurationDetails> getSegmentConfigurationDetails(Map<String, String> confValues) {
            List<ConfigurationDetails> configurationDetails = new ArrayList<>();
            configurationDetails.add(new ConfigurationDetails("Exposure Type",
                    confValues.getOrDefault(IdentityKeysConst.EXPOSE_MCX_TYPE, "")
            ));
            configurationDetails.add(new ConfigurationDetails("Brokerage",
                    String.format("%s %s", confValues.getOrDefault(IdentityKeysConst.MCX_BROKERAGE, ""),
                            confValues.getOrDefault(IdentityKeysConst.MCX_BROKERAGE_TYPE, ""))
            ));
            configurationDetails.add(new ConfigurationDetails("Margin Intraday",
                    String.format("Turnover / %s", confValues.getOrDefault(IdentityKeysConst.INTRADAY_MARGIN_MCX, "")))
            );
            configurationDetails.add(new ConfigurationDetails("Margin Holding",
                    String.format("Turnover / %s", confValues.getOrDefault(IdentityKeysConst.HOLDING_MARGIN_MCX, "")))
            );
            return configurationDetails;
        }

        @Override
        public boolean isTradingEnabled(Map<String,String> confValues) {
            return confValues.getOrDefault(IdentityKeysConst.MCX_TRADING, "0").equalsIgnoreCase("1");
        }
    };

    public abstract String getSegmentName();

    public abstract Set<String> getSegmentIdentityKeys();

    public abstract List<ConfigurationDetails> getSegmentConfigurationDetails(Map<String, String> confValues);
    public abstract boolean isTradingEnabled(Map<String,String> confValues);
}
