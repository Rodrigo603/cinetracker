package com.bd.cinetracker.controller;

import com.bd.cinetracker.repository.AvaliacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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
    public ResponseEntity<List<AvaliacaoRepository.AvaliacaoViewDTO>> getFilmeReviews(@PathVariable Integer id, @RequestParam(required = false) Integer idUsuarioLogado) {
        return ResponseEntity.ok(avaliacaoRepository.listarPorFilme(id, idUsuarioLogado));
    }

    @GetMapping("/serie/{id}")
    public ResponseEntity<List<AvaliacaoRepository.AvaliacaoViewDTO>> getSerieReviews(@PathVariable Integer id, @RequestParam(required = false) Integer idUsuarioLogado) {
        return ResponseEntity.ok(avaliacaoRepository.listarPorSerie(id, idUsuarioLogado));
    }

    @PostMapping("/{id}/curtir")
    public ResponseEntity<Map<String, Boolean>> toggleCurtida(@PathVariable Integer id, @RequestParam Integer idUsuario) {
        boolean isLiked = avaliacaoRepository.toggleLike(id, idUsuario);
        return ResponseEntity.ok(Map.of("curtido", isLiked));
    }

    @PostMapping("/{id}/comentarios")
    public ResponseEntity<String> comentar(@PathVariable Integer id, @RequestBody Map<String, Object> payload) {
        Integer idUsuario = (Integer) payload.get("idUsuario");
        String texto = (String) payload.get("texto");
        avaliacaoRepository.adicionarComentario(id, idUsuario, texto);
        return ResponseEntity.ok("Comentário adicionado!");
    }

    @GetMapping("/{id}/comentarios")
    public ResponseEntity<List<AvaliacaoRepository.ComentarioReviewDTO>> listarComentarios(@PathVariable Integer id) {
        return ResponseEntity.ok(avaliacaoRepository.listarComentariosDaAvaliacao(id));
    }

    public record EdicaoComentarioRequest(Integer idUsuario, String texto, boolean admin) {}

    @PutMapping("/comentarios/{idComentario}")
    public ResponseEntity<String> atualizarComentarioResp(@PathVariable Integer idComentario, @RequestBody EdicaoComentarioRequest req) {
        int alterados = avaliacaoRepository.atualizarComentario(idComentario, req.idUsuario(), req.texto(), req.admin());
        if (alterados > 0) {
            return ResponseEntity.ok("Comentário atualizado!");
        }
        return ResponseEntity.status(403).body("Não autorizado.");
    }

    @DeleteMapping("/comentarios/{idComentario}")
    public ResponseEntity<String> deletarComentarioResp(@PathVariable Integer idComentario, @RequestParam Integer idUsuario, @RequestParam boolean admin) {
        int deletados = avaliacaoRepository.deletarComentarioResp(idComentario, idUsuario, admin);
        if (deletados > 0) {
            return ResponseEntity.ok("Comentário removido!");
        }
        return ResponseEntity.status(403).body("Não autorizado.");
    }
}