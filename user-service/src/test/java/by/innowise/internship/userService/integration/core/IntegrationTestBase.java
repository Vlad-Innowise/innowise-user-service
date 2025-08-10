package by.innowise.internship.userService.integration.core;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
@Sql(scripts = {
        "classpath:/sql/test-data.sql"
})
public abstract class IntegrationTestBase {

    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
            "postgres:17-alpine")
            .withDatabaseName("test-db")
            .withUsername("postgres")
            .withPassword("postgres");

    private static final GenericContainer redis =
            new GenericContainer("redis:7.2.10-alpine").withExposedPorts(6379);

    static {
        postgres.start();
        redis.start();
    }

    @DynamicPropertySource
    static void postgresProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);

        registry.add("spring.cache.redis.host", redis::getHost);
        registry.add("spring.cache.redis.port", redis::getFirstMappedPort);
    }

}
