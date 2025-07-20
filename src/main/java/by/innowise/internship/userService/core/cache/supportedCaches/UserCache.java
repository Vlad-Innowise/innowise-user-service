package by.innowise.internship.userService.core.cache.supportedCaches;

public enum UserCache implements CacheType {

    BY_ID("users");

    private final String cacheName;

    UserCache(String cacheName) {
        this.cacheName = cacheName;
    }

    @Override
    public String getCacheName() {
        return cacheName;
    }
}
