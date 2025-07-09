package by.innowise.internship.userService.core.service.impl;

import by.innowise.internship.userService.api.dto.cardInfo.CardInfoCreateDto;
import by.innowise.internship.userService.api.dto.cardInfo.CardInfoResponseDto;
import by.innowise.internship.userService.core.mapper.CardInfoMapper;
import by.innowise.internship.userService.core.repository.CardInfoRepository;
import by.innowise.internship.userService.core.repository.entity.CardInfo;
import by.innowise.internship.userService.core.service.api.CardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CardServiceImpl implements CardService {

    private final CardInfoRepository cardRepository;
    private final CardInfoMapper mapper;

    @Transactional
    @Override
    public CardInfoResponseDto create(CardInfoCreateDto dto, Long userId) {
        CardInfo saved = cardRepository.saveAndFlush(mapper.toEntity(dto, userId));
        log.info("Saved a card info: {}", saved);
        CardInfoResponseDto responseDto = mapper.toDto(saved);
        log.info("Returning a response dto: {}", responseDto);
        return responseDto;
    }

    @Transactional(readOnly = true)
    @Override
    public boolean cardNumberExists(String number) {
        log.info("Checking if card number: {} already exists in the database", number);
        return cardRepository.existsByNumber(number);
    }
}
