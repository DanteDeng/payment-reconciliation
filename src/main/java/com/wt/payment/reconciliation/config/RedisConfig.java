package com.wt.payment.reconciliation.config;

import com.alibaba.fastjson.support.spring.FastJsonRedisSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Redis配置
 */
@Configuration
public class RedisConfig {

    /**
     * redis操作模板实例
     * @param factory redis连接工厂
     * @return redis操作模板实例
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new FastJsonRedisSerializer<>(Object.class));
        redisTemplate.setValueSerializer(new FastJsonRedisSerializer<>(Object.class));
        redisTemplate.setStringSerializer(new StringRedisSerializer());
        redisTemplate.setConnectionFactory(factory);

        return redisTemplate;
    }

    /**
     * redis hash操作模板实例
     * @param redisTemplate redis操作模板实例
     * @return hash操作模板实例
     */
    @Bean
    public HashOperations<String, String, Object> hashOperations(RedisTemplate<String, Object> redisTemplate) {
        return redisTemplate.opsForHash();
    }

    /**
     * redis value操作模板实例
     * @param redisTemplate redis操作模板实例
     * @return value操作模板实例
     */
    @Bean
    public ValueOperations<String, String> valueOperations(RedisTemplate<String, String> redisTemplate) {
        return redisTemplate.opsForValue();
    }

    /**
     * redis list操作模板实例
     * @param redisTemplate redis操作模板实例
     * @return list操作模板实例
     */
    @Bean
    public ListOperations<String, Object> listOperations(RedisTemplate<String, Object> redisTemplate) {
        return redisTemplate.opsForList();
    }

    /**
     * redis set操作模板实例
     * @param redisTemplate redis操作模板实例
     * @return set操作模板实例
     */
    @Bean
    public SetOperations<String, Object> setOperations(RedisTemplate<String, Object> redisTemplate) {
        return redisTemplate.opsForSet();
    }

    /**
     * redis ZSet操作模板实例
     * @param redisTemplate redis操作模板实例
     * @return ZSet操作模板实例
     */
    @Bean
    public ZSetOperations<String, Object> zSetOperations(RedisTemplate<String, Object> redisTemplate) {
        return redisTemplate.opsForZSet();
    }
}

