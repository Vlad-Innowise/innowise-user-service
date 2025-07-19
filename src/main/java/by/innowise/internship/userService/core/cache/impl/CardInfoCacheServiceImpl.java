package by.innowise.internship.userService.core.cache.impl;

import by.innowise.internship.userService.core.cache.CacheRegistry;
import by.innowise.internship.userService.core.cache.CardInfoCacheService;
import by.innowise.internship.userService.core.cache.dto.CardCacheDto;
import by.innowise.internship.userService.core.cache.supportedCaches.CacheType;
import by.innowise.internship.userService.core.cache.supportedCaches.CardCache;
import by.innowise.internship.userService.core.validation.validator.CacheValidator;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CardInfoCacheServiceImpl extends AbstractCache<CardCacheDto> implements CardInfoCacheService {

    private static final String CARD_INFO_CACHE_SET_NAME = "cardInfo";
    private final CacheRegistry cacheRegistry;

    @Autowired
    public CardInfoCacheServiceImpl(CacheManager cacheManager, CacheValidator validator, CacheRegistry cacheRegistry) {
        super(cacheManager, validator);
        this.cacheRegistry = cacheRegistry;
    }

    @PostConstruct
    @Override
    public void selfRegisterCaches() {
        Set<String> supportedCaches = Arrays.stream(CardCache.values())
                                            .map(CacheType::getCacheName)
                                            .collect(Collectors.toSet());
        cacheRegistry.register(getCacheSetName(), supportedCaches);
    }

    @Override
    public String getCacheSetName() {
        return CARD_INFO_CACHE_SET_NAME;
    }

}
