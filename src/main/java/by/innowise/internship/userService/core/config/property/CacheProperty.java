package by.innowise.internship.userService.core.config.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "application.cache.redis")
@Data
public class CacheProperty {

    private int timeToLiveInMinutes;

}
