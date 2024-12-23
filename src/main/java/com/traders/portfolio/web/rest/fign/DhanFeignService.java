package com.traders.portfolio.web.rest.fign;

import com.traders.common.model.InstrumentInfo;
import com.traders.common.model.MarkestDetailsRequest;
import com.traders.common.model.MarketQuotes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class DhanFeignService {
    private final DhanFignClient feignClient;

    public DhanFeignService(DhanFignClient feignClient) {
        this.feignClient = feignClient;
    }

    public void subScribeInstruments(MarkestDetailsRequest request){
        try {
            this.feignClient.subScribeInstruments(request);
        }catch (RuntimeException re){
            log.error(re.getLocalizedMessage());
        }
    }

    public Map<String, MarketQuotes> getQuotesFromMarketList(MarkestDetailsRequest request){
        try {
            var response = this.feignClient.getQuotesFromMarketList(request);
            return response.getBody();
        }catch (RuntimeException re){
            log.error(re.getLocalizedMessage());
            return new HashMap<>();
        }
    }
}
