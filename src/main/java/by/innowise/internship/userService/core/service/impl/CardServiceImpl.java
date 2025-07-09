package by.innowise.internship.userService.core.service.impl;

import by.innowise.internship.userService.core.repository.CardInfoRepository;
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

    @Transactional(readOnly = true)
    @Override
    public boolean cardNumberExists(String number) {
        log.info("Checking if card number: {} already exists in the database", number);
        return cardRepository.existsByNumber(number);
    }
}
