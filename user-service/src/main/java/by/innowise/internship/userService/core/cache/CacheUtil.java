package by.innowise.internship.userService.core.cache;

import by.innowise.internship.userService.core.exception.IllegalCacheKeyException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CacheUtil {

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
