package engenharia.de.software.poker.api.dto.card;

import engenharia.de.software.poker.api.model.enums.Rank;
import engenharia.de.software.poker.api.model.enums.Suit;

public record CardResponse(
        Rank rank,
        Suit suit,
        int dealOrder
) {}