package com.bd.cinetracker.service;

import com.bd.cinetracker.DTOs.FilmeDTO;
import com.bd.cinetracker.DTOs.SerieDTO;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class OmdbService {

    @Value("${omdb.api.key}")
    private String apiKey;

    private final RestClient restClient = RestClient.create();

    public Object buscarMídia(String nome) {
        String busca = nome.replace(" ", "+");
        String url = "https://www.omdbapi.com/?t=" + busca + "&apikey=" + apiKey;

        // Pegamos o JSON bruto para checar o "Type"
        JsonNode json = restClient.get().uri(url).retrieve().body(JsonNode.class);

        if (json == null || json.get("Response").asText().equals("False")) {
            return null;
        }

        String tipo = json.get("Type").asText();

        if ("series".equals(tipo)) {
            return restClient.get().uri(url).retrieve().body(SerieDTO.class);
        } else {
            return restClient.get().uri(url).retrieve().body(FilmeDTO.class);
        }
    }
}