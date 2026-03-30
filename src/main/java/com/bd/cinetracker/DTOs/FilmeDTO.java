package com.bd.cinetracker.DTOs;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record FilmeDTO(
        @JsonProperty("Title") String titulo,
        @JsonProperty("Year") String ano,
        @JsonProperty("Plot") String descricao,
        @JsonProperty("imdbRating") String notaImdb,
        @JsonProperty("Runtime") String duracao,
        @JsonProperty("Country") String pais,
        @JsonProperty("imdbID") String imdbId,
        @JsonProperty("Poster") String posterUrl,
        @JsonProperty("BoxOffice") String bilheteria,
        @JsonProperty("Genre") String genero,
        @JsonProperty("Type") String tipo
) {}