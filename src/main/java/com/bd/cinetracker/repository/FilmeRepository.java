package com.bd.cinetracker.repository;

import com.bd.cinetracker.model.Filme;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class FilmeRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public Integer salvar(Filme filme) {
        String sql = """
            INSERT INTO FILME (ID_IMDB, TITULO, DESCRICAO, POSTER_URL, BILHETERIA, DURACAO, ANO_LANCAMENTO, PAIS_ORIGEM, NOTA_IMDB) 
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, filme.getIdImdb());
            ps.setString(2, filme.getTitulo());
            ps.setString(3, filme.getDescricao());
            ps.setString(4, filme.getPosterUrl());
            ps.setObject(5, filme.getBilheteria());
            ps.setObject(6, filme.getDuracao());
            ps.setObject(7, filme.getAnoLancamento());
            ps.setString(8, filme.getPaisOrigem());
            ps.setObject(9, filme.getNotaImdb());
            return ps;
        }, keyHolder);

        return keyHolder.getKey().intValue();
    }

    public List<Filme> buscarPorTitulo(String titulo) {
        String sql = "SELECT * FROM FILME WHERE TITULO LIKE ?";
        String termo = "%" + titulo + "%";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Filme f = new Filme();
            f.setIdMidia(rs.getInt("ID_MIDIA"));
            f.setIdImdb(rs.getString("ID_IMDB"));
            f.setTitulo(rs.getString("TITULO"));
            f.setDescricao(rs.getString("DESCRICAO"));
            f.setPosterUrl(rs.getString("POSTER_URL"));
            f.setBilheteria(rs.getDouble("BILHETERIA"));
            f.setDuracao(rs.getInt("DURACAO"));
            f.setAnoLancamento(rs.getInt("ANO_LANCAMENTO"));
            f.setPaisOrigem(rs.getString("PAIS_ORIGEM"));
            f.setNotaImdb(rs.getDouble("NOTA_IMDB"));
            f.setAvaliacao(rs.getDouble("AVALIACAO")); // Leitura para trigger
            return f;
        }, termo);
    }

    public List<Filme> listarTodos() {
        String sql = "SELECT * FROM FILME ORDER BY RAND()";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Filme f = new Filme();
            f.setIdMidia(rs.getInt("ID_MIDIA"));
            f.setIdImdb(rs.getString("ID_IMDB"));
            f.setTitulo(rs.getString("TITULO"));
            f.setDescricao(rs.getString("DESCRICAO"));
            f.setPosterUrl(rs.getString("POSTER_URL"));
            f.setNotaImdb(rs.getDouble("NOTA_IMDB"));
            f.setAnoLancamento(rs.getInt("ANO_LANCAMENTO"));
            f.setDuracao(rs.getInt("DURACAO"));
            return f;
        });
    }

    public int atualizar(Filme filme) {
        String sql = """
            UPDATE FILME 
            SET TITULO = ?, DESCRICAO = ?, POSTER_URL = ?, BILHETERIA = ?, DURACAO = ? 
            WHERE ID_MIDIA = ?
        """;
        return jdbcTemplate.update(
                sql,
                filme.getTitulo(),
                filme.getDescricao(),
                filme.getPosterUrl(),
                filme.getBilheteria(),
                filme.getDuracao(),
                filme.getIdMidia()
        );
    }

    public int deletar(Integer id) {
        String sql = "DELETE FROM FILME WHERE ID_MIDIA = ?";
        return jdbcTemplate.update(sql, id);
    }

    public Filme buscarPorId(Integer id) {
        String sql = """
            SELECT f.*, GROUP_CONCAT(p.NOME_GENERO SEPARATOR ', ') AS GENEROS
            FROM FILME f
            LEFT JOIN PERTENCER p ON f.ID_MIDIA = p.FK_FILME_ID_MIDIA
            WHERE f.ID_MIDIA = ?
            GROUP BY f.ID_MIDIA
        """;

        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
            Filme f = new Filme();
            f.setIdMidia(rs.getInt("ID_MIDIA"));
            f.setIdImdb(rs.getString("ID_IMDB"));
            f.setTitulo(rs.getString("TITULO"));
            f.setDescricao(rs.getString("DESCRICAO"));
            f.setPosterUrl(rs.getString("POSTER_URL"));
            f.setNotaImdb(rs.getDouble("NOTA_IMDB"));
            f.setBilheteria(rs.getDouble("BILHETERIA"));
            f.setAnoLancamento(rs.getInt("ANO_LANCAMENTO"));
            f.setDuracao(rs.getInt("DURACAO"));
            f.setGeneros(rs.getString("GENEROS"));
            f.setAvaliacao(rs.getDouble("AVALIACAO")); // Leitura para trigger
            return f;
        }, id);
    }

    public record FilmeMediaDTO(Integer idMidia, String titulo, String posterUrl, Double media) {}
    public record FilmeAvaliacaoGeneroDTO(Integer idMidia, String titulo, String posterUrl, Integer ano, String genero, Double notaUsuario, String comentario) {}

    public List<FilmeMediaDTO> listarFilmesComMediaAlta() {
        String sql = """
            SELECT f.ID_MIDIA, f.TITULO, f.POSTER_URL, AVG(a.NOTA) AS Media_Avaliacoes
            FROM FILME f
            JOIN AVALIACAO a ON f.ID_MIDIA = a.FK_FILME_ID_MIDIA
            GROUP BY f.ID_MIDIA, f.TITULO, f.POSTER_URL
            HAVING AVG(a.NOTA) >= 4.0
        """;
        return jdbcTemplate.query(sql, (rs, rowNum) -> new FilmeMediaDTO(
                rs.getInt("ID_MIDIA"),
                rs.getString("TITULO"),
                rs.getString("POSTER_URL"),
                rs.getDouble("Media_Avaliacoes")
        ));
    }

    public List<FilmeAvaliacaoGeneroDTO> listarFilmesPorGeneroEAvaliacaoAlta(String generoDesejado) {
        String sql = """
            SELECT v.ID_MIDIA, 
                   MAX(v.Filme) AS TITULO, 
                   MAX(v.POSTER_URL) AS POSTER_URL, 
                   MAX(v.ANO_LANCAMENTO) AS ANO_LANCAMENTO, 
                   v.Genero AS NOME_GENERO, 
                   f.AVALIACAO AS Nota_Usuario, 
                   'Média Geral Alta' AS COMENTARIO
            FROM vw_avaliacoes_positivas_com_genero v
            JOIN FILME f ON v.ID_MIDIA = f.ID_MIDIA
            WHERE v.Genero = ? AND f.AVALIACAO >= 4.0
            GROUP BY v.ID_MIDIA, v.Genero, f.AVALIACAO
        """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> new FilmeAvaliacaoGeneroDTO(
                rs.getInt("ID_MIDIA"),
                rs.getString("TITULO"),
                rs.getString("POSTER_URL"),
                rs.getInt("ANO_LANCAMENTO"),
                rs.getString("NOME_GENERO"),
                rs.getDouble("Nota_Usuario"),
                rs.getString("COMENTARIO")
        ), generoDesejado);
    }

    public List<Filme> listarFilmesSemAvaliacao() {
        String sql = """
            SELECT f.ID_MIDIA, f.ID_IMDB, f.TITULO, f.DESCRICAO, f.POSTER_URL,
                   f.BILHETERIA, f.DURACAO, f.ANO_LANCAMENTO, f.PAIS_ORIGEM, f.NOTA_IMDB
            FROM FILME f
            LEFT JOIN AVALIACAO a ON f.ID_MIDIA = a.FK_FILME_ID_MIDIA
            WHERE a.ID_AVALIACAO IS NULL
        """;
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Filme f = new Filme();
            f.setIdMidia(rs.getInt("ID_MIDIA"));
            f.setIdImdb(rs.getString("ID_IMDB"));
            f.setTitulo(rs.getString("TITULO"));
            f.setDescricao(rs.getString("DESCRICAO"));
            f.setPosterUrl(rs.getString("POSTER_URL"));
            f.setBilheteria(rs.getDouble("BILHETERIA"));
            f.setDuracao(rs.getInt("DURACAO"));
            f.setAnoLancamento(rs.getInt("ANO_LANCAMENTO"));
            f.setPaisOrigem(rs.getString("PAIS_ORIGEM"));
            f.setNotaImdb(rs.getDouble("NOTA_IMDB"));
            return f;
        });
    }
}