package com.bd.cinetracker;

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
        var busca = "";

        System.out.println("\n=== BEM-VINDO AO CINETRACKER ===");

        while (!busca.equalsIgnoreCase("sair")) {
            System.out.println("Digite o nome do filme para buscar (ou 'sair'):");
            busca = leitura.nextLine();

            if (busca.equalsIgnoreCase("sair")) {
                break;
            }

            try {
                var dados = omdbService.buscarFilme(busca);

                if (dados.titulo() == null) {
                    System.out.println("❌ Filme não encontrado na base do OMDb.");
                } else {
                    System.out.println("\n--- RESULTADO DA BUSCA ---");
                    System.out.println("🎬 Título: " + dados.titulo());
                    System.out.println("📅 Ano: " + dados.ano());
                    System.out.println("⏳ Duração: " + dados.duracao());
                    System.out.println("🌍 País: " + dados.pais());
                    System.out.println("⭐ Avaliação (IMDb): " + dados.avaliacao());
                    System.out.println("📝 Sinopse: " + dados.sinopse());
                    System.out.println("--------------------------\n");
                }
            } catch (Exception e) {
                System.out.println("⚠️ Erro ao consultar a API: " + e.getMessage());
            }
        }

        System.out.println("Encerrando o Cinetracker... Até logo!");
    }
}