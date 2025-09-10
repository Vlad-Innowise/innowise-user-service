package by.innowise.internship.userService.core.mapper;

import by.innowise.internship.userService.api.dto.user.UserCreateDto;
import by.innowise.internship.userService.api.dto.user.UserResponseDto;
import by.innowise.internship.userService.api.dto.user.UserUpdateDto;
import by.innowise.internship.userService.core.cache.dto.UserCacheDto;
import by.innowise.internship.userService.core.repository.entity.User;
import org.mapstruct.AfterMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = BaseMapper.class,
        uses = {CardInfoMapper.class})
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "cards", ignore = true)
    User toEntity(UserCreateDto dto, @Context Long authId);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "cards", ignore = true)
    User updateEntity(UserUpdateDto dto, @MappingTarget User entity, @Context Long authId);

    UserResponseDto toDto(User e);

    UserCacheDto toRedisDto(User e);

    UserResponseDto toDto(UserCacheDto cacheDto);

    @AfterMapping
    default void mapAuthId(@MappingTarget User user, @Context Long authId) {
        user.setAuthId(authId);
    }

}
