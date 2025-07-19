package by.innowise.internship.userService.core.cache.impl;

import by.innowise.internship.userService.core.cache.CacheUtil;
import by.innowise.internship.userService.core.cache.CardInfoCacheService;
import by.innowise.internship.userService.core.cache.dto.CardCacheDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CardInfoCacheServiceImpl extends AbstractCache<CardCacheDto> implements CardInfoCacheService {

    private static final String CARD_INFO_CACHE_SET_NAME = "cardInfo";

    @Autowired
    public CardInfoCacheServiceImpl(CacheManager cacheManager, CacheUtil cacheUtil) {
        super(cacheManager, cacheUtil);
    }

    @Override
    public String getCacheSetName() {
        return CARD_INFO_CACHE_SET_NAME;
    }

}
