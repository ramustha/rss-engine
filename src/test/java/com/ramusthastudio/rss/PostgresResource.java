package com.ramusthastudio.rss;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.Map;

public class PostgresResource implements QuarkusTestResourceLifecycleManager {

    static PostgreSQLContainer<?> container =
            new PostgreSQLContainer<>("postgres")
                    .withDatabaseName("postgres")
                    .withUsername("postgres")
                    .withPassword("postgres");

    @Override
    public Map<String, String> start() {
        container.start();

        String url = container.getJdbcUrl().replace("jdbc:", "vertx-reactive:");
        return Map.of("quarkus.datasource.reactive.url", url,
                "quarkus.datasource.username", container.getUsername(),
                "quarkus.datasource.password", container.getPassword()
        );
    }

    @Override
    public void stop() {
        container.stop();
    }
}
