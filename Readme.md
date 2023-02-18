# Read Me First

Example of Spring Boot Kotlin project Redis configuration with ability to reconnect

Configuration classes: 

- `RedisConfig`
- `RetryableJedisConnectionFactory`

Add topic listener inside `RedisConnector.container()` addMessageListener

Entry point to handle incoming event: `RedisReceiver.receiveMessage()`