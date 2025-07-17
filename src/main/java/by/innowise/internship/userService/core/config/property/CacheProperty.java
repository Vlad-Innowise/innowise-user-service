package by.innowise.internship.userService.core.config.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;
import java.util.Set;

@ConfigurationProperties(prefix = "application.cache.redis")
@Data
public class CacheProperty {

    private int timeToLiveInMinutes;

    private Map<String, Set<String>> supportedCaches;
}
