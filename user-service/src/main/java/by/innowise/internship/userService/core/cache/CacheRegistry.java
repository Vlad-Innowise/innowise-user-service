package by.innowise.internship.userService.core.cache;

import java.util.Set;

public interface CacheRegistry {

    Set<String> getRegisteredByCacheSetName(String cacheSetName);

    void register(String cacheSetName, Set<String> supportedCaches);

}
