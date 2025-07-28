package by.innowise.internship.userService.core.cache;

import by.innowise.internship.userService.core.cache.supportedCaches.CacheType;

import java.util.Optional;

public interface CacheBase<T> {

    String getCacheSetName();

    void selfRegisterCaches();

    Optional<T> readFromCache(CacheType cacheType, String key);

    void updateCache(CacheType cacheType, String key, T value);

    void removeFromCache(CacheType cacheType, String key);

    void invalidateCache(CacheType cacheType);

}
