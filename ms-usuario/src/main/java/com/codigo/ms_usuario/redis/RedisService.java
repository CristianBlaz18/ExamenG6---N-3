package com.codigo.ms_usuario.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    public void setCache(String key, String value, long timeoutInMinutes) {
        redisTemplate.opsForValue().set(key, value, timeoutInMinutes, TimeUnit.MINUTES);
    }

    public String getCache(String key) {
        return redisTemplate.opsForValue().get(key);
    }
}
