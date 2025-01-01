package com.traders.portfolio.web.rest.fign;

import com.traders.common.model.MarketDetailsRequest;
import com.traders.common.model.MarketQuotes;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@Service
@FeignClient(name = "exchangeService", url = "${gateway.url}" ,configuration = FeignConfig.class)
public interface DhanFignClient {

    @PostMapping("/api/exchange/machine/subscribe")
    ResponseEntity<Void> subScribeInstruments(@RequestBody MarketDetailsRequest request);

    @PostMapping("/api/exchange/machine/quotes")
    ResponseEntity<Map<String, MarketQuotes>> getQuotesFromMarketList(@RequestBody MarketDetailsRequest request);
}
