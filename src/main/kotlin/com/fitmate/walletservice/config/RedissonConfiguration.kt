package com.fitmate.walletservice.config

import org.redisson.Redisson
import org.redisson.api.RedissonClient
import org.redisson.config.Config
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RedissonConfiguration {

    @Value("\${spring.redis.port}")
    private val port = 0

    @Value("\${spring.redis.host}")
    private val host: String? = null

    private val REDISSON_HOST_PREFIX = "redis://"

    @Bean
    fun redissonClient(): RedissonClient {
        val config = Config()
        config.useSingleServer().setAddress("$REDISSON_HOST_PREFIX$host:$port")

        return Redisson.create(config)
    }
}