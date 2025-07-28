package by.innowise.internship.userService.core.cache.impl;

import by.innowise.internship.userService.core.cache.CacheRegistry;
import by.innowise.internship.userService.core.cache.CacheUtil;
import by.innowise.internship.userService.core.cache.UserCacheInvalidator;
import by.innowise.internship.userService.core.cache.UserCacheService;
import by.innowise.internship.userService.core.cache.dto.UserCacheDto;
import by.innowise.internship.userService.core.cache.supportedCaches.CacheType;
import by.innowise.internship.userService.core.cache.supportedCaches.UserCache;
import by.innowise.internship.userService.core.validation.validator.CacheValidator;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserCacheServiceImpl extends AbstractCache<UserCacheDto> implements UserCacheService, UserCacheInvalidator {

    private static final String USER_CACHE_SET_NAME = "user";
    private final CacheRegistry cacheRegistry;
    private final CacheUtil cacheUtil;

    @Autowired
    public UserCacheServiceImpl(CacheManager cacheManager, CacheValidator validator, CacheRegistry cacheRegistry,
                                CacheUtil cacheUtil) {
        super(cacheManager, validator);
        this.cacheRegistry = cacheRegistry;
        this.cacheUtil = cacheUtil;
    }

    @PostConstruct
    @Override
    public void selfRegisterCaches() {
        Set<String> supportedCaches = Arrays.stream(UserCache.values())
                                            .map(CacheType::getCacheName)
                                            .collect(Collectors.toSet());
        cacheRegistry.register(getCacheSetName(), supportedCaches);
    }

    @Override
    public String getCacheSetName() {
        return USER_CACHE_SET_NAME;
    }

    @Transactional
    @Override
    public void invalidate(Long userId) {
        String byIdKey = cacheUtil.composeKey("id", userId);
        Cache byIdCache = validateAndGet(UserCache.BY_ID.getCacheName());
        byIdCache.evict(byIdKey);
    }
}
