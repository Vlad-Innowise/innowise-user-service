package by.innowise.internship.userService.core.repository.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.util.UUID;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "card_info")
public class CardInfo {

    @EqualsAndHashCode.Include
    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "number", nullable = false, unique = true, length = 16)
    private String number;

    @Column(name = "holder", nullable = false, length = 64)
    private String holder;

    @Column(name = "expiration_date", nullable = false)
    private LocalDate expirationDate;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

}
