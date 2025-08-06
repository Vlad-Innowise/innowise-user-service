package by.innowise.internship.userService;

import by.innowise.internship.userService.api.dto.cardInfo.CardInfoResponseDto;
import by.innowise.internship.userService.api.dto.cardInfo.CardInfoUpdateDto;
import by.innowise.internship.userService.api.dto.user.UserCreateDto;
import by.innowise.internship.userService.api.dto.user.UserResponseDto;
import by.innowise.internship.userService.api.dto.user.UserUpdateDto;
import by.innowise.internship.userService.core.cache.dto.CardCacheDto;
import by.innowise.internship.userService.core.cache.dto.UserCacheDto;
import by.innowise.internship.userService.core.repository.entity.CardInfo;
import by.innowise.internship.userService.core.repository.entity.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TestUtil {

    public static User getUser(Long id, String name, String surname, LocalDate birthDate, String email) {
        return User.builder()
                   .id(id)
                   .name(name)
                   .surname(surname)
                   .birthDate(birthDate)
                   .email(email)
                   .createdAt(LocalDateTime.now())
                   .updatedAt(LocalDateTime.now())
                   .version(0L)
                   .build();
    }

    public static CardInfo getCard(String number, String holder, LocalDate expirationDate) {
        return CardInfo.builder()
                       .id(UUID.randomUUID())
                       .number(number)
                       .holder(holder)
                       .expirationDate(expirationDate)
                       .createdAt(LocalDateTime.now())
                       .updatedAt(LocalDateTime.now())
                       .version(0L)
                       .build();
    }

    public static UserCreateDto getUserCreateDtoFromUser(User user) {
        return new UserCreateDto(user.getName(), user.getSurname(), user.getBirthDate(), user.getEmail());
    }

    public static User deepCopyUser(User user) {
        User copiedUser = User.builder()
                              .id(user.getId())
                              .name(user.getName())
                              .surname(user.getSurname())
                              .birthDate(user.getBirthDate())
                              .email(user.getEmail())
                              .cards(new ArrayList<>())
                              .createdAt(user.getCreatedAt())
                              .updatedAt(user.getUpdatedAt())
                              .version(user.getVersion())
                              .build();

        user.getCards().forEach(card -> {
            CardInfo copiedCard = copyCard(card);
            copiedCard.setUser(copiedUser);
            copiedUser.addCard(copiedCard);
        });
        return copiedUser;
    }

    public static CardInfo copyCard(CardInfo card) {
        return CardInfo.builder()
                       .id(card.getId())
                       .number(card.getNumber())
                       .holder(card.getHolder())
                       .expirationDate(card.getExpirationDate())
                       .user(card.getUser())
                       .version(card.getVersion())
                       .createdAt(card.getCreatedAt())
                       .updatedAt(card.getUpdatedAt())
                       .build();
    }

    public static void updateUser(UserUpdateDto dto, User user) {
        user.setName(dto.name());
        user.setSurname(dto.surname());
        user.setBirthDate(dto.birthDate());
        user.setEmail(dto.email());
    }

    public static void updateCard(CardInfoUpdateDto dto, CardInfo entity) {
        entity.setNumber(dto.number());
        entity.setHolder(dto.holder());
        entity.setExpirationDate(dto.expirationDate());
    }

    public static UserResponseDto mapToUserResponseDto(User user) {
        List<CardInfoResponseDto> cardResponseDtos = user.getCards().stream()
                                                         .map(TestUtil::mapToCardInfoResponseDto)
                                                         .toList();
        return new UserResponseDto(user.getId(), user.getName(), user.getSurname(), user.getBirthDate(),
                                   user.getEmail(), user.getVersion(), cardResponseDtos);
    }

    public static CardInfoResponseDto mapToCardInfoResponseDto(CardInfo card) {
        return new CardInfoResponseDto(card.getId(), card.getNumber(), card.getHolder(), card.getExpirationDate(),
                                       card.getUser().getId(), card.getVersion()
        );
    }

    public static UserResponseDto mapToUserResponseDtoFromRedisDto(UserCacheDto cached) {
        List<CardInfoResponseDto> cardResponseDtos = cached.getCards().stream()
                                                           .map(TestUtil::mapToCardInfoResponseDtoFromRedisDto)
                                                           .toList();
        return new UserResponseDto(cached.getId(), cached.getName(), cached.getSurname(), cached.getBirthDate(),
                                   cached.getEmail(), cached.getVersion(), cardResponseDtos);
    }

    public static CardInfoResponseDto mapToCardInfoResponseDtoFromRedisDto(CardCacheDto cached) {
        return new CardInfoResponseDto(cached.getId(), cached.getNumber(), cached.getHolder(),
                                       cached.getExpirationDate(),
                                       cached.getUserId(), cached.getVersion()
        );
    }

    public static CardCacheDto mapToCardRedisDto(CardInfo card) {
        return CardCacheDto.builder()
                           .id(card.getId())
                           .number(card.getNumber())
                           .holder(card.getHolder())
                           .expirationDate(card.getExpirationDate())
                           .userId(card.getUser().getId())
                           .version(card.getVersion())
                           .build();
    }

    public static UserCacheDto mapToUserRedisDto(User user) {
        List<CardCacheDto> cardRedisDtos = user.getCards().stream()
                                               .map(TestUtil::mapToCardRedisDto)
                                               .toList();
        return UserCacheDto.builder()
                           .id(user.getId())
                           .name(user.getName())
                           .surname(user.getSurname())
                           .birthDate(user.getBirthDate())
                           .email(user.getEmail())
                           .version(user.getVersion())
                           .cards(cardRedisDtos)
                           .build();
    }

}
