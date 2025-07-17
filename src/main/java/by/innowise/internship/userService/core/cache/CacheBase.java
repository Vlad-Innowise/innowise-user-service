package by.innowise.internship.userService.core.cache;

import java.util.Optional;

public interface CacheBase<T> {

    String getCacheSetName();

    Optional<T> readFromCache(String cacheName, String key);

    void updateCache(String cacheName, String key, T value);

    void removeFromCache(String cacheName, String key);

    void invalidateCache(String cacheName);

}
