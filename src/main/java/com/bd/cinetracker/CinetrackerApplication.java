package com.bd.cinetracker;

import com.bd.cinetracker.DTOs.FilmeDTO;
import com.bd.cinetracker.DTOs.SerieDTO;
import com.bd.cinetracker.service.OmdbService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Scanner;

@SpringBootApplication
public class CinetrackerApplication implements CommandLineRunner {

    @Autowired
    private OmdbService omdbService;

    public static void main(String[] args) {
        SpringApplication.run(CinetrackerApplication.class, args);
    }

    @Override
    public void run(String... args) {
        Scanner leitura = new Scanner(System.in);
        String busca = "";

        System.out.println("\n=== BEM-VINDO AO CINETRACKER ===");

        while (!busca.equalsIgnoreCase("sair")) {
            System.out.println("\nDigite o nome (Filme ou Série) ou 'sair':");
            busca = leitura.nextLine();

            if (busca.equalsIgnoreCase("sair")) break;

            try {
                Object resultado = omdbService.buscarMídia(busca);

                if (resultado == null) {
                    System.out.println("❌ Não encontrado.");
                } else if (resultado instanceof FilmeDTO filme) {
                    imprimirFilme(filme);
                } else if (resultado instanceof SerieDTO serie) {
                    imprimirSerie(serie);
                }
            } catch (Exception e) {
                System.out.println("⚠️ Erro: " + e.getMessage());
            }
        }
    }

    private void imprimirFilme(FilmeDTO f) {
        System.out.println("\n--- 🎬 FILME ENCONTRADO ---");
        System.out.println("Título: " + f.titulo());
        System.out.println("Ano: " + f.ano());
        System.out.println("Duração: " + f.duracao());
        System.out.println("Bilheteria: " + f.bilheteria());
        System.out.println("Nota IMDb: " + f.notaImdb());
        System.out.println("ID IMDb: " + f.imdbId());
        System.out.println("Poster: " + f.posterUrl());
        System.out.println("Sinopse: " + f.descricao());
    }

    private void imprimirSerie(SerieDTO s) {
        System.out.println("\n--- 📺 SÉRIE ENCONTRADA ---");
        System.out.println("Título: " + s.titulo());
        System.out.println("Temporadas: " + s.qtdTemporadas());
        System.out.println("Ano: " + s.ano());
        System.out.println("Duração Episódio: " + s.duracaoEpisodio());
        System.out.println("Nota IMDb: " + s.notaImdb());
        System.out.println("ID IMDb: " + s.imdbId());
        System.out.println("Poster: " + s.posterUrl());
        System.out.println("Sinopse: " + s.descricao());
    }
}