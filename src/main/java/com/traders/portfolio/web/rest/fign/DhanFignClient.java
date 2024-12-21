package com.traders.portfolio.web.rest.fign;

import com.traders.portfolio.web.rest.model.DhanRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Service
@FeignClient(name = "exchangeService", url = "${gateway.url}" ,configuration = FeignConfig.class)
public interface DhanFignClient {

    @PostMapping("/api/exchange/machine/subscribe")
    ResponseEntity<Void> subScribeInstruments(@RequestBody DhanRequest request);

}
