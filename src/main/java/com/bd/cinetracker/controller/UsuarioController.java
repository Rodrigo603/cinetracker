package com.bd.cinetracker.controller;

import com.bd.cinetracker.model.Usuario;
import com.bd.cinetracker.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public record UsuarioRequest(String nome, String email, String senha, String telefone) {}

    public record UsuarioResponse(Integer id, String nome, String email, String telefone) {}
    public record LoginRequest(String email, String senha) {}
    public record LoginResponse(Integer id, String nome, String email, boolean admin) {}

    @PostMapping("/cadastrar")
    public ResponseEntity<String> cadastrar(@RequestBody UsuarioRequest request) {
        Usuario novoUsuario = new Usuario();
        novoUsuario.setNome(request.nome());
        novoUsuario.setEmail(request.email());
        novoUsuario.setSenha(request.senha());
        novoUsuario.setDataCadastro(LocalDate.now());

        usuarioRepository.cadastrarComTelefone(novoUsuario, request.telefone());
        return ResponseEntity.ok("Usuário cadastrado com sucesso!");
    }

    @PutMapping("/atualizar/{id}")
    public ResponseEntity<String> atualizarPerfil(@PathVariable Integer id, @RequestBody UsuarioRequest request) {
        Usuario usuarioExistente = usuarioRepository.buscarPorId(id);

        if (usuarioExistente == null) {
            return ResponseEntity.notFound().build();
        }

        usuarioExistente.setNome(request.nome());
        usuarioExistente.setEmail(request.email());

        if (request.senha() != null && !request.senha().trim().isEmpty()) {
            usuarioExistente.setSenha(request.senha());
        }

        usuarioRepository.atualizarPerfilCompleto(usuarioExistente, request.telefone());

        return ResponseEntity.ok("Perfil atualizado com sucesso!");
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponse> buscarPorId(@PathVariable Integer id) {
        Usuario u = usuarioRepository.buscarPorId(id);
        if (u == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(new UsuarioResponse(u.getId(), u.getNome(), u.getEmail(), u.getTelefone()));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        Usuario u = usuarioRepository.buscarPorEmail(request.email());
        if (u == null || !u.getSenha().equals(request.senha())) {
            return ResponseEntity.status(401).body("Credenciais inválidas");
        }
        return ResponseEntity.ok(new LoginResponse(u.getId(), u.getNome(), u.getEmail(), false));
    }
}