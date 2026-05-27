package com.bd.cinetracker.controller;

import com.bd.cinetracker.model.Lista;
import com.bd.cinetracker.repository.ListaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/listas")
public class ListaController {

    @Autowired
    private ListaRepository listaRepository;

    public record CriarListaRequest(String nomeLista, Integer idUsuario) {}
    public record EditarListaRequest(String nomeLista, Integer idUsuario) {}
    public record AdicionarItemRequest(Integer idUsuario, Integer idFilme, Integer idSerie) {}

    @PostMapping
    public ResponseEntity<?> criarLista(@RequestBody CriarListaRequest request) {
        if (request.nomeLista() == null || request.nomeLista().isBlank()) {
            return ResponseEntity.badRequest().body("O nome da lista é obrigatório.");
        }
        if (request.idUsuario() == null) {
            return ResponseEntity.badRequest().body("O id do usuário é obrigatório.");
        }

        Lista lista = new Lista();
        lista.setNomeLista(request.nomeLista());

        Integer idLista = listaRepository.criar(lista, request.idUsuario());
        return ResponseEntity.ok(Map.of("idLista", idLista));
    }

    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<Lista>> listarPorUsuario(@PathVariable Integer idUsuario) {
        return ResponseEntity.ok(listaRepository.listarPorUsuario(idUsuario));
    }

    @PutMapping("/{idLista}")
    public ResponseEntity<String> editar(@PathVariable Integer idLista, @RequestBody EditarListaRequest request) {
        if (!listaRepository.pertenceAoUsuario(idLista, request.idUsuario())) {
            return ResponseEntity.status(403).body("Acesso negado.");
        }
        if (request.nomeLista() == null || request.nomeLista().isBlank()) {
            return ResponseEntity.badRequest().body("O nome da lista é obrigatório.");
        }
        listaRepository.renomear(idLista, request.nomeLista());
        return ResponseEntity.ok("Lista updated com sucesso.");
    }

    @DeleteMapping("/{idLista}")
    public ResponseEntity<String> deletar(@PathVariable Integer idLista, @RequestParam Integer idUsuario) {
        if (!listaRepository.pertenceAoUsuario(idLista, idUsuario)) {
            return ResponseEntity.status(403).body("Acesso negado.");
        }
        listaRepository.deletar(idLista);
        return ResponseEntity.ok("Lista removida com sucesso.");
    }

    @GetMapping("/{idLista}/itens")
    public ResponseEntity<?> listarItens(@PathVariable Integer idLista, @RequestParam Integer idUsuario) {
        if (!listaRepository.pertenceAoUsuario(idLista, idUsuario)) {
            return ResponseEntity.status(403).body("Acesso negado.");
        }
        return ResponseEntity.ok(listaRepository.listarItens(idLista));
    }

    @PostMapping("/{idLista}/itens")
    public ResponseEntity<String> adicionarItem(@PathVariable Integer idLista, @RequestBody AdicionarItemRequest request) {
        if (!listaRepository.pertenceAoUsuario(idLista, request.idUsuario())) {
            return ResponseEntity.status(403).body("Acesso negado.");
        }
        if (request.idFilme() == null && request.idSerie() == null) {
            return ResponseEntity.badRequest().body("Informe idFilme ou idSerie.");
        }
        if (request.idFilme() != null && request.idSerie() != null) {
            return ResponseEntity.badRequest().body("Informe apenas idFilme ou idSerie, não ambos.");
        }
        if (listaRepository.itemJaNaLista(idLista, request.idFilme(), request.idSerie())) {
            return ResponseEntity.badRequest().body("Este item já está na lista.");
        }
        if (request.idFilme() != null) {
            listaRepository.adicionarFilme(idLista, request.idFilme());
        } else {
            listaRepository.adicionarSerie(idLista, request.idSerie());
        }
        return ResponseEntity.ok("Item adicionado à lista.");
    }

    @DeleteMapping("/{idLista}/itens/{idContem}")
    public ResponseEntity<String> removerItem(
            @PathVariable Integer idLista,
            @PathVariable Integer idContem,
            @RequestParam Integer idUsuario) {
        if (!listaRepository.pertenceAoUsuario(idLista, idUsuario)) {
            return ResponseEntity.status(403).body("Acesso negado.");
        }
        listaRepository.removerItem(idContem);
        return ResponseEntity.ok("Item removido da lista.");
    }
}