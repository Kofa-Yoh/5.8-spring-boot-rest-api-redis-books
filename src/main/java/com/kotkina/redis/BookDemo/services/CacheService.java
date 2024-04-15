package com.kotkina.redis.BookDemo.services;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class CacheService {

    private final RedisTemplate<String, ?> redisTemplate;

    public void evictCachesByPrefix(String prefix) {
        Set<String> keys = redisTemplate.keys(prefix + "*");
        if (keys != null) {
            redisTemplate.delete(keys);
        }
    }
}
