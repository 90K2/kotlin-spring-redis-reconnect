package com.example.redisreconnect.config

import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory
import org.springframework.retry.RetryContext
import org.springframework.retry.RetryPolicy
import org.springframework.retry.backoff.BackOffPolicy
import org.springframework.retry.backoff.ExponentialBackOffPolicy
import org.springframework.retry.policy.SimpleRetryPolicy
import org.springframework.retry.support.RetryTemplate
import org.springframework.stereotype.Component
import redis.clients.jedis.Jedis
import java.time.Duration

@Component
class RetryableJedisConnectionFactory(
        standaloneConfig: RedisStandaloneConfiguration
) : JedisConnectionFactory(standaloneConfig) {

    init {
        super.afterPropertiesSet()
    }

    private val CONNECTION_RETRY_BACKOFF_INITIAL_INTERVAL: Duration = Duration.ofMillis(200)

    private val CONNECTION_RETRY_BACKOFF_MAX_INTERVAL: Duration = Duration.ofSeconds(3)

    private val CONNECTION_RETRY_BACKOFF_MULTIPLIER = 1.5

    private val CONNECTION_RETRY_MAX_ATTEMPTS = 100

    val retryTemplate = sessionRetryTemplate()

    @Bean
    fun sessionRetryTemplate(): RetryTemplate {
        val retryTemplate = RetryTemplate()
        retryTemplate.setBackOffPolicy(backOffPolicy())
        retryTemplate.setRetryPolicy(retryPolicy())
        return retryTemplate
    }

    fun backOffPolicy(): BackOffPolicy {
        val defaultBackOffPolicy = ExponentialBackOffPolicy()
        defaultBackOffPolicy.initialInterval = CONNECTION_RETRY_BACKOFF_INITIAL_INTERVAL.toMillis()
        defaultBackOffPolicy.maxInterval = CONNECTION_RETRY_BACKOFF_MAX_INTERVAL.toMillis()
        defaultBackOffPolicy.multiplier = CONNECTION_RETRY_BACKOFF_MULTIPLIER
        return defaultBackOffPolicy
    }

    fun retryPolicy(): RetryPolicy {
        return SimpleRetryPolicy(CONNECTION_RETRY_MAX_ATTEMPTS)
    }

    override fun fetchJedisConnector(): Jedis {
        return retryTemplate.execute<Jedis, RuntimeException> { context: RetryContext ->
                if (context.retryCount > 0) {
                    logger.warn("Retrying Redis connection. Retry Count = ${context.retryCount}")
                }
                super.fetchJedisConnector()
            }
    }

    private val logger = LoggerFactory.getLogger(this::class.simpleName)
}
