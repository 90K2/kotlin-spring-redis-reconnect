package com.example.redisreconnect.config

import org.springframework.context.annotation.Bean
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.listener.PatternTopic
import org.springframework.data.redis.listener.RedisMessageListenerContainer
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter
import org.springframework.stereotype.Component

@Component
class RedisConnector {

    @Bean
    fun container(
            connectionFactory: RedisConnectionFactory,
            listenerAdapter: MessageListenerAdapter
    ) = RedisMessageListenerContainer().apply {
        setConnectionFactory(connectionFactory)
        addMessageListener(listenerAdapter, PatternTopic("redis_topic_name"))
    }

    @Bean
    fun listenerAdapter(receiver: RedisReceiver) =
            MessageListenerAdapter(receiver, "receiveMessage")

}