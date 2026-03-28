package com.bd.cinetracker.model.DTOs;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record FilmeDTO(
        @JsonAlias("Title") String titulo,
        @JsonAlias("Year") String ano,
        @JsonAlias("Plot") String descricao,
        @JsonAlias("imdbRating") String notaImdb, // Nota da crítica (IMDb)
        @JsonAlias("Runtime") String duracao,
        @JsonAlias("Country") String pais,
        @JsonAlias("imdbID") String imdbId,      // ID único da API (essencial para o V2 do seu banco)
        @JsonAlias("Poster") String posterUrl,   // URL da imagem do filme
        @JsonAlias("BoxOffice") String bilheteria, // Arrecadação
        @JsonAlias("Genre") String genero,
        @JsonAlias("Type") String tipo
) {}