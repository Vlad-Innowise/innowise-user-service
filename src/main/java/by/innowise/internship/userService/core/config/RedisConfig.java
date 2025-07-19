package by.innowise.internship.userService.core.config;

import by.innowise.internship.userService.core.cache.supportedCaches.CacheType;
import by.innowise.internship.userService.core.cache.supportedCaches.CardCache;
import by.innowise.internship.userService.core.cache.supportedCaches.UserCache;
import by.innowise.internship.userService.core.config.property.CacheProperty;
import by.innowise.internship.userService.core.config.property.RedisProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.time.Duration;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@EnableCaching
@Configuration
public class RedisConfig {

    @Bean
    public RedisConnectionFactory redisConnectionFactory(RedisProperty property) {
        return new LettuceConnectionFactory(new RedisStandaloneConfiguration(property.getHost(), property.getPort()));
    }

    @Bean
    public ObjectMapper redisObjectMapper(Jackson2ObjectMapperBuilder builder,
                                          @Qualifier("jsonCustomizer")
                                          Jackson2ObjectMapperBuilderCustomizer jsonCustomizer) {

        PolymorphicTypeValidator ptv =
                BasicPolymorphicTypeValidator.builder()
                                             .allowIfSubType("by.innowise")
                                             .build();

        builder.postConfigurer(redisMapper -> {
            redisMapper.activateDefaultTyping(ptv,
                                              ObjectMapper.DefaultTyping.NON_FINAL,
                                              JsonTypeInfo.As.PROPERTY);
        });

        jsonCustomizer.customize(builder);

        return builder.build();
    }

    @Bean
    public GenericJackson2JsonRedisSerializer genericRedisSerializer(@Qualifier("redisObjectMapper")
                                                                     ObjectMapper redisObjectMapper) {
        return new GenericJackson2JsonRedisSerializer(redisObjectMapper);
    }

    @Bean
    public StringRedisSerializer stringRedisSerializer() {
        return new StringRedisSerializer();
    }

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory factory,
                                     StringRedisSerializer stringRedisSerializer,
                                     GenericJackson2JsonRedisSerializer genericRedisSerializer,
                                     CacheProperty cacheProperty) {

        RedisCacheConfiguration config =
                RedisCacheConfiguration.defaultCacheConfig()
                                       .serializeKeysWith(RedisSerializationContext.SerializationPair
                                                                  .fromSerializer(stringRedisSerializer))
                                       .serializeValuesWith(RedisSerializationContext.SerializationPair
                                                                    .fromSerializer(genericRedisSerializer))
                                       .disableCachingNullValues()
                                       .entryTtl(Duration.ofMinutes(cacheProperty.getTimeToLiveInMinutes()));

        Set<String> supportedCaches = Stream.of(UserCache.values(),
                                                CardCache.values())
                                            .flatMap(Arrays::stream)
                                            .map(CacheType::getCacheName)
                                            .collect(Collectors.toSet());

        return RedisCacheManager.builder(factory)
                                .cacheDefaults(config)
                                .initialCacheNames(supportedCaches)
                                .transactionAware()
                                .build();
    }

}
