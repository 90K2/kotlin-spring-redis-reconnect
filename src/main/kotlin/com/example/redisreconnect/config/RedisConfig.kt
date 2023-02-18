package com.example.redisreconnect.config

import com.example.redisreconnect.config.RetryableJedisConnectionFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.StringRedisSerializer

@Configuration
class RedisConfig {

    @Value("\${spring.redis.host}")
    private val redisHostName: String = ""

    @Value("\${spring.redis.port}")
    private val redisPort: Int = 6379


    @Bean
    fun redisConfiguration() = RedisStandaloneConfiguration().apply {
        hostName = redisHostName
        port = redisPort
    }

    @Bean
    fun redisTemplate() = RedisTemplate<String, Any>().apply {
        setConnectionFactory(
                RetryableJedisConnectionFactory(redisConfiguration())
        )
        setDefaultSerializer(StringRedisSerializer())
    }
}
