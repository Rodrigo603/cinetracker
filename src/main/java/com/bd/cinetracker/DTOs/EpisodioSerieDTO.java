package com.bd.cinetracker.DTOs;

public record EpisodioSerieDTO(
        Integer numTemporada,
        Integer numEpisodio,
        String titulo,
        Integer duracao,
        String descricao
) {
}
