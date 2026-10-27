package engenharia.de.software.poker.api.dto.card;

public record ParticipantDealStatusDto(
        Long playerId,
        int seatOrder,
        int cardsReceived
) {}