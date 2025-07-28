package by.innowise.internship.userService.core.cache;

public interface UserCacheInvalidator {

    void invalidate(Long userId);
}
