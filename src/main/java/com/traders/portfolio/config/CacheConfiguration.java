package com.traders.portfolio.config;

import com.traders.common.properties.ConfigProperties;
import org.springframework.boot.autoconfigure.cache.JCacheManagerCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CacheConfiguration extends com.traders.common.config.CacheConfiguration{

//    @Bean
//    public ConfigProperties getConfigProperties(){
//        return new ConfigProperties();
//    }

    @Bean
    public JCacheManagerCustomizer cacheManagerCustomizer(javax.cache.configuration.Configuration<Object, Object> jcacheConfiguration) {
        return (cm) -> {
            cm.createCache("stockCache", jcacheConfiguration);
        };
    }
}
