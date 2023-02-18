package com.example.redisreconnect.config

import org.springframework.stereotype.Component

@Component
class RedisReceiver(
) {

    /**
     * used by config.RedisConfig.listenerAdapter
     */
    fun receiveMessage(message: String) {
        println(message)
    }
}