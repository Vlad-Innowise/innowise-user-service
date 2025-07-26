package by.innowise.internship.userService.core.config.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.cache.redis")
@Data
public class RedisProperty {

    private String host;

    private int port;
}
