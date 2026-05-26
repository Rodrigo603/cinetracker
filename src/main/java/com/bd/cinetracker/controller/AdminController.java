package com.bd.cinetracker.controller;

import com.bd.cinetracker.DTOs.FilmeDTO;
import com.bd.cinetracker.DTOs.SerieDTO;
import com.bd.cinetracker.model.Admin;
import com.bd.cinetracker.model.Filme;
import com.bd.cinetracker.model.Serie;
import com.bd.cinetracker.model.Usuario;
import com.bd.cinetracker.repository.AdminRepository;
import com.bd.cinetracker.repository.FilmeRepository;
import com.bd.cinetracker.repository.SerieRepository;
import com.bd.cinetracker.repository.UsuarioRepository;
import com.bd.cinetracker.repository.GeneroRepository;
import com.bd.cinetracker.repository.AvaliacaoRepository;
import com.bd.cinetracker.service.OmdbService;
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

    @Autowired
    private SerieRepository serieRepository;

    @Autowired
    private GeneroRepository generoRepository;

    @Autowired
    private AvaliacaoRepository avaliacaoRepository;

    @Autowired
    private OmdbService omdbService;

    public record AdminRequest(String nome, String email, String senha, String telefone) {}
    public record AdminLoginResponse(Integer id, String nome, String email, boolean admin) {}
    public record NovoFilmeRequest(String titulo) {}
    public record AdminResponse(Integer id, String nome, String email, String telefone) {}

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

    @GetMapping("/{id}")
    public ResponseEntity<AdminResponse> buscarAdminPorId(@PathVariable Integer id) {
        Admin a = adminRepository.buscarPorId(id);
        if (a == null) return ResponseEntity.notFound().build();

        String telefone = adminRepository.buscarTelefonePorFk(a.getFkTelefone());
        return ResponseEntity.ok(new AdminResponse(a.getIdAdmin(), a.getNome(), a.getEmail(), telefone));
    }

    @PutMapping("/atualizar/{id}")
    public ResponseEntity<String> atualizarPerfilAdmin(@PathVariable Integer id, @RequestBody AdminRequest request) {
        Admin adminExistente = adminRepository.buscarPorId(id);
        if (adminExistente == null) {
            return ResponseEntity.notFound().build();
        }

        adminExistente.setNome(request.nome());
        adminExistente.setEmail(request.email());

        if (request.senha() != null && !request.senha().trim().isEmpty()) {
            adminExistente.setSenha(request.senha());
        }

        adminRepository.atualizarPerfilCompleto(adminExistente, request.telefone());
        return ResponseEntity.ok("Perfil atualizado com sucesso!");
    }

    @DeleteMapping("/usuarios/{id}")
    public ResponseEntity<String> deletarUsuario(@PathVariable Integer id) {
        int linhasAfetadas = usuarioRepository.deletar(id);
        if (linhasAfetadas > 0) {
            return ResponseEntity.ok("Usuário deletado com sucesso do sistema.");
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/series")
    public ResponseEntity<String> adicionarSerie(@RequestBody NovoFilmeRequest request) {
        SerieDTO dto = omdbService.buscarSerie(request.titulo());

        if (dto == null) {
            return ResponseEntity.status(404).body("Série não encontrada na API do OMDb.");
        }

        Serie serie = new Serie();
        serie.setTitulo(dto.titulo());
        serie.setDescricao(dto.descricao());
        serie.setPosterUrl(dto.posterUrl());
        serie.setIdImdb(dto.imdbId());

        // CORREÇÃO: Limitar país a 50 caracteres e pegar o primeiro da lista
        if (dto.pais() != null && !dto.pais().equals("N/A")) {
            String paisPrincipal = dto.pais().split(",")[0].trim();
            serie.setPaisOrigem(paisPrincipal.length() > 50 ? paisPrincipal.substring(0, 50) : paisPrincipal);
        }

        try {
            if (dto.ano() != null && !dto.ano().equals("N/A")) {
                serie.setAnoLancamento(Integer.parseInt(dto.ano().substring(0, 4)));
            }
            if (dto.qtdTemporadas() != null && !dto.qtdTemporadas().equals("N/A")) {
                serie.setQtdTemporadas(Integer.parseInt(dto.qtdTemporadas()));
            }
            if (dto.notaImdb() != null && !dto.notaImdb().equals("N/A")) {
                serie.setNotaImdb(Double.parseDouble(dto.notaImdb()));
            }
        } catch (Exception e) {
            System.out.println("Erro ao converter dados numéricos da Série: " + e.getMessage());
        }

        Integer idSerieGerada = serieRepository.salvar(serie);

        if (dto.genero() != null && !dto.genero().equals("N/A")) {
            String[] generos = dto.genero().split(",");
            for (String nomeGenero : generos) {
                String generoTraduzido = traduzirGenero(nomeGenero);
                generoRepository.salvarGeneroSeNaoExistir(generoTraduzido);
                generoRepository.vincularSerie(idSerieGerada, generoTraduzido);
            }
        }
        return ResponseEntity.ok("Série e gêneros adicionados ao catálogo com sucesso!");
    }

    @PutMapping("/series/{id}")
    public ResponseEntity<String> atualizarSerie(@PathVariable Integer id, @RequestBody Serie serie) {
        serie.setIdMidia(id);
        int linhasAfetadas = serieRepository.atualizar(serie);
        if (linhasAfetadas > 0) {
            return ResponseEntity.ok("Série atualizada com sucesso!");
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/series/{id}")
    public ResponseEntity<String> deletarSerie(@PathVariable Integer id) {
        int linhasAfetadas = serieRepository.deletar(id);
        if (linhasAfetadas > 0) {
            return ResponseEntity.ok("Série removida do catálogo.");
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/filmes")
    public ResponseEntity<String> adicionarMidia(@RequestBody NovoFilmeRequest request) {
        Object resultado = omdbService.buscarMídia(request.titulo());

        if (resultado == null) {
            return ResponseEntity.status(404).body("Mídia não encontrada na API do OMDb.");
        }

        if (resultado instanceof FilmeDTO dto) {
            Filme filme = new Filme();
            filme.setTitulo(dto.titulo());
            filme.setDescricao(dto.descricao());
            filme.setPosterUrl(dto.posterUrl());
            filme.setIdImdb(dto.imdbId());

            // CORREÇÃO: Limitar país a 50 caracteres e pegar o primeiro da lista
            if (dto.pais() != null && !dto.pais().equals("N/A")) {
                String paisPrincipal = dto.pais().split(",")[0].trim();
                filme.setPaisOrigem(paisPrincipal.length() > 50 ? paisPrincipal.substring(0, 50) : paisPrincipal);
            }

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
                System.out.println("Erro ao converter dados numéricos do Filme: " + e.getMessage());
            }

            Integer idFilmeGerado = filmeRepository.salvar(filme);

            // Salvando Gêneros (FILMES)
            if (dto.genero() != null && !dto.genero().equals("N/A")) {
                String[] generos = dto.genero().split(",");
                for (String nomeGenero : generos) {
                    String generoTraduzido = traduzirGenero(nomeGenero);
                    generoRepository.salvarGeneroSeNaoExistir(generoTraduzido);
                    generoRepository.vincularFilme(idFilmeGerado, generoTraduzido);
                }
            }
            return ResponseEntity.ok("Filme e gêneros adicionados ao catálogo com sucesso!");
        }

        else if (resultado instanceof SerieDTO dto) {
            Serie serie = new Serie();
            serie.setTitulo(dto.titulo());
            serie.setDescricao(dto.descricao());
            serie.setPosterUrl(dto.posterUrl());
            serie.setIdImdb(dto.imdbId());

            if (dto.pais() != null && !dto.pais().equals("N/A")) {
                String paisPrincipal = dto.pais().split(",")[0].trim();
                serie.setPaisOrigem(paisPrincipal.length() > 50 ? paisPrincipal.substring(0, 50) : paisPrincipal);
            }

            try {
                if (dto.ano() != null && !dto.ano().equals("N/A")) {
                    serie.setAnoLancamento(Integer.parseInt(dto.ano().substring(0, 4)));
                }
                if (dto.qtdTemporadas() != null && !dto.qtdTemporadas().equals("N/A")) {
                    serie.setQtdTemporadas(Integer.parseInt(dto.qtdTemporadas()));
                }
                if (dto.notaImdb() != null && !dto.notaImdb().equals("N/A")) {
                    serie.setNotaImdb(Double.parseDouble(dto.notaImdb()));
                }
            } catch (Exception e) {
                System.out.println("Erro ao converter dados numéricos da Série: " + e.getMessage());
            }

            Integer idSerieGerada = serieRepository.salvar(serie);

            if (dto.genero() != null && !dto.genero().equals("N/A")) {
                String[] generos = dto.genero().split(",");
                for (String nomeGenero : generos) {
                    String generoTraduzido = traduzirGenero(nomeGenero);
                    generoRepository.salvarGeneroSeNaoExistir(generoTraduzido);
                    generoRepository.vincularSerie(idSerieGerada, generoTraduzido);
                }
            }
            return ResponseEntity.ok("Série e gêneros adicionados ao catálogo com sucesso!");
        }

        return ResponseEntity.badRequest().body("Tipo de mídia não suportado.");
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

    private String traduzirGenero(String generoIngles) {
        return switch (generoIngles.trim()) {
            case "Action" -> "Ação";
            case "Adventure" -> "Aventura";
            case "Animation" -> "Animação";
            case "Biography" -> "Biografia";
            case "Comedy" -> "Comédia";
            case "Crime" -> "Crime";
            case "Documentary" -> "Documentário";
            case "Drama" -> "Drama";
            case "Family" -> "Família";
            case "Fantasy" -> "Fantasia";
            case "Film-Noir" -> "Film-Noir";
            case "History" -> "História";
            case "Horror" -> "Terror";
            case "Music" -> "Música";
            case "Musical" -> "Musical";
            case "Mystery" -> "Mistério";
            case "Romance" -> "Romance";
            case "Sci-Fi" -> "Ficção Científica";
            case "Short" -> "Curta-metragem";
            case "Sport" -> "Esporte";
            case "Thriller" -> "Suspense";
            case "War" -> "Guerra";
            case "Western" -> "Faroeste";
            default -> generoIngles.trim();
        };
    }

    @DeleteMapping("/comentarios/{idAvaliacao}")
    public ResponseEntity<String> deletarComentario(@PathVariable Integer idAvaliacao) {
        int deletados = avaliacaoRepository.deletarComoAdmin(idAvaliacao);
        if (deletados > 0) {
            return ResponseEntity.ok("Comentário removido com sucesso pelo administrador.");
        }
        return ResponseEntity.status(404).body("Avaliação não encontrada.");
    }
}