package com.codigo.ms_usuario.redis;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RedisServiceTest {

    @Mock
    private StringRedisTemplate redisTemplate;

    @InjectMocks
    private RedisService redisService;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Configura el mock para opsForValue() para devolver el mock de ValueOperations
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    void setCache() {
        // Arrange
        String key = "testKey";
        String value = "testValue";
        long timeoutInMinutes = 5;

        // Act
        redisService.setCache(key, value, timeoutInMinutes);

        // Assert
        verify(valueOperations).set(key, value, timeoutInMinutes, TimeUnit.MINUTES);
    }

    @Test
    void getCache() {
        // Arrange
        String key = "testKey";
        String expectedValue = "testValue";
        when(valueOperations.get(key)).thenReturn(expectedValue);

        // Act
        String actualValue = redisService.getCache(key);

        // Assert
        verify(valueOperations).get(key);
        assertEquals(expectedValue, actualValue, "The returned value should match the expected value.");
    }
}