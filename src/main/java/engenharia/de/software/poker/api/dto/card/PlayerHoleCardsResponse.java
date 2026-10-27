package engenharia.de.software.poker.api.dto.card;

import java.util.List;

public record PlayerHoleCardsResponse(
        Long roundId,
        Long playerId,
        List<CardResponse> cards
) {}