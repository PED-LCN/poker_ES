package engenharia.de.software.poker.api.dto.card;

import engenharia.de.software.poker.api.model.enums.RoundStatus;
import java.util.List;

public record RoundDealResponse(
        Long roundId,
        Long matchId,
        RoundStatus status,
        int totalDealtCards,
        int remainingDeckCount,
        List<ParticipantDealStatusDto> participants
) {}