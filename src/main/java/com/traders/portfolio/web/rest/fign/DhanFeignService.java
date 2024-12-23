package com.traders.portfolio.web.rest.fign;

import com.traders.common.model.MarketDetailsRequest;
import com.traders.common.model.MarketQuotes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class DhanFeignService {
    private final DhanFignClient feignClient;

    public DhanFeignService(DhanFignClient feignClient) {
        this.feignClient = feignClient;
    }

    public void subScribeInstruments(MarketDetailsRequest request){
        try {
            this.feignClient.subScribeInstruments(request);
        }catch (RuntimeException re){
            log.error(re.getLocalizedMessage());
        }
    }

    public Map<String, MarketQuotes> getQuotesFromMarketList(MarketDetailsRequest request){
        try {
            var response = this.feignClient.getQuotesFromMarketList(request);
            return response.getBody();
        }catch (RuntimeException re){
            log.error(re.getLocalizedMessage());
            return new HashMap<>();
        }
    }
}
