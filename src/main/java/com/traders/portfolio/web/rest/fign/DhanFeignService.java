package com.traders.portfolio.web.rest.fign;

import com.traders.portfolio.web.rest.model.DhanRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class DhanFeignService {
    private final DhanFignClient feignClient;

    public DhanFeignService(DhanFignClient feignClient) {
        this.feignClient = feignClient;
    }

    public void subScribeInstruments(DhanRequest request){
        try {
            this.feignClient.subScribeInstruments(request);
        }catch (RuntimeException re){
            log.error(re.getLocalizedMessage());
        }
    }
}
