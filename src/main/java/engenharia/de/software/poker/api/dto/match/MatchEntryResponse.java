package engenharia.de.software.poker.api.dto.match;

import engenharia.de.software.poker.api.model.entity.Player;

public record MatchEntryResponse(boolean success, String message, Long matchId, Long playerId) {
}
