package by.innowise.internship.userService.core.mapper;

import by.innowise.internship.userService.api.dto.user.UserCreateDto;
import by.innowise.internship.userService.api.dto.user.UserResponseDto;
import by.innowise.internship.userService.core.repository.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = BaseMapper.class,
        uses = {CardInfoMapper.class})
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "cards", ignore = true)
    User toEntity(UserCreateDto dto);

    UserResponseDto toDto(User e);

}
