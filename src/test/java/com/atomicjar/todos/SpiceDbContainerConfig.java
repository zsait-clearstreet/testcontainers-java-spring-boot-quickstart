package com.atomicjar.todos;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;

public class SpiceDbContainerConfig {

    /**
     * The original port which SpiceDB is listening on (will be mapped to something else in localhost)
     */
    public static final Integer SPICEDB_PORT = 50051;

    public static final String AUTHZ_TOKEN = RandomStringUtils.randomAlphanumeric(10);

    /**
     * The spice DB (authzed) test container
     */
    @Container
    static GenericContainer<?> spiceDb = new GenericContainer<>(
                    DockerImageName.parse("authzed/spicedb:v1.26.0-debug"))
            .withExposedPorts(SPICEDB_PORT)
            .withCommand("serve-testing");

    /**
     * A static method to inject the port exposed from the SpiceDB container into the grpc endpoint property
     *
     * @param registry The dynamic properties registry given by spring which we'll use to inject our properties.
     */
    @DynamicPropertySource
    static void spiceDbProperties(DynamicPropertyRegistry registry) {

        if (!spiceDb.isRunning()) spiceDb.start();

        registry.add("security.authz-token", () -> AUTHZ_TOKEN);
        registry.add(
                "security.authz-grpc-endpoint",
                () -> spiceDb.getHost() + ':' + spiceDb.getMappedPort(SPICEDB_PORT));
    }
}
