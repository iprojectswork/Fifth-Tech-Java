package com.fifthtech.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author RH
 * @ClassName RedissonConfig
 * @description: Redisson配置
 * @date 2026年08月02日
 * @version: 1.0
 */
@Configuration
public class RedissonConfig {

    /**
    * @description: 显式构造 RedissonClient，复用 spring.data.redis 配置；与 StringRedisTemplate 共存同一 Redis 实例
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [properties]
    * @return: {@link RedissonClient}
    **/
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