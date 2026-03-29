package com.bd.cinetracker.controller;

import com.bd.cinetracker.model.Admin;
import com.bd.cinetracker.model.Filme;
import com.bd.cinetracker.repository.AdminRepository;
import com.bd.cinetracker.repository.FilmeRepository;
import com.bd.cinetracker.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private FilmeRepository filmeRepository;

    public record AdminRequest(String nome, String email, String senha, String telefone) {}

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
    public ResponseEntity<String> adicionarFilme(@RequestBody Filme filme) {
        filmeRepository.salvar(filme);
        return ResponseEntity.ok("Filme adicionado ao catálogo!");
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
}