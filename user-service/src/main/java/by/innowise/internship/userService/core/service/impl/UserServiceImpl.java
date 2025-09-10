package by.innowise.internship.userService.core.service.impl;

import by.innowise.common.library.exception.UniqueConstraintViolationException;
import by.innowise.common.library.exception.UserNotFoundException;
import by.innowise.common.library.util.ValidationUtil;
import by.innowise.internship.userService.api.dto.user.UserCreateDto;
import by.innowise.internship.userService.api.dto.user.UserResponseDto;
import by.innowise.internship.userService.api.dto.user.UserUpdateDto;
import by.innowise.internship.userService.core.cache.CacheUtil;
import by.innowise.internship.userService.core.cache.UserCacheService;
import by.innowise.internship.userService.core.cache.dto.UserCacheDto;
import by.innowise.internship.userService.core.cache.supportedCaches.UserCache;
import by.innowise.internship.userService.core.mapper.UserMapper;
import by.innowise.internship.userService.core.repository.UserRepository;
import by.innowise.internship.userService.core.repository.entity.User;
import by.innowise.internship.userService.core.service.api.InternalUserService;
import by.innowise.internship.userService.core.service.api.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService, InternalUserService {

    private final UserRepository userRepository;
    private final UserMapper mapper;
    private final UserCacheService userCacheService;
    private final CacheUtil cacheUtil;

    @Transactional
    @Override
    public UserResponseDto create(UserCreateDto dto, Long authUserId) {
        User toSave = mapper.toEntity(dto, authUserId);
        log.info("Invoking user repository to save user: {}", toSave);
        User saved = userRepository.saveAndFlush(toSave);
        log.info("Map a created user entity {} to dto", saved);
        updateCache(saved);
        return mapper.toDto(saved);
    }

    @Transactional(readOnly = true)
    @Override
    public boolean isEmailExists(String email) {
        log.info("Checking if email: {} already exists in the database", email);
        return userRepository.findByEmail(email)
                             .isPresent();
    }

    @Transactional
    @Override
    public UserResponseDto getById(Long authUserId) {
        String cacheKey = cacheUtil.composeKey("id", authUserId);
        log.info("Trying to retrieve the user: [{}] from cache by key: {}", authUserId, cacheKey);
        return userCacheService
                .readFromCache(UserCache.BY_ID, cacheKey)
                .map(cached -> {
                    log.info("Retrieved the user from a cache: {}", cached);
                    return mapper.toDto(cached);
                })
                .orElseGet(() -> {
                    log.info("Not found in cache by key: {}, go to DB", cacheKey);
                    User found = getUserByAuthIdFetchAllCards(authUserId);
                    updateCache(found);
                    return mapper.toDto(found);
                });
    }

    @Transactional
    @Override
    public UserResponseDto update(UserUpdateDto dto, Long authUserId) {

        User foundById = getUserByAuthIdFetchAllCards(authUserId);

        User updated;
        if (hasAnyFieldChanged(dto, foundById)) {
            log.info("Check if the provided dto version is not outdated. Dto: [{}] Entity: [{}]", dto, foundById);
            ValidationUtil.checkIfDtoVersionIsOutdated(foundById.getVersion(), dto);


            String emailToCheck = dto.email();
            log.info("Check if the provided email doesn't exists [{}]", emailToCheck);
            if (hasEmailChanged(emailToCheck, foundById)) {
                userRepository.findByEmail(emailToCheck)
                              .ifPresent(user -> {
                                  throw new UniqueConstraintViolationException(
                                          String.format("Can't update the email to: [%s]. This email already exists!",
                                                        emailToCheck), HttpStatus.BAD_REQUEST);
                              });
            }

            updated = mapper.updateEntity(dto, foundById, authUserId);
            log.info("Updated entity {} to save", updated);
            userRepository.saveAndFlush(updated);
            updateCache(updated);
        } else {
            log.info("Non of the fields in the dto {} have changed any of the fields in the entity {}", dto, foundById);
            updated = foundById;
        }
        return mapper.toDto(updated);
    }

    private boolean hasEmailChanged(String emailToCheck, User foundById) {
        return !foundById.getEmail().equalsIgnoreCase(emailToCheck);
    }

    @Transactional
    @Override
    public void delete(Long authUserId) {
        User found = getUserByAuthIdFetchAllCards(authUserId);
        log.info("Invoking user repository to delete a user: [{}]", found);
        userRepository.delete(found);
        String cacheKey = cacheUtil.composeKey("id", authUserId);
        log.info("Invoking cache service to remove a cache for a key: {}", cacheKey);
        userCacheService.removeFromCache(UserCache.BY_ID, cacheKey);
    }

    @Transactional(readOnly = true)
    @Override
    public List<UserResponseDto> getAllByIds(List<Long> authUserIds) {
        Set<Long> idsToFind = new HashSet<>(authUserIds);
        log.info("Invoking user repository for ids: [{}]", idsToFind);
        List<User> foundUsers = userRepository.findByIdIn(idsToFind);
        log.info("Retrieved users list: {}", foundUsers);

        if (foundUsers.size() != idsToFind.size()) {
            foundUsers.forEach(u -> idsToFind.remove(u.getId()));
            log.info("Haven't found users with ids: {}", idsToFind);
        }
        return foundUsers.stream()
                         .map(mapper::toDto)
                         .toList();
    }

    @Transactional
    @Override
    public User getUserByAuthId(Long authId) {
        return getUserByAuthIdFetchAllCards(authId);
    }

    private User getUserByAuthIdFetchAllCards(Long authId) {
        log.info("Trying to get a user by authId: {} with all cards", authId);
        User found = userRepository.findByAuthIdWithAllCards(authId)
                                   .orElseThrow(
                                           () -> new UserNotFoundException(
                                                   String.format("User with authId [%s] doesn't exist", authId),
                                                   HttpStatus.NOT_FOUND));
        log.info("Retrieved a user: {} with all cards {}", found, found.getCards());
        return found;
    }

    private boolean hasAnyFieldChanged(UserUpdateDto d, User e) {
        return !(Objects.equals(d.name(), e.getName()) &&
                Objects.equals(d.surname(), e.getSurname()) &&
                Objects.equals(d.birthDate(), e.getBirthDate()) &&
                Objects.equals(d.email(), e.getEmail())
        );
    }

    private void updateCache(User entity) {
        UserCacheDto cacheDto = mapper.toRedisDto(entity);
        String cacheKey = cacheUtil.composeKey("id", entity.getAuthId());
        log.info("Putting value: {} into cache: [{}]", cacheDto, UserCache.BY_ID.getCacheName());
        userCacheService.updateCache(UserCache.BY_ID, cacheKey, cacheDto);
    }
}
