package by.innowise.internship.userService.core.cache.impl;

import by.innowise.internship.userService.core.cache.CacheUtil;
import by.innowise.internship.userService.core.cache.CardInfoCacheService;
import by.innowise.internship.userService.core.cache.dto.CardCacheDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CardInfoCacheServiceImpl implements CardInfoCacheService {

    private static final String CARD_INFO_CACHE_SET_NAME = "cardInfo";
    private final CacheManager cacheManager;
    private final CacheUtil cacheUtil;

    @Override
    public String getCacheSetName() {
        return CARD_INFO_CACHE_SET_NAME;
    }

    @Override
    public Optional<CardCacheDto> readFromCache(String cacheName, String key) {
        log.info("Retrieving a cache by name: [{}]", cacheName);
        Cache cache = cacheUtil.getCache(CARD_INFO_CACHE_SET_NAME, cacheName, cacheManager);
        log.info("Trying to read from cache: [{}] by key: {}", cacheName, key);
        return Optional.ofNullable(cache.get(key, CardCacheDto.class));
    }

    @Override
    public void updateCache(String cacheName, String key, CardCacheDto value) {
        log.info("Updating a cache: [{}] with key: {} value: {}", cacheName, key, value);
        Cache cache = cacheUtil.getCache(CARD_INFO_CACHE_SET_NAME, cacheName, cacheManager);
        cache.put(key, value);
        log.info("Cache for the key: {} was updated with a value: {}", key, value);
    }

    @Override
    public void removeFromCache(String cacheName, String key) {
        log.info("Deleting a key entry: {} from a cache: [{}]", key, cacheName);
        Cache cache = cacheUtil.getCache(CARD_INFO_CACHE_SET_NAME, cacheName, cacheManager);
        cache.evict(key);
        log.info("The key: {} was removed", String.join("::", cacheName, key));
    }

    @Override
    public void invalidateCache(String cacheName) {
        log.info("Invalidating all entries for a cache: [{}]", cacheName);
        Cache cache = cacheUtil.getCache(CARD_INFO_CACHE_SET_NAME, cacheName, cacheManager);
        cache.clear();
        log.info("Cache: [{}] was invalidated", cacheName);
    }

}
