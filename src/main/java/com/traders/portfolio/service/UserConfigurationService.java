package com.traders.portfolio.service;

import com.google.common.base.Strings;
import com.traders.portfolio.constants.CacheName;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

@Service
public class UserConfigurationService {
    private final RedisService redisService;

    public UserConfigurationService(RedisService redisService) {
        this.redisService = redisService;
    }

    public HashMap<String,String> getUserConfValues(Long userId, Set<String> identityKeys) {
        HashMap<String, String> values = new HashMap<>();
        HashSet<String> failToFindInCache = new HashSet<>();
        if (identityKeys != null && !identityKeys.isEmpty()) {
            identityKeys.forEach(identityKey -> {
                String value = redisService.getValue(CacheName.USER_CONFIGURATION_CACHE, String.format("%s_%s", userId, identityKey));
                if (Strings.isNullOrEmpty(value)) {
                    failToFindInCache.add(identityKey);
                } else {
                    values.put(identityKey, value);
                }
            });
        }
        return values;
    }
}
