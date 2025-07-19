package by.innowise.internship.userService.core.validation.validator;

import by.innowise.internship.userService.core.cache.CacheRegistry;
import by.innowise.internship.userService.core.exception.NotSupportedCacheException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class CacheValidator {

    private final CacheRegistry cacheRegistry;

    public void validate(String cacheSetName, String cacheName) {
        checkCacheSetName(cacheSetName);
        checkCacheNameAssociation(cacheSetName, cacheName);
    }

    private void checkCacheSetName(String cacheSetName) {
        log.info("Checking if any caches are registered under cash set name: [{}]", cacheSetName);
        if (isNoAssociatedCaches(cacheSetName)) {
            throw new NotSupportedCacheException(
                    String.format("The cache set with name: [%s] is not registered", cacheSetName),
                    HttpStatus.BAD_REQUEST);
        }
    }

    private void checkCacheNameAssociation(String cacheSetName, String cacheName) {
        Set<String> supportedCaches = cacheRegistry.getRegisteredByCacheSetName(cacheSetName);
        log.info("Checking if the requested cache name: [{}] is associated with cache set: [{}]", cacheName,
                 cacheSetName);
        if (hasNotContainCache(cacheName, supportedCaches)) {
            throw new NotSupportedCacheException(
                    String.format("The cache with name: [%s] is not supported by cache set: [%s]", cacheName,
                                  cacheSetName),
                    HttpStatus.BAD_REQUEST);
        }
    }

    private boolean hasNotContainCache(String cacheName, Set<String> supportedCaches) {
        return !supportedCaches.contains(cacheName);
    }

    private boolean isNoAssociatedCaches(String cacheSetName) {
        return cacheRegistry.getRegisteredByCacheSetName(cacheSetName).isEmpty();
    }

}
