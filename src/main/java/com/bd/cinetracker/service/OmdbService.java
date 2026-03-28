package com.bd.cinetracker.service;

import com.bd.cinetracker.model.FilmeDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class OmdbService {

    @Value("${omdb.api.key}")
    private String apiKey;

    private final RestClient restClient;

    public OmdbService() {
        this.restClient = RestClient.create();
    }

    public FilmeDTO buscarFilme(String nomeFilme) {
        // Substitui espaços por + para a URL (ex: "The Matrix" -> "The+Matrix")
        String busca = nomeFilme.replace(" ", "+");
        String url = "https://www.omdbapi.com/?t=" + busca + "&apikey=" + apiKey;

        return restClient.get()
                .uri(url)
                .retrieve()
                .body(FilmeDTO.class);
    }
}