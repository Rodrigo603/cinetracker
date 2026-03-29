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
        usuarioExistente.setSenha(request.senha());

        usuarioRepository.atualizarPerfilCompleto(usuarioExistente, request.telefone());

        return ResponseEntity.ok("Perfil atualizado com sucesso!");
    }
}