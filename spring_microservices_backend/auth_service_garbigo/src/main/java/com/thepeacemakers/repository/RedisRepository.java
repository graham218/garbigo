package com.thepeacemakers.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Repository
public class RedisRepository {
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    public void save(String key, Object value, long timeout, TimeUnit unit) {
        redisTemplate.opsForValue().set(key, value, timeout, unit);
    }
    
    public void save(String key, Object value, Duration duration) {
        redisTemplate.opsForValue().set(key, value, duration);
    }
    
    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }
    
    public void delete(String key) {
        redisTemplate.delete(key);
    }
    
    public boolean exists(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }
    
    public void setExpire(String key, long timeout, TimeUnit unit) {
        redisTemplate.expire(key, timeout, unit);
    }
    
    // Auth specific methods
    public void saveJwtBlacklist(String token, long expiry) {
        String key = "jwt:blacklist:" + token;
        save(key, "blacklisted", expiry, TimeUnit.MILLISECONDS);
    }
    
    public boolean isJwtBlacklisted(String token) {
        String key = "jwt:blacklist:" + token;
        return exists(key);
    }
    
    public void saveLoginAttempt(String email, int attempts) {
        String key = "login:attempts:" + email;
        save(key, attempts, Duration.ofMinutes(30));
    }
    
    public Integer getLoginAttempts(String email) {
        String key = "login:attempts:" + email;
        Object attempts = get(key);
        return attempts != null ? (Integer) attempts : 0;
    }
    
    public void clearLoginAttempts(String email) {
        String key = "login:attempts:" + email;
        delete(key);
    }
}