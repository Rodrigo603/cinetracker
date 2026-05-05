package com.bd.cinetracker.controller;

import com.bd.cinetracker.DTOs.EpisodioSerieDTO;
import com.bd.cinetracker.model.Serie;
import com.bd.cinetracker.repository.SerieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/series")
public class SerieController {

    @Autowired
    private SerieRepository serieRepository;

    @GetMapping
    public ResponseEntity<List<Serie>> listarSeries() {
        return ResponseEntity.ok(serieRepository.listarTodas());
    }

    @GetMapping("/{id}/episodios")
    public ResponseEntity<List<EpisodioSerieDTO>> listarEpisodios(@PathVariable Integer id) {
        List<EpisodioSerieDTO> episodios = serieRepository.buscarEpisodiosPorSerie(id);
        return ResponseEntity.ok(episodios);
    }
}