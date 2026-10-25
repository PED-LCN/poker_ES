package com.example.jogos.controller;

import com.example.jogos.model.Partida; 
import com.example.jogos.service.PartidaService; 
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/partidas")
public class PartidaController {

    private final PartidaService partidaService;

   
    public PartidaController(PartidaService partidaService) {
        this.partidaService = partidaService;
    }

  
    @GetMapping("/disponiveis")
    public ResponseEntity<List<Partida>> listarPartidasDisponiveis() {
       
        List<Partida> partidasDisponiveis = partidaService.encontrarPartidasDisponiveis();
        
        return ResponseEntity.ok(partidasDisponiveis);
    }
}