package engenharia.de.software.poker.api.model.entity;

import engenharia.de.software.poker.api.model.enums.RoundStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "poker_rounds")
public class PokerRound {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "match_id", nullable = false)
    private Match match;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    @Builder.Default
    private RoundStatus status = RoundStatus.AGUARDANDO_DISTRIBUICAO;

    @Column(name = "cards_dealt", nullable = false)
    @Builder.Default
    private boolean cardsDealt = false;

    @Column(name = "created_at", nullable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "round", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("seatOrder ASC")
    @Builder.Default
    private List<RoundParticipant> participants = new ArrayList<>();

    @OneToMany(mappedBy = "round", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("dealOrder ASC")
    @Builder.Default
    private List<DealtCard> dealtCards = new ArrayList<>();

    @OneToMany(mappedBy = "round", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("deckPosition ASC")
    @Builder.Default
    private List<RemainingDeckCard> remainingDeck = new ArrayList<>();
}