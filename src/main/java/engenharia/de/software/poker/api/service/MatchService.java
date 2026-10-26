package engenharia.de.software.poker.api.service;

import engenharia.de.software.poker.api.dto.match.ApiResponse;
import engenharia.de.software.poker.api.dto.match.MatchEntryResponse;
import engenharia.de.software.poker.api.exception.*;
import engenharia.de.software.poker.api.model.entity.Match;
import engenharia.de.software.poker.api.model.entity.MatchState;
import engenharia.de.software.poker.api.model.entity.Player;
import engenharia.de.software.poker.api.model.entity.PokerTable;
import engenharia.de.software.poker.api.repository.MatchRepository;
import engenharia.de.software.poker.api.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MatchService {

    private final MatchRepository matchRepository;
    private final PlayerRepository playerRepository;

    // Implementação da US10 - Entrada em uma partida
    @Transactional
    public MatchEntryResponse joinMatch(Long matchId, Long playerId) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new ResourceNotFoundException("Match not found."));

        if (match.getState() != MatchState.WAITING_PLAYERS) {
            throw new MatchAlreadyStartedException("Cannot join. The match has already started or finished.");
        }

        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new ResourceNotFoundException("Player not found."));

        PokerTable table = match.getPokerTable();

        if (!table.isAvailable()) {
            throw new TableFullException("Cannot join. The table is full.");
        }

        table.addPlayer(player);
        matchRepository.save(match);

        return new MatchEntryResponse(true, "Player successfully joined the match.", matchId, playerId);
    }

    // Implementação da US12 - Início de uma partida
    @Transactional
    public ApiResponse startMatch(Long matchId, Long requestingPlayerId) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new ResourceNotFoundException("Match not found."));

        if (match.getState() != MatchState.WAITING_PLAYERS) {
            throw new InvalidMatchStateException("Match is already in progress or finished.");
        }

        if (!match.getCreator().getId().equals(requestingPlayerId)) {
            throw new UnauthorizedActionException("Only the match creator can start the match.");
        }

        PokerTable table = match.getPokerTable();

        if (table.getPlayers().size() < table.getMinPlayersToStart()) {
            throw new NotEnoughPlayersException("Not enough players to start the match.");
        }

        match.start();
        matchRepository.save(match);

        return new ApiResponse(true, "Match started successfully.");
    }
}
