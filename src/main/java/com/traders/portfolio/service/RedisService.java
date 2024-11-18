package com.traders.portfolio.service;

import com.traders.common.utils.CommonValidations;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.cache.Cache;
import javax.cache.CacheManager;
import java.util.Iterator;

@Service
public class RedisService {
//    private final StringRedisTemplate stringRedisTemplate;
//
//    public RedisService(StringRedisTemplate stringRedisTemplate) {
//        this.stringRedisTemplate = stringRedisTemplate;
//    }
//
//    public String getValue(String key) {
//        return stringRedisTemplate.opsForValue().get(key);
//    }


    private final CacheManager cacheManager;

    public RedisService(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    public String getValue( String key) {
        Cache<String, String> cache = cacheManager.getCache("stockCache");
        if (cache != null) {
            return cache.get(key);
        }
        return null;
    }
    public Double getDoubleValue( String key) {
        return CommonValidations.getNumber(getValue(key),Double.class);
    }

    public void saveToCache(String cacheName, Object key, Object value) {
        Cache<Object, Object> cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            cache.put(key, value);
        }
    }
}
