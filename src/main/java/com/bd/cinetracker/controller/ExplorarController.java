package com.bd.cinetracker.controller;

import com.bd.cinetracker.model.Filme;
import com.bd.cinetracker.model.Serie;
import com.bd.cinetracker.repository.FilmeRepository;
import com.bd.cinetracker.repository.SerieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/explorar")
@CrossOrigin("*") // Libera o CORS para o seu front-end (HTML) poder acessar os dados
public class ExplorarController {

    @Autowired
    private FilmeRepository filmeRepository;

    @Autowired
    private SerieRepository serieRepository;

    // Rota da Consulta 1 (JOIN + GROUP BY + HAVING)
    @GetMapping("/filmes-media-alta")
    public List<FilmeRepository.FilmeMediaDTO> getFilmesMediaAlta() {
        return filmeRepository.listarFilmesComMediaAlta();
    }

    // Rota da Consulta 2 (2 JOINs + WHERE) - Usa parâmetro dinâmico na URL
    @GetMapping("/filmes-por-genero/{genero}")
    public List<FilmeRepository.FilmeAvaliacaoGeneroDTO> getFilmesPorGenero(@PathVariable String genero) {
        return filmeRepository.listarFilmesPorGeneroEAvaliacaoAlta(genero);
    }

    // Rota da Consulta 3 (Anti Join)
    @GetMapping("/filmes-sem-avaliacao")
    public List<Filme> getFilmesSemAvaliacao() {
        return filmeRepository.listarFilmesSemAvaliacao();
    }

    // Rota da Consulta 4 (Subconsulta)
    @GetMapping("/series-acima-da-media")
    public List<Serie> getSeriesAcimaMedia() {
        return serieRepository.listarSeriesAcimaDaMedia();
    }

    // Rota Bônus (Anti Join de Séries)
    @GetMapping("/series-sem-avaliacao")
    public List<Serie> getSeriesSemAvaliacao() {
        return serieRepository.listarSeriesSemAvaliacao();
    }
}