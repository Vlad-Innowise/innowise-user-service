package by.innowise.internship.userService.core.cache.impl;

import by.innowise.internship.userService.core.cache.CacheBase;
import by.innowise.internship.userService.core.cache.supportedCaches.CacheType;
import by.innowise.internship.userService.core.validation.validator.CacheValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public abstract class AbstractCache<T> implements CacheBase<T> {

    private final CacheManager cacheManager;
    private final CacheValidator validator;

    @Transactional
    @Override
    public Optional<T> readFromCache(CacheType cacheType, String key) {
        String cacheName = cacheType.getCacheName();
        log.info("Retrieving a cache by name: [{}]", cacheName);
        Cache cache = validateAndGet(cacheName);
        log.info("Trying to read from cache: [{}] by key: {}", cacheName, key);
        return Optional.ofNullable(cache.get(key))
                       .map(Cache.ValueWrapper::get)
                       .map(value -> (T) value);
    }

    @Transactional
    @Override
    public void updateCache(CacheType cacheType, String key, T value) {
        String cacheName = cacheType.getCacheName();
        log.info("Updating a cache: [{}] with key: {} value: {}", cacheName, key, value);
        Cache cache = validateAndGet(cacheName);
        cache.put(key, value);
        log.info("Cache for the key: {} was updated with a value: {}", key, value);
    }

    @Transactional
    @Override
    public void removeFromCache(CacheType cacheType, String key) {
        String cacheName = cacheType.getCacheName();
        log.info("Deleting a key entry: {} from a cache: [{}]", key, cacheName);
        Cache cache = validateAndGet(cacheName);
        cache.evict(key);
        log.info("The key: {} was removed", String.join("::", cacheName, key));
    }

    @Transactional
    @Override
    public void invalidateCache(CacheType cacheType) {
        String cacheName = cacheType.getCacheName();
        log.info("Invalidating all entries for a cache: [{}]", cacheName);
        Cache cache = validateAndGet(cacheName);
        cache.clear();
        log.info("Cache: [{}] was invalidated", cacheName);
    }

    protected Cache validateAndGet(String cacheName) {
        validator.validate(getCacheSetName(), cacheName);
        return cacheManager.getCache(cacheName);
    }

    protected CacheManager getCacheManager() {
        return cacheManager;
    }

    protected CacheValidator getCacheValidator() {
        return validator;
    }

}
