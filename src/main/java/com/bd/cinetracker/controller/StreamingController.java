package com.bd.cinetracker.controller;

import com.bd.cinetracker.service.TmdbService;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/streaming")
public class StreamingController {

    @Autowired
    private TmdbService tmdbService;

    @GetMapping("/{tipo}/{imdbId}")
    public ResponseEntity<JsonNode> getStreaming(@PathVariable String tipo, @PathVariable String imdbId) {
        JsonNode plataformas = tmdbService.buscarPlataformas(imdbId, tipo);

        if (plataformas == null || plataformas.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(plataformas);
    }
}