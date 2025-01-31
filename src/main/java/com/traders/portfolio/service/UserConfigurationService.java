package com.traders.portfolio.service;

import com.google.common.base.Strings;
import com.traders.portfolio.constants.CacheName;
import com.traders.portfolio.domain.ConfigurationIdentityKey;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserConfigurationService {
    private final RedisService redisService;

    public UserConfigurationService(RedisService redisService) {
        this.redisService = redisService;
    }

    public HashMap<String, String> getUserConfigurations(Long userId, ConfigurationIdentityKey configurationIdentityKey) {
        HashMap<String, String> confs = new HashMap<>();
        HashSet<String> failToFindInCache = new HashSet<>();
        if (configurationIdentityKey != null) {
            configurationIdentityKey.getIdentityKeys().forEach(identityKey -> {
                String value = redisService.getValue(CacheName.USER_CONFIGURATION_CACHE, String.format("%s_%s", userId, identityKey));
                if (Strings.isNullOrEmpty(value)) {
                    failToFindInCache.add(identityKey);
                } else {
                    confs.put(identityKey, value);
                }
            });
        }
        return confs;
    }

    public HashMap<String, String> getUserConfigurations(Long userId, List<ConfigurationIdentityKey> configurationIdentityKeys) {
        HashMap<String, String> confs = new HashMap<>();
        if (configurationIdentityKeys != null && !configurationIdentityKeys.isEmpty()) {
            configurationIdentityKeys.forEach(identityKey -> {
                confs.putAll(this.getUserConfigurations(userId, identityKey));
            });
        }
        return confs;
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
