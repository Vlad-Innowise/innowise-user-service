package by.innowise.internship.userService.core.repository;

import by.innowise.internship.userService.core.repository.entity.CardInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface CardInfoRepository extends JpaRepository<CardInfo, UUID> {

    boolean existsByNumber(String number);

    Optional<CardInfo> findByNumber(String number);

    @Query("SELECT ci FROM CardInfo ci JOIN FETCH ci.user WHERE ci.id =:id AND ci.user.id =:userId")
    Optional<CardInfo> findByIdAndUserId(@Param("id") UUID id, @Param("userId") Long userId);

    Page<CardInfo> findAllByUserId(Long userId, Pageable pageable);
}
