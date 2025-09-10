package by.innowise.internship.userService.integration.util;

import by.innowise.internship.userService.core.repository.entity.CardInfo;
import by.innowise.internship.userService.core.repository.entity.User;
import by.innowise.internship.userService.util.TestUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

public class TestData {

    public static final User FRODO_BAGGINS;
    public static final User SAMWISE_GAMPGIE;
    public static final User ARYA_STARK;

    static {

        FRODO_BAGGINS = TestUtil.getUser(1L, "Frodo", "Baggins", LocalDate.of(1997, 7, 7), "frodo_baggins@email.com",
                                         21L);
        SAMWISE_GAMPGIE = TestUtil.getUser(2L, "Samwise", "Gampgie", LocalDate.of(1999, 11, 1),
                                           "sam_gampgie@email.com", 22L);
        ARYA_STARK = TestUtil.getUser(3L, "Arya", "Stark", LocalDate.of(1993, 1, 1), "arya_stark@email.com", 23L);

        CardInfo frodoCard1 = TestUtil.getCard("2222444499991111", "FRODO BAGGINS", LocalDate.of(2025, 12, 31));
        frodoCard1.setId(UUID.fromString("cbf7e645-523c-458e-a4bc-c8c01bb3d82a"));

        CardInfo frodoCard2 = TestUtil.getCard("2222111144447777", "FRODO BAGGINS", LocalDate.of(2029, 6, 30));
        frodoCard2.setId(UUID.fromString("dd7844c4-4746-4ce0-a665-fb7388755865"));

        CardInfo frodoCard3 = TestUtil.getCard("2222333344446666", "FRODO BAGGINS", LocalDate.of(2027, 3, 31));
        frodoCard3.setId(UUID.fromString("8f1958d9-8c55-4f86-be31-9060b5feb7c6"));

        CardInfo samCard1 = TestUtil.getCard("333377775559999", "SAMWISE GAMPGIE", LocalDate.of(2028, 8, 31));
        samCard1.setId(UUID.fromString("83aa085d-e6f6-4246-bc98-c536a006641c"));

        FRODO_BAGGINS.setCards(new ArrayList<>(List.of(frodoCard1, frodoCard2, frodoCard3)));
        FRODO_BAGGINS.getCards().forEach(card -> card.setUser(FRODO_BAGGINS));

        SAMWISE_GAMPGIE.setCards(new ArrayList<>(List.of(samCard1)));
        samCard1.setUser(SAMWISE_GAMPGIE);

        LocalDateTime createdAndUpdatedDateTime = LocalDateTime.of(2025, 8, 7, 19, 30);
        Stream.of(FRODO_BAGGINS, SAMWISE_GAMPGIE, ARYA_STARK)
              .forEach(u -> {
                  setUserCreatedUpdatedDate(u, createdAndUpdatedDateTime);
                  setCardCreatedUpdatedDate(u.getCards(), createdAndUpdatedDateTime);
              });
    }

    private static void setUserCreatedUpdatedDate(User user, LocalDateTime dateToSet) {
        user.setUpdatedAt(dateToSet);
        user.setCreatedAt(dateToSet);
    }

    private static void setCardCreatedUpdatedDate(List<CardInfo> cards, LocalDateTime dateToSet) {
        cards.forEach(c -> {
            c.setCreatedAt(dateToSet);
            c.setUpdatedAt(dateToSet);
        });
    }

}
