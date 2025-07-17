package by.innowise.internship.userService.core.cache;

import by.innowise.internship.userService.core.config.property.CacheProperty;
import by.innowise.internship.userService.core.exception.IllegalCacheKeyException;
import by.innowise.internship.userService.core.exception.NotSupportedCacheException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CacheUtil {

    private final CacheProperty cacheProperty;

    public Cache getCache(String cacheSetName, String cacheName, CacheManager cacheManager) {
        return Optional.ofNullable(cacheManager.getCache(cacheName))
                       .orElseThrow(() -> new NotSupportedCacheException(
                               String.format("The cache with name: [%s] is not supported", cacheName),
                               HttpStatus.BAD_REQUEST)
                       );
    }

    public String composeKey(String... keyParts) {
        return formatKey(keyParts);
    }

    public String composeKey(Object... keyParts) {
        return formatKey(keyParts);
    }

    private String formatKey(Object[] parts) {
        checkNotNull(parts);
        String key = getStringFromArray(parts);
        checkNotNullComposite(key);
        return key;
    }

    private void checkNotNull(Object key) {
        if (key == null) {
            throw new IllegalCacheKeyException("Cache key cannot be null!", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private String getStringFromArray(Object[] parts) {
        return Arrays.stream(parts)
                     .map(kp -> Objects.isNull(kp) ? "null" : kp.toString())
                     .collect(Collectors.joining(":"));
    }

    private void checkNotNullComposite(String compositeKey) {
        if (compositeKey.contains(":null:") || compositeKey.startsWith("null") || compositeKey.endsWith("null")) {
            throw new IllegalCacheKeyException(
                    String.format("Cache key cannot contain null parts: [%s]", compositeKey),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
