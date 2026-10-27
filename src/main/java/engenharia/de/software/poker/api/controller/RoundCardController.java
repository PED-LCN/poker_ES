package engenharia.de.software.poker.api.controller;

import engenharia.de.software.poker.api.dto.card.PlayerHoleCardsResponse;
import engenharia.de.software.poker.api.dto.card.RoundDealResponse;
import engenharia.de.software.poker.api.service.CardDistributionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/poker/rodadas")
@RequiredArgsConstructor
public class RoundCardController {

    private final CardDistributionService cardDistributionService;

    @PostMapping("/{idRodada}/distribuir")
    public ResponseEntity<RoundDealResponse> distributeCards(@PathVariable Long idRodada) {
        RoundDealResponse summary = cardDistributionService.distributeCards(idRodada);
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/{idRodada}/jogadores/{idJogador}/cartas")
    public ResponseEntity<PlayerHoleCardsResponse> getPlayerHoleCards(
            @PathVariable Long idRodada,
            @PathVariable Long idJogador
    ) {
        PlayerHoleCardsResponse response = cardDistributionService.getPlayerCards(idRodada, idJogador);
        return ResponseEntity.ok(response);
    }
}