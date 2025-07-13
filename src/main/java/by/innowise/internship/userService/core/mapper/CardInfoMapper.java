package by.innowise.internship.userService.core.mapper;

import by.innowise.internship.userService.api.dto.cardInfo.CardInfoCreateDto;
import by.innowise.internship.userService.api.dto.cardInfo.CardInfoResponseDto;
import by.innowise.internship.userService.api.dto.cardInfo.CardInfoUpdateDto;
import by.innowise.internship.userService.core.repository.entity.CardInfo;
import by.innowise.internship.userService.core.repository.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.UUID;

@Mapper(config = BaseMapper.class,
        imports = {UUID.class})
public interface CardInfoMapper {

    @Mapping(target = "id", expression = "java(UUID.randomUUID())")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(source = "dto.number", target = "number")
    @Mapping(source = "dto.holder", target = "holder")
    @Mapping(source = "dto.expirationDate", target = "expirationDate")
    @Mapping(source = "user", target = "user")
    CardInfo toEntity(CardInfoCreateDto dto, User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "user", ignore = true)
    CardInfo updateEntity(CardInfoUpdateDto dto, @MappingTarget CardInfo e);

    @Mapping(source = "user.id", target = "userId")
    CardInfoResponseDto toDto(CardInfo e);
}
