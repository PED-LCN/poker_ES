package engenharia.de.software.poker.api.controller;

import engenharia.de.software.poker.api.dto.match.ApiResponse;
import engenharia.de.software.poker.api.dto.match.MatchEntryResponse;
import engenharia.de.software.poker.api.dto.match.MatchListResponse;
import engenharia.de.software.poker.api.service.MatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List; 

@RestController
@RequestMapping("/poker")
@RequiredArgsConstructor
public class MatchController {

    private final MatchService matchService;

    @PostMapping("/{idPartida}/entrar")
    public ResponseEntity<MatchEntryResponse> joinMatch(@PathVariable("idPartida") Long matchId, @RequestParam("idUsuario") Long userId) {
        MatchEntryResponse response = matchService.joinMatch(matchId, userId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{idPartida}/iniciar")
    public ResponseEntity<ApiResponse> startMatch(@PathVariable("idPartida") Long matchId, @RequestParam("idJogador") Long playerId) {
        ApiResponse response = matchService.startMatch(matchId, playerId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/partidasAbertas")
    public ResponseEntity<List<MatchListResponse>> listarPartidasDisponiveis() {
        List<MatchListResponse> partidas = matchService.listarPartidasDisponiveis();
        return ResponseEntity.ok(partidas);
    }
}
