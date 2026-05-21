package com.ruoyi.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonConfigTemplate {
    @Value("${redisson.hostName}")
    private String hostName;
    @Value("${redisson.port}")
    private String port;
    @Value("${redisson.password}")
    private String password;

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        String address = "redis://" + hostName + ":" + port;
        config.useSingleServer().setAddress(address).setPassword(password);
        return Redisson.create(config);
    }
}
