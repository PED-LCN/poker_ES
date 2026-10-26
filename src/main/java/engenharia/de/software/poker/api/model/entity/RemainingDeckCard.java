package engenharia.de.software.poker.api.model.entity;

import engenharia.de.software.poker.api.model.enums.Rank;
import engenharia.de.software.poker.api.model.enums.Suit;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "remaining_deck_cards")
public class RemainingDeckCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "round_id", nullable = false)
    private PokerRound round;

    @Enumerated(EnumType.STRING)
    @Column(name = "suit", nullable = false, length = 15)
    private Suit suit;

    @Enumerated(EnumType.STRING)
    @Column(name = "rank", nullable = false, length = 15)
    private Rank rank;

    @Column(name = "deck_position", nullable = false)
    private int deckPosition;
}