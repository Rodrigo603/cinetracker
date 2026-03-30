package com.bd.cinetracker.DTOs;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SerieDTO(
        @JsonProperty("Title") String titulo,
        @JsonProperty("Year") String ano,
        @JsonProperty("Plot") String descricao,
        @JsonProperty("imdbRating") String notaImdb,
        @JsonProperty("totalSeasons") String qtdTemporadas,
        @JsonProperty("Runtime") String duracaoEpisodio,
        @JsonProperty("Country") String pais,
        @JsonProperty("imdbID") String imdbId,
        @JsonProperty("Poster") String posterUrl,
        @JsonProperty("Type") String tipo
) {}