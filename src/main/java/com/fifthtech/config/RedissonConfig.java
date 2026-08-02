package com.fifthtech.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RedissonConfig
 *
 * <p>显式构造 {@link RedissonClient} Bean，复用 {@code spring.data.redis.*} 配置；
 * starter 自带的 {@code RedissonAutoConfiguration} 因 {@code @ConditionalOnMissingBean(RedissonClient.class)}
 * 而跳过，与现有 {@code StringRedisTemplate} 共存同一 Redis 实例。</p>
 *
 * @author RH
 * @description C2 Redisson 客户端：与 Spring Data Redis 共享连接配置
 * @date 2026-08-02
 */
@Configuration
public class RedissonConfig {

    @Bean(destroyMethod = "shutdown")
    public RedissonClient redissonClient(RedisProperties properties) {
        Config config = new Config();
        String address = "redis://" + properties.getHost() + ":" + properties.getPort();
        config.useSingleServer()
                .setAddress(address)
                .setDatabase(properties.getDatabase())
                .setPassword(properties.getPassword())
                .setConnectionMinimumIdleSize(2)
                .setConnectionPoolSize(8);
        return Redisson.create(config);
    }
}
