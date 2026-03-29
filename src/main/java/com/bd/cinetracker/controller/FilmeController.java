package com.bd.cinetracker.controller;

import com.bd.cinetracker.model.Filme;
import com.bd.cinetracker.repository.FilmeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/filmes")
public class FilmeController {

    @Autowired
    private FilmeRepository filmeRepository;

    @GetMapping
    public ResponseEntity<List<Filme>> listarFilmes() {
        return ResponseEntity.ok(filmeRepository.listarTodos());
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<Filme>> buscarFilmePorTitulo(@RequestParam String titulo) {
        List<Filme> filmes = filmeRepository.buscarPorTitulo(titulo);

        if (filmes.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(filmes);
    }
}