package by.innowise.internship.userService.core.repository;

import by.innowise.internship.userService.core.repository.entity.CardInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CardInfoRepository extends JpaRepository<CardInfo, UUID> {
}
