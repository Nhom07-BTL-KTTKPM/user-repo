package iuh.fit.userservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

@SpringBootTest(properties = {
    "spring.config.import=",
    "SPRING_DATASOURCE_URL=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=PostgreSQL",
    "SPRING_DATASOURCE_USERNAME=sa",
    "SPRING_DATASOURCE_PASSWORD=",
    "SPRING_JPA_HIBERNATE_DDL_AUTO=create-drop",
    "AUTH_JWT_ISSUER=auth-service",
    "AUTH_JWT_SECRET=test-secret-32-bytes-long-000000",
    "USER_SERVICE_INTERNAL_AUTH_HEADER_NAME=X-Internal-Api-Key",
    "USER_SERVICE_INTERNAL_AUTH_API_KEY=test-key",
    "spring.cloud.discovery.enabled=false",
    "eureka.client.enabled=false",
    "eureka.client.register-with-eureka=false",
    "eureka.client.fetch-registry=false"
})
@ActiveProfiles("test")
class UserServiceApplicationTests {

    static {
        System.setProperty("SPRING_DATASOURCE_URL", "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=PostgreSQL");
        System.setProperty("spring.datasource.url", "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=PostgreSQL");
        System.setProperty("SPRING_DATASOURCE_USERNAME", "sa");
        System.setProperty("SPRING_DATASOURCE_PASSWORD", "");
        System.setProperty("SPRING_JPA_HIBERNATE_DDL_AUTO", "create-drop");
    }

    @MockitoBean
    private RedisConnectionFactory redisConnectionFactory;

    @MockitoBean
    private ReactiveRedisConnectionFactory reactiveRedisConnectionFactory;

    @MockitoBean
    private RabbitTemplate rabbitTemplate;

    @Test
    void contextLoads() {
    }

}
