package com.traders.portfolio.web.rest.fign;

import com.traders.portfolio.properties.ConfigProperties;
import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {
    private final ConfigProperties configProperties;

    public FeignConfig(ConfigProperties configProperties) {
        this.configProperties = configProperties;
    }

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            requestTemplate.header("Authorization", "Bearer " + getAuthToken());
        };
    }

    private String getAuthToken() {
        return configProperties.getSecurity().getAuthentication().getJwt().getMachineToken();
    }
}
