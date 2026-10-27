package engenharia.de.software.poker.api.service;

import engenharia.de.software.poker.api.dto.card.PlayerHoleCardsResponse;
import engenharia.de.software.poker.api.dto.card.RoundDealResponse;
import engenharia.de.software.poker.api.exception.InvalidMatchStateException;
import engenharia.de.software.poker.api.exception.NotEnoughPlayersException;
import engenharia.de.software.poker.api.model.entity.*;
import engenharia.de.software.poker.api.model.enums.RoundStatus;
import engenharia.de.software.poker.api.repository.DealtCardRepository;
import engenharia.de.software.poker.api.repository.PokerRoundRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardDistributionServiceTest {

    @Mock
    private PokerRoundRepository pokerRoundRepository;

    @Mock
    private DealtCardRepository dealtCardRepository;

    @InjectMocks
    private CardDistributionService cardDistributionService;

    private PokerRound validRound;
    private Player player1;
    private Player player2;
    private Player player3;
    private Player ineligiblePlayer;

    @BeforeEach
    void setUp() {
        player1 = new Player(1L);
        player2 = new Player(2L);
        player3 = new Player(3L);
        ineligiblePlayer = new Player(4L);

        PokerTable table = new PokerTable();
        table.setId(1L);
        table.setMaxPlayers(9);
        table.setMinPlayersToStart(2);

        Match match = new Match(10L, MatchState.IN_PROGRESS, table, player1);

        validRound = PokerRound.builder()
                .id(100L)
                .match(match)
                .status(RoundStatus.AGUARDANDO_DISTRIBUICAO)
                .cardsDealt(false)
                .build();

        validRound.getParticipants().addAll(List.of(
                RoundParticipant.builder().round(validRound).player(player1).seatOrder(1).eligible(true).build(),
                RoundParticipant.builder().round(validRound).player(player2).seatOrder(2).eligible(true).build(),
                RoundParticipant.builder().round(validRound).player(player3).seatOrder(3).eligible(true).build(),
                RoundParticipant.builder().round(validRound).player(ineligiblePlayer).seatOrder(4).eligible(false).build()
        ));
    }

    @Test
    @DisplayName("Deve distribuir 2 cartas por jogador elegível, manter 52 cartas únicas e preservar o baralho restante")
    void shouldDistributeCorrectAmountAndMaintainUniquenessAndEligibility() {
        when(pokerRoundRepository.findById(100L)).thenReturn(Optional.of(validRound));
        when(pokerRoundRepository.save(any(PokerRound.class))).thenAnswer(inv -> inv.getArgument(0));

        RoundDealResponse summary = cardDistributionService.distributeCards(100L);

        assertEquals(6, summary.totalDealtCards());
        assertEquals(46, summary.remainingDeckCount());
        assertEquals(RoundStatus.EM_ANDAMENTO, summary.status());
        assertTrue(validRound.isCardsDealt());

        long p1Cards = validRound.getDealtCards().stream().filter(c -> c.getPlayer().getId().equals(1L)).count();
        long p2Cards = validRound.getDealtCards().stream().filter(c -> c.getPlayer().getId().equals(2L)).count();
        long p3Cards = validRound.getDealtCards().stream().filter(c -> c.getPlayer().getId().equals(3L)).count();
        long ineligibleCards = validRound.getDealtCards().stream().filter(c -> c.getPlayer().getId().equals(4L)).count();

        assertEquals(2, p1Cards);
        assertEquals(2, p2Cards);
        assertEquals(2, p3Cards);
        assertEquals(0, ineligibleCards);

        Set<String> allCards = new HashSet<>();
        validRound.getDealtCards().forEach(c -> allCards.add(c.getRank() + "_" + c.getSuit()));
        validRound.getRemainingDeck().forEach(c -> allCards.add(c.getRank() + "_" + c.getSuit()));
        assertEquals(52, allCards.size());

        List<Long> dealSequencePlayerIds = validRound.getDealtCards().stream()
                .map(c -> c.getPlayer().getId())
                .toList();
        assertEquals(List.of(1L, 2L, 3L, 1L, 2L, 3L), dealSequencePlayerIds);
    }

    @Test
    @DisplayName("Deve impedir segunda distribuição para a mesma rodada lançando InvalidMatchStateException")
    void shouldPreventDuplicateDistributionOnSameRound() {
        when(pokerRoundRepository.findById(100L)).thenReturn(Optional.of(validRound));
        when(pokerRoundRepository.save(any(PokerRound.class))).thenAnswer(inv -> inv.getArgument(0));

        cardDistributionService.distributeCards(100L);
        assertEquals(6, validRound.getDealtCards().size());

        assertThrows(
                InvalidMatchStateException.class,
                () -> cardDistributionService.distributeCards(100L)
        );
        assertEquals(6, validRound.getDealtCards().size());
    }

    @Test
    @DisplayName("Deve rejeitar rodada com menos de 2 jogadores elegíveis lançando NotEnoughPlayersException")
    void shouldRejectRoundWithInsufficientEligiblePlayers() {
        validRound.getParticipants().forEach(p -> p.setEligible(false));
        validRound.getParticipants().getFirst().setEligible(true);

        when(pokerRoundRepository.findById(100L)).thenReturn(Optional.of(validRound));

        assertThrows(
                NotEnoughPlayersException.class,
                () -> cardDistributionService.distributeCards(100L)
        );

        assertTrue(validRound.getDealtCards().isEmpty());
        assertFalse(validRound.isCardsDealt());
        verify(pokerRoundRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve retornar apenas as cartas privadas do jogador consultado")
    void shouldReturnOnlyRequestedPlayerCards() {
        List<DealtCard> p1Dealt = List.of(
                DealtCard.builder().round(validRound).player(player1).dealOrder(1).build(),
                DealtCard.builder().round(validRound).player(player1).dealOrder(4).build()
        );

        when(pokerRoundRepository.existsById(100L)).thenReturn(true);
        when(dealtCardRepository.findByRoundIdAndPlayerIdOrderByDealOrderAsc(100L, 1L)).thenReturn(p1Dealt);

        PlayerHoleCardsResponse response = cardDistributionService.getPlayerCards(100L, 1L);

        assertEquals(100L, response.roundId());
        assertEquals(1L, response.playerId());
        assertEquals(2, response.cards().size());
    }
}