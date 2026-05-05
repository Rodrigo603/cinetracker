package com.bd.cinetracker.controller;

import com.bd.cinetracker.repository.AvaliacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/avaliacoes")
public class AvaliacaoController {

    @Autowired
    private AvaliacaoRepository avaliacaoRepository;

    public record NovaAvaliacaoRequest(Integer idUsuario, Integer idMidia, String tipo, Double nota, String comentario) {}
    public record EdicaoAvaliacaoRequest(Integer idUsuario, Double nota, String comentario) {}

    @PostMapping
    public ResponseEntity<String> avaliar(@RequestBody NovaAvaliacaoRequest req) {
        Integer idFilme = req.tipo().equals("filme") ? req.idMidia() : null;
        Integer idSerie = req.tipo().equals("serie") ? req.idMidia() : null;

        avaliacaoRepository.salvar(req.idUsuario(), idFilme, idSerie, req.nota(), req.comentario());
        return ResponseEntity.ok("Avaliação salva com sucesso!");
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> atualizarAvaliacao(@PathVariable Integer id, @RequestBody EdicaoAvaliacaoRequest req) {
        int alterados = avaliacaoRepository.atualizar(id, req.idUsuario(), req.nota(), req.comentario());
        if (alterados > 0) {
            return ResponseEntity.ok("Avaliação atualizada!");
        }
        return ResponseEntity.status(403).body("Não autorizado ou avaliação não encontrada.");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletarAvaliacao(@PathVariable Integer id, @RequestParam Integer idUsuario) {
        int deletados = avaliacaoRepository.deletar(id, idUsuario);
        if (deletados > 0) {
            return ResponseEntity.ok("Avaliação removida!");
        }
        return ResponseEntity.status(403).body("Não autorizado ou avaliação não encontrada.");
    }

    @GetMapping("/filme/{id}")
    public ResponseEntity<List<AvaliacaoRepository.AvaliacaoViewDTO>> getFilmeReviews(@PathVariable Integer id) {
        return ResponseEntity.ok(avaliacaoRepository.listarPorFilme(id));
    }

    @GetMapping("/serie/{id}")
    public ResponseEntity<List<AvaliacaoRepository.AvaliacaoViewDTO>> getSerieReviews(@PathVariable Integer id) {
        return ResponseEntity.ok(avaliacaoRepository.listarPorSerie(id));
    }
}