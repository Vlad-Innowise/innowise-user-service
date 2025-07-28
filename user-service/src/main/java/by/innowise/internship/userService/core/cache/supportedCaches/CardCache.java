package by.innowise.internship.userService.core.cache.supportedCaches;

public enum CardCache implements CacheType {

    BY_ID("cards");

    private final String cacheName;

    CardCache(String cacheName) {
        this.cacheName = cacheName;
    }

    @Override
    public String getCacheName() {
        return cacheName;
    }
}
