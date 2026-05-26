package com.bd.cinetracker.repository;

import com.bd.cinetracker.DTOs.EpisodioSerieDTO;
import com.bd.cinetracker.model.Serie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class SerieRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public int atualizar(Serie serie) {
        String sql = """
            UPDATE SERIE
            SET TITULO = ?, DESCRICAO = ?, POSTER_URL = ?, QTD_TEMPORADAS = ?, ANO_LANCAMENTO = ?
            WHERE ID_MIDIA = ?
        """;
        return jdbcTemplate.update(sql,
                serie.getTitulo(),
                serie.getDescricao(),
                serie.getPosterUrl(),
                serie.getQtdTemporadas(),
                serie.getAnoLancamento(),
                serie.getIdMidia());
    }

    public int deletar(Integer id) {
        return jdbcTemplate.update("DELETE FROM SERIE WHERE ID_MIDIA = ?", id);
    }

    public Integer salvar(Serie serie) {
        String sql = """
            INSERT INTO SERIE (ID_IMDB, TITULO, DESCRICAO, POSTER_URL, QTD_TEMPORADAS, ANO_LANCAMENTO, PAIS_ORIGEM, NOTA_IMDB) 
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, serie.getIdImdb());
            ps.setString(2, serie.getTitulo());
            ps.setString(3, serie.getDescricao());
            ps.setString(4, serie.getPosterUrl());
            ps.setObject(5, serie.getQtdTemporadas());
            ps.setObject(6, serie.getAnoLancamento());
            ps.setString(7, serie.getPaisOrigem());
            ps.setObject(8, serie.getNotaImdb());
            return ps;
        }, keyHolder);

        return keyHolder.getKey().intValue();
    }

    public List<Serie> buscarPorTitulo(String titulo) {
        String sql = "SELECT * FROM SERIE WHERE TITULO LIKE ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Serie s = new Serie();
            s.setIdMidia(rs.getInt("ID_MIDIA"));
            s.setTitulo(rs.getString("TITULO"));
            s.setDescricao(rs.getString("DESCRICAO"));
            s.setPosterUrl(rs.getString("POSTER_URL"));
            s.setNotaImdb(rs.getDouble("NOTA_IMDB"));
            s.setQtdTemporadas(rs.getInt("QTD_TEMPORADAS"));
            s.setAnoLancamento(rs.getInt("ANO_LANCAMENTO"));
            s.setAvaliacao(rs.getDouble("AVALIACAO")); // Leitura para trigger
            return s;
        }, "%" + titulo + "%");
    }


    public List<Serie> listarTodas() {
        String sql = """
            SELECT s.*, GROUP_CONCAT(p.NOME_GENERO SEPARATOR ', ') AS GENEROS
            FROM SERIE s
            LEFT JOIN PERTENCER p ON s.ID_MIDIA = p.FK_SERIE_ID_MIDIA
            GROUP BY s.ID_MIDIA
            ORDER BY RAND()
        """;
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Serie s = new Serie();
            s.setIdMidia(rs.getInt("ID_MIDIA"));
            s.setTitulo(rs.getString("TITULO"));
            s.setDescricao(rs.getString("DESCRICAO"));
            s.setPosterUrl(rs.getString("POSTER_URL"));
            s.setNotaImdb(rs.getDouble("NOTA_IMDB"));
            s.setQtdTemporadas(rs.getInt("QTD_TEMPORADAS"));
            s.setGeneros(rs.getString("GENEROS")); // Campo mapeado para o front-end
            return s;
        });
    }

    public List<EpisodioSerieDTO> buscarEpisodiosPorSerie(Integer idSerie) {
        String sql = """
            SELECT t.NUM_TEMPORADA, e.NUM_EPISODIO, e.TITULO, e.DURACAO, e.DESCRICAO
            FROM TEMPORADA t
            JOIN EPISODIO e ON t.ID_TEMPORADA = e.FK_TEMPORADA_ID_TEMPORADA
            WHERE t.FK_SERIE_ID_MIDIA = ?
            ORDER BY t.NUM_TEMPORADA, e.NUM_EPISODIO
        """;
        return jdbcTemplate.query(sql, (rs, rowNum) -> new EpisodioSerieDTO(
                rs.getInt("NUM_TEMPORADA"),
                rs.getInt("NUM_EPISODIO"),
                rs.getString("TITULO"),
                rs.getInt("DURACAO"),
                rs.getString("DESCRICAO")
        ), idSerie);
    }

    public Serie buscarPorId(Integer id) {
        String sql = """
            SELECT s.*, GROUP_CONCAT(p.NOME_GENERO SEPARATOR ', ') AS GENEROS
            FROM SERIE s
            LEFT JOIN PERTENCER p ON s.ID_MIDIA = p.FK_SERIE_ID_MIDIA
            WHERE s.ID_MIDIA = ?
            GROUP BY s.ID_MIDIA
        """;

        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
            Serie s = new Serie();
            s.setIdMidia(rs.getInt("ID_MIDIA"));
            s.setTitulo(rs.getString("TITULO"));
            s.setDescricao(rs.getString("DESCRICAO"));
            s.setPosterUrl(rs.getString("POSTER_URL"));
            s.setNotaImdb(rs.getDouble("NOTA_IMDB"));
            s.setQtdTemporadas(rs.getInt("QTD_TEMPORADAS"));
            s.setAnoLancamento(rs.getInt("ANO_LANCAMENTO"));
            s.setGeneros(rs.getString("GENEROS"));
            s.setAvaliacao(rs.getDouble("AVALIACAO")); // Leitura para trigger
            return s;
        }, id);
    }

    public List<Serie> listarSeriesAcimaDaMedia() {
        String sql = """
            SELECT *
            FROM SERIE
            WHERE NOTA_IMDB > (
                SELECT AVG(NOTA_IMDB) 
                FROM SERIE 
                WHERE NOTA_IMDB IS NOT NULL
            )
        """;
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Serie s = new Serie();
            s.setIdMidia(rs.getInt("ID_MIDIA"));
            s.setTitulo(rs.getString("TITULO"));
            s.setDescricao(rs.getString("DESCRICAO"));
            s.setPosterUrl(rs.getString("POSTER_URL"));
            s.setNotaImdb(rs.getDouble("NOTA_IMDB"));
            s.setQtdTemporadas(rs.getInt("QTD_TEMPORADAS"));
            s.setAnoLancamento(rs.getInt("ANO_LANCAMENTO"));
            return s;
        });
    }

    public List<Serie> listarSeriesSemAvaliacao() {
        String sql = """
            SELECT s.ID_MIDIA, s.TITULO, s.DESCRICAO, s.POSTER_URL,
                   s.NOTA_IMDB, s.QTD_TEMPORADAS, s.ANO_LANCAMENTO
            FROM SERIE s
            LEFT JOIN AVALIACAO a ON s.ID_MIDIA = a.FK_SERIE_ID_MIDIA
            WHERE a.ID_AVALIACAO IS NULL
        """;
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Serie s = new Serie();
            s.setIdMidia(rs.getInt("ID_MIDIA"));
            s.setTitulo(rs.getString("TITULO"));
            s.setDescricao(rs.getString("DESCRICAO"));
            s.setPosterUrl(rs.getString("POSTER_URL"));
            s.setNotaImdb(rs.getDouble("NOTA_IMDB"));
            s.setQtdTemporadas(rs.getInt("QTD_TEMPORADAS"));
            s.setAnoLancamento(rs.getInt("ANO_LANCAMENTO"));
            return s;
        });
    }
}