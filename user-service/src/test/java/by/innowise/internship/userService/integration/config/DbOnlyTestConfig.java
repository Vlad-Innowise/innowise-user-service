package by.innowise.internship.userService.integration.config;

import by.innowise.internship.userService.core.cache.CardInfoCacheService;
import by.innowise.internship.userService.core.cache.UserCacheInvalidator;
import by.innowise.internship.userService.core.cache.UserCacheService;
import by.innowise.internship.userService.core.cache.dto.CardCacheDto;
import by.innowise.internship.userService.core.cache.dto.UserCacheDto;
import by.innowise.internship.userService.core.cache.supportedCaches.CacheType;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import java.util.Optional;

@TestConfiguration
public class DbOnlyTestConfig {

    @Bean
    @Primary
    public CardInfoCacheService cardInfoCacheServiceTest() {
        return new CardInfoCacheService() {

            @Override
            public String getCacheSetName() {
                return "";
            }

            @Override
            public void selfRegisterCaches() {
            }

            @Override
            public Optional<CardCacheDto> readFromCache(CacheType cacheType, String key) {
                return Optional.empty();
            }

            @Override
            public void updateCache(CacheType cacheType, String key, CardCacheDto value) {
            }

            @Override
            public void removeFromCache(CacheType cacheType, String key) {
            }

            @Override
            public void invalidateCache(CacheType cacheType) {
            }
        };
    }

    @Bean
    @Primary
    public UserCacheInvalidator userCacheInvalidatorTest() {
        return userId -> {
        };
    }

    @Bean
    @Primary
    public UserCacheService userCacheServiceTest() {
        return new UserCacheService() {
            @Override
            public String getCacheSetName() {
                return "";
            }

            @Override
            public void selfRegisterCaches() {

            }

            @Override
            public Optional<UserCacheDto> readFromCache(CacheType cacheType, String key) {
                return Optional.empty();
            }

            @Override
            public void updateCache(CacheType cacheType, String key, UserCacheDto value) {

            }

            @Override
            public void removeFromCache(CacheType cacheType, String key) {

            }

            @Override
            public void invalidateCache(CacheType cacheType) {

            }
        };
    }

}
