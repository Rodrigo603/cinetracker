package com.bd.cinetracker.model.DTOs;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SerieDTO(
        @JsonAlias("Title") String titulo,
        @JsonAlias("Year") String ano,
        @JsonAlias("Plot") String descricao,
        @JsonAlias("imdbRating") String notaImdb,
        @JsonAlias("totalSeasons") String qtdTemporadas, // Campo específico de série
        @JsonAlias("Runtime") String duracaoEpisodio,
        @JsonAlias("Country") String pais,
        @JsonAlias("imdbID") String imdbId,
        @JsonAlias("Poster") String posterUrl,
        @JsonAlias("Type") String tipo
) {}