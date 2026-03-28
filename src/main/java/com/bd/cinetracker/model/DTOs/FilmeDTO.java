package com.bd.cinetracker.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true) // Ignora campos que não precisamos
public record FilmeDTO(
        @JsonAlias("Title") String titulo,
        @JsonAlias("Year") String ano,
        @JsonAlias("Plot") String sinopse,
        @JsonAlias("imdbRating") String avaliacao,
        @JsonAlias("Runtime") String duracao,
        @JsonAlias("Country") String pais
) {}