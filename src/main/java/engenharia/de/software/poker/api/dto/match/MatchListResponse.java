package engenharia.de.software.poker.api.dto.match;

import engenharia.de.software.poker.api.model.entity.Match;
import engenharia.de.software.poker.api.model.entity.MatchState;

public record MatchListResponse(
        Long matchId,
        MatchState state,
        int currentPlayers,
        Integer maxPlayers,
        Long creatorId
) {
    public static MatchListResponse fromEntity(Match match) {
        int currentPlayers = 0;
        Integer maxPlayers = null;

        if (match.getPokerTable() != null) {
            if (match.getPokerTable().getPlayers() != null) {
                currentPlayers = match.getPokerTable().getPlayers().size();
            }
            maxPlayers = match.getPokerTable().getMaxPlayers();
        }

        Long creatorId = match.getCreator() != null ? match.getCreator().getId() : null;

        return new MatchListResponse(
                match.getId(),
                match.getState(),
                currentPlayers,
                maxPlayers,
                creatorId
        );
    }
}