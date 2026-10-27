package engenharia.de.software.poker.api.service;

import engenharia.de.software.poker.api.dto.card.CardResponse;
import engenharia.de.software.poker.api.dto.card.ParticipantDealStatusDto;
import engenharia.de.software.poker.api.dto.card.PlayerHoleCardsResponse;
import engenharia.de.software.poker.api.dto.card.RoundDealResponse;
import engenharia.de.software.poker.api.exception.InvalidMatchStateException;
import engenharia.de.software.poker.api.exception.NotEnoughPlayersException;
import engenharia.de.software.poker.api.exception.ResourceNotFoundException;
import engenharia.de.software.poker.api.exception.UnauthorizedActionException;
import engenharia.de.software.poker.api.model.entity.*;
import engenharia.de.software.poker.api.model.enums.Rank;
import engenharia.de.software.poker.api.model.enums.RoundStatus;
import engenharia.de.software.poker.api.model.enums.Suit;
import engenharia.de.software.poker.api.repository.DealtCardRepository;
import engenharia.de.software.poker.api.repository.PokerRoundRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class CardDistributionService {

    public static final int HOLE_CARDS_PER_PLAYER = 2;
    public static final int DEFAULT_MIN_PLAYERS = 2;
    public static final int DEFAULT_MAX_PLAYERS = 9;

    private final PokerRoundRepository pokerRoundRepository;
    private final DealtCardRepository dealtCardRepository;
    private final SecureRandom secureRandom = new SecureRandom();

    private record RawCard(Suit suit, Rank rank) {}

    /**
     * Cria uma nova rodada a partir dos jogadores da mesa da partida (US12)
     * e realiza imediatamente a distribuição de cartas (US13).
     */
    public RoundDealResponse createAndDistributeInitialRound(Match match) {
        PokerTable table = match.getPokerTable();
        if (table == null || table.getPlayers() == null) {
            throw new NotEnoughPlayersException("Not enough players to start the round.");
        }

        PokerRound round = PokerRound.builder()
                .match(match)
                .status(RoundStatus.AGUARDANDO_DISTRIBUICAO)
                .cardsDealt(false)
                .build();

        List<Player> tablePlayers = table.getPlayers();
        for (int i = 0; i < tablePlayers.size(); i++) {
            RoundParticipant participant = RoundParticipant.builder()
                    .round(round)
                    .player(tablePlayers.get(i))
                    .seatOrder(i + 1)
                    .eligible(true)
                    .build();
            round.getParticipants().add(participant);
        }

        PokerRound savedRound = pokerRoundRepository.save(round);
        return distributeCards(savedRound.getId());
    }

    /**
     * Distribui as cartas para uma rodada existente que esteja AGUARDANDO_DISTRIBUICAO.
     */
    public RoundDealResponse distributeCards(Long roundId) {
        PokerRound round = pokerRoundRepository.findById(roundId)
                .orElseThrow(() -> new ResourceNotFoundException("Round not found."));

        // Proteção contra repetição ou estado inválido
        if (round.isCardsDealt()
                || round.getStatus() != RoundStatus.AGUARDANDO_DISTRIBUICAO
                || !round.getDealtCards().isEmpty()) {
            throw new InvalidMatchStateException("Cards have already been dealt for this round or the round is not waiting for deal.");
        }

        List<RoundParticipant> eligibleParticipants = round.getParticipants().stream()
                .filter(RoundParticipant::isEligible)
                .sorted(Comparator.comparingInt(RoundParticipant::getSeatOrder))
                .toList();

        int minPlayers = DEFAULT_MIN_PLAYERS;
        int maxPlayers = DEFAULT_MAX_PLAYERS;
        if (round.getMatch() != null && round.getMatch().getPokerTable() != null) {
            minPlayers = round.getMatch().getPokerTable().getMinPlayersToStart();
            maxPlayers = round.getMatch().getPokerTable().getMaxPlayers();
        }

        if (eligibleParticipants.size() < minPlayers || eligibleParticipants.size() > maxPlayers) {
            throw new NotEnoughPlayersException("Invalid number of eligible players to deal cards.");
        }

        List<RawCard> deck = createShuffledDeck();

        int cursor = 0;
        int dealOrder = 1;

        // Distribuição em 2 voltas (1 carta por jogador elegível em cada volta)
        for (int pass = 0; pass < HOLE_CARDS_PER_PLAYER; pass++) {
            for (RoundParticipant participant : eligibleParticipants) {
                RawCard rawCard = deck.get(cursor++);
                DealtCard dealtCard = DealtCard.builder()
                        .round(round)
                        .player(participant.getPlayer())
                        .suit(rawCard.suit())
                        .rank(rawCard.rank())
                        .dealOrder(dealOrder++)
                        .build();
                round.getDealtCards().add(dealtCard);
            }
        }

        // Preserva o restante do baralho associado à rodada
        int deckPosition = 1;
        while (cursor < deck.size()) {
            RawCard rawCard = deck.get(cursor++);
            RemainingDeckCard remainingCard = RemainingDeckCard.builder()
                    .round(round)
                    .suit(rawCard.suit())
                    .rank(rawCard.rank())
                    .deckPosition(deckPosition++)
                    .build();
            round.getRemainingDeck().add(remainingCard);
        }

        round.setCardsDealt(true);
        round.setStatus(RoundStatus.EM_ANDAMENTO);
        PokerRound savedRound = pokerRoundRepository.save(round);

        List<ParticipantDealStatusDto> participantSummaries = eligibleParticipants.stream()
                .map(p -> new ParticipantDealStatusDto(
                        p.getPlayer().getId(),
                        p.getSeatOrder(),
                        HOLE_CARDS_PER_PLAYER
                ))
                .toList();

        Long matchId = savedRound.getMatch() != null ? savedRound.getMatch().getId() : null;

        return new RoundDealResponse(
                savedRound.getId(),
                matchId,
                savedRound.getStatus(),
                savedRound.getDealtCards().size(),
                savedRound.getRemainingDeck().size(),
                participantSummaries
        );
    }

    public PlayerHoleCardsResponse getPlayerCards(Long roundId, Long playerId) {
        if (!pokerRoundRepository.existsById(roundId)) {
            throw new ResourceNotFoundException("Round not found.");
        }

        List<DealtCard> playerCards = dealtCardRepository.findByRoundIdAndPlayerIdOrderByDealOrderAsc(roundId, playerId);
        if (playerCards.isEmpty()) {
            throw new UnauthorizedActionException("Player is not part of this round or cards have not been dealt yet.");
        }

        List<CardResponse> cards = playerCards.stream()
                .map(c -> new CardResponse(c.getRank(), c.getSuit(), c.getDealOrder()))
                .toList();

        return new PlayerHoleCardsResponse(roundId, playerId, cards);
    }

    private List<RawCard> createShuffledDeck() {
        List<RawCard> deck = new ArrayList<>(52);
        for (Suit suit : Suit.values()) {
            for (Rank rank : Rank.values()) {
                deck.add(new RawCard(suit, rank));
            }
        }
        Collections.shuffle(deck, secureRandom);
        return deck;
    }
}