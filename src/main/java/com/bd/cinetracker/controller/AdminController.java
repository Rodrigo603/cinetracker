package com.bd.cinetracker.controller;

import com.bd.cinetracker.DTOs.FilmeDTO;
import com.bd.cinetracker.model.Admin;
import com.bd.cinetracker.model.Filme;
import com.bd.cinetracker.repository.AdminRepository;
import com.bd.cinetracker.repository.FilmeRepository;
import com.bd.cinetracker.repository.UsuarioRepository;
import com.bd.cinetracker.service.OmdbService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.bd.cinetracker.model.Usuario;

import java.util.List;
import java.time.LocalDate;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private FilmeRepository filmeRepository;

    @Autowired
    private OmdbService omdbService;

    public record AdminRequest(String nome, String email, String senha, String telefone) {}
    public record AdminLoginResponse(Integer id, String nome, String email, boolean admin) {}

    // Novo Record para receber apenas o título do front-end
    public record NovoFilmeRequest(String titulo) {}

    @PostMapping("/novo-admin")
    public ResponseEntity<String> criarAdmin(@RequestBody AdminRequest request) {
        Admin novoAdmin = new Admin();
        novoAdmin.setNome(request.nome());
        novoAdmin.setEmail(request.email());
        novoAdmin.setSenha(request.senha());
        novoAdmin.setDataCadastro(LocalDate.now());

        adminRepository.cadastrarComTelefone(novoAdmin, request.telefone());
        return ResponseEntity.ok("Novo administrador criado com sucesso!");
    }

    @DeleteMapping("/usuarios/{id}")
    public ResponseEntity<String> deletarUsuario(@PathVariable Integer id) {
        int linhasAfetadas = usuarioRepository.deletar(id);
        if (linhasAfetadas > 0) {
            return ResponseEntity.ok("Usuário deletado com sucesso do sistema.");
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/filmes")
    public ResponseEntity<String> adicionarFilme(@RequestBody NovoFilmeRequest request) {
        Object resultado = omdbService.buscarMídia(request.titulo());

        if (resultado == null) {
            return ResponseEntity.status(404).body("Filme não encontrado na API do OMDb.");
        }

        if (resultado instanceof FilmeDTO dto) {
            Filme filme = new Filme();
            filme.setTitulo(dto.titulo());
            filme.setDescricao(dto.descricao());
            filme.setPosterUrl(dto.posterUrl());
            filme.setIdImdb(dto.imdbId());
            filme.setPaisOrigem(dto.pais());

            try {
                if (dto.ano() != null && !dto.ano().equals("N/A")) {
                    filme.setAnoLancamento(Integer.parseInt(dto.ano().substring(0, 4)));
                }
                if (dto.duracao() != null && !dto.duracao().equals("N/A")) {
                    filme.setDuracao(Integer.parseInt(dto.duracao().replace(" min", "")));
                }
                if (dto.notaImdb() != null && !dto.notaImdb().equals("N/A")) {
                    filme.setNotaImdb(Double.parseDouble(dto.notaImdb()));
                }
                if (dto.bilheteria() != null && !dto.bilheteria().equals("N/A")) {
                    String limpaBilheteria = dto.bilheteria().replaceAll("[$,]", "");
                    filme.setBilheteria(Double.parseDouble(limpaBilheteria));
                }
            } catch (Exception e) {
                System.out.println("Erro ao converter números do OMDb: " + e.getMessage());
            }

            filmeRepository.salvar(filme);
            return ResponseEntity.ok("Filme '" + filme.getTitulo() + "' adicionado ao catálogo com sucesso!");
        }

        return ResponseEntity.badRequest().body("O título buscado não é um filme (poderá ser uma série).");
    }

    @PutMapping("/filmes/{id}")
    public ResponseEntity<String> atualizarFilme(@PathVariable Integer id, @RequestBody Filme filme) {
        filme.setIdMidia(id);
        int linhasAfetadas = filmeRepository.atualizar(filme);

        if (linhasAfetadas > 0) {
            return ResponseEntity.ok("Filme atualizado com sucesso!");
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/filmes/{id}")
    public ResponseEntity<String> deletarFilme(@PathVariable Integer id) {
        int linhasAfetadas = filmeRepository.deletar(id);

        if (linhasAfetadas > 0) {
            return ResponseEntity.ok("Filme removido do catálogo.");
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginAdmin(@RequestBody AdminRequest request) {
        Admin a = adminRepository.buscarPorEmail(request.email());
        if (a == null || !a.getSenha().equals(request.senha())) {
            return ResponseEntity.status(401).body("Credenciais inválidas");
        }
        return ResponseEntity.ok(new AdminLoginResponse(a.getIdAdmin(), a.getNome(), a.getEmail(), true));
    }

    @GetMapping("/usuarios")
    public ResponseEntity<List<Usuario>> listarUsuarios() {
        return ResponseEntity.ok(usuarioRepository.listarTodos());
    }
}