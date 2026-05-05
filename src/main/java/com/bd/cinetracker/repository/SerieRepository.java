package com.bd.cinetracker.repository;

import com.bd.cinetracker.DTOs.EpisodioSerieDTO;
import com.bd.cinetracker.model.Serie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class SerieRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<Serie> listarTodas() {
        String sql = "SELECT * FROM SERIE ORDER BY RAND()";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Serie s = new Serie();
            s.setIdMidia(rs.getInt("ID_MIDIA"));
            s.setTitulo(rs.getString("TITULO"));
            s.setDescricao(rs.getString("DESCRICAO"));
            s.setPosterUrl(rs.getString("POSTER_URL"));
            s.setNotaImdb(rs.getDouble("NOTA_IMDB"));
            s.setQtdTemporadas(rs.getInt("QTD_TEMPORADAS"));
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
        String sql = "SELECT * FROM SERIE WHERE ID_MIDIA = ?";
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
            Serie s = new Serie();
            s.setIdMidia(rs.getInt("ID_MIDIA"));
            s.setTitulo(rs.getString("TITULO"));
            s.setDescricao(rs.getString("DESCRICAO"));
            s.setPosterUrl(rs.getString("POSTER_URL"));
            s.setNotaImdb(rs.getDouble("NOTA_IMDB"));
            s.setQtdTemporadas(rs.getInt("QTD_TEMPORADAS"));
            s.setAnoLancamento(rs.getInt("ANO_LANCAMENTO"));
            return s;
        }, id);
    }
}