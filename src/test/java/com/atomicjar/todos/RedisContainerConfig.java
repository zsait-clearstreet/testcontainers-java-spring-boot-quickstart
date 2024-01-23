package com.atomicjar.todos;

import com.redis.testcontainers.RedisContainer;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;

public class RedisContainerConfig {
    @Container
    @ServiceConnection
    static RedisContainer redisContainer =
            new RedisContainer(DockerImageName.parse("redis:6.0.20"));

    @DynamicPropertySource
    static void redisProperties(DynamicPropertyRegistry registry) {

        if (!redisContainer.isRunning()) redisContainer.start();

        registry.add(
                "cache.redis.server",
                () -> "redis://" + redisContainer.getHost() + ':' + redisContainer.getMappedPort(6379));
    }
}
