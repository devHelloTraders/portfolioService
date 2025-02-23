package com.traders.portfolio.domain;

import com.traders.portfolio.validations.AbstractConfigValidator;

import java.util.Optional;

import static java.util.Optional.empty;
import static java.util.Optional.of;

public enum Exchange {
    MCX {
        @Override
        public AbstractConfigValidator getConfigValidator(boolean isShortSell, Stock stock) {
            String stockInstrumentType=stock.getInstrumentType();
            Optional<InstrumentType> instrumentType=switch(stockInstrumentType){
                case "FUTCOM" -> of(InstrumentType.FUTCOM);
                case "OPTFUT" -> of(InstrumentType.OPTFUT);
                default -> empty();
            };
            return instrumentType.map(type -> type.getValidator(isShortSell)).orElse(null);
        }
    },
    NSE {
        @Override
        public AbstractConfigValidator getConfigValidator(boolean isShortSell, Stock stock) {
            String stockInstrumentType=stock.getInstrumentType();
            Optional<InstrumentType> instrumentType=switch(stockInstrumentType){
                case "FUTSTK" -> of(InstrumentType.FUTSTK);
                case "OPTSTK" -> of(InstrumentType.OPTSTK);
                case "OPTIDX" -> of(InstrumentType.OPTIDX);
                default -> empty();
            };
            return instrumentType.map(type -> type.getValidator(isShortSell)).orElse(null);
        }
    };

    public abstract AbstractConfigValidator getConfigValidator(boolean isShortSell, Stock stock);

    public static AbstractConfigValidator getAbstractConfigValidator(boolean isShortSell,Stock stock){
        String exchange=stock.getExchange();
        if("MCX".equalsIgnoreCase(exchange))
            return MCX.getConfigValidator(isShortSell,stock);
        else
            return NSE.getConfigValidator(isShortSell,stock);
    }
}
