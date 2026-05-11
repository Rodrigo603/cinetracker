package com.bd.cinetracker.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AvaliacaoRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void salvar(Integer idUsuario, Integer idFilme, Integer idSerie, Double nota, String comentario) {
        String sql = "INSERT INTO AVALIACAO (FK_USUARIO_ID_USER, FK_FILME_ID_MIDIA, FK_SERIE_ID_MIDIA, NOTA, COMENTARIO) VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, idUsuario, idFilme, idSerie, nota, comentario);
    }

    public int atualizar(Integer idAvaliacao, Integer idUsuario, Double nota, String comentario) {
        String sql = "UPDATE AVALIACAO SET NOTA = ?, COMENTARIO = ? WHERE ID_AVALIACAO = ? AND FK_USUARIO_ID_USER = ?";
        return jdbcTemplate.update(sql, nota, comentario, idAvaliacao, idUsuario);
    }

    public int deletar(Integer idAvaliacao, Integer idUsuario) {
        String sql = "DELETE FROM AVALIACAO WHERE ID_AVALIACAO = ? AND FK_USUARIO_ID_USER = ?";
        return jdbcTemplate.update(sql, idAvaliacao, idUsuario);
    }

    public record AvaliacaoViewDTO(Integer idAvaliacao, Integer idUsuario, String avaliador, Double nota, String comentario, java.sql.Timestamp data) {}

    public List<AvaliacaoViewDTO> listarPorFilme(Integer idFilme) {
        String sql = "SELECT ID_AVALIACAO, ID_USUARIO, AVALIADOR, NOTA, COMENTARIO, DATA_AVALIACAO FROM vw_avaliacoes_recentes WHERE ID_FILME = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new AvaliacaoViewDTO(
                rs.getInt("ID_AVALIACAO"), rs.getInt("ID_USUARIO"), rs.getString("AVALIADOR"),
                rs.getDouble("NOTA"), rs.getString("COMENTARIO"), rs.getTimestamp("DATA_AVALIACAO")
        ), idFilme);
    }

    public List<AvaliacaoViewDTO> listarPorSerie(Integer idSerie) {
        String sql = "SELECT ID_AVALIACAO, ID_USUARIO, AVALIADOR, NOTA, COMENTARIO, DATA_AVALIACAO FROM vw_avaliacoes_recentes WHERE ID_SERIE = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new AvaliacaoViewDTO(
                rs.getInt("ID_AVALIACAO"), rs.getInt("ID_USUARIO"), rs.getString("AVALIADOR"),
                rs.getDouble("NOTA"), rs.getString("COMENTARIO"), rs.getTimestamp("DATA_AVALIACAO")
        ), idSerie);
    }
    public int deletarComoAdmin(Integer idAvaliacao) {
        String sql = "DELETE FROM AVALIACAO WHERE ID_AVALIACAO = ?";
        return jdbcTemplate.update(sql, idAvaliacao);
    }
}