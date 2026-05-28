package com.bd.cinetracker.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class TmdbService {

    @Value("${tmdb.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public JsonNode buscarPlataformas(String imdbId, String tipo) {
        if (tipo == null || imdbId == null) return null;

        try {
            String findUrl = "https://api.themoviedb.org/3/find/" + imdbId + "?api_key=" + apiKey + "&external_source=imdb_id";
            JsonNode findResult = restTemplate.getForObject(findUrl, JsonNode.class);

            String tmdbId = null;

            String tipoLimpo = tipo.toLowerCase().replace("é", "e");
            String endpointTipo = tipoLimpo.contains("filme") ? "movie" : "tv";

            if (tipoLimpo.contains("filme") && findResult.path("movie_results").size() > 0) {
                tmdbId = findResult.path("movie_results").get(0).path("id").asText();
            } else if (tipoLimpo.contains("serie") && findResult.path("tv_results").size() > 0) {
                tmdbId = findResult.path("tv_results").get(0).path("id").asText();
            }

            if (tmdbId == null) return null;

            String providersUrl = "https://api.themoviedb.org/3/" + endpointTipo + "/" + tmdbId + "/watch/providers?api_key=" + apiKey;
            JsonNode providersResult = restTemplate.getForObject(providersUrl, JsonNode.class);

            return providersResult.path("results").path("BR");

        } catch (Exception e) {
            System.out.println("Erro ao buscar dados de streaming no TMDB: " + e.getMessage());
            return null;
        }
    }
}