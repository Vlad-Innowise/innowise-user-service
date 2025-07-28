package by.innowise.internship.userService.core.cache.impl;

import by.innowise.internship.userService.core.cache.CacheRegistry;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
public class CacheRegistryImpl implements CacheRegistry {

    private final Map<String, Set<String>> supportedCachesBySetName = new HashMap<>();

    @Override
    public Set<String> getRegisteredByCacheSetName(String cacheSetName) {
        Set<String> supportedCaches = supportedCachesBySetName.getOrDefault(cacheSetName, Collections.emptySet());
        return isNotEmpty(supportedCaches) ? Set.copyOf(supportedCaches) : supportedCaches;
    }

    @Override
    public void register(String cacheSetName, Set<String> supportedCaches) {
        supportedCachesBySetName.put(cacheSetName, supportedCaches);
    }

    private boolean isNotEmpty(Set<String> supportedCaches) {
        return !supportedCaches.isEmpty();
    }
}
