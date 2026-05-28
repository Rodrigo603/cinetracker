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

    public int deletarComoAdmin(Integer idAvaliacao) {
        String sql = "DELETE FROM AVALIACAO WHERE ID_AVALIACAO = ?";
        return jdbcTemplate.update(sql, idAvaliacao);
    }

    public record AvaliacaoViewDTO(Integer idAvaliacao, Integer idUsuario, String avaliador, Double nota, String comentario, java.sql.Timestamp data, Integer qtdLikes, Boolean curtidoPeloUsuario) {}

    public record ComentarioReviewDTO(Integer idComentario, Integer idUsuario, String nomeUsuario, String texto, java.sql.Timestamp data) {}

    public List<AvaliacaoViewDTO> listarPorFilme(Integer idFilme, Integer idUsuarioLogado) {
        String sql = "SELECT v.ID_AVALIACAO, v.ID_USUARIO, v.AVALIADOR, v.NOTA, v.COMENTARIO, v.DATA_AVALIACAO, " +
                "(SELECT COUNT(*) FROM INTERAGIR i WHERE i.FK_AVALIACAO_ID = v.ID_AVALIACAO) AS QTD_LIKES, " +
                "(SELECT COUNT(*) FROM INTERAGIR i WHERE i.FK_AVALIACAO_ID = v.ID_AVALIACAO AND i.FK_USUARIO_ID_USER = ?) AS CURTIDO " +
                "FROM vw_avaliacoes_recentes v WHERE v.ID_FILME = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new AvaliacaoViewDTO(
                rs.getInt("ID_AVALIACAO"), rs.getInt("ID_USUARIO"), rs.getString("AVALIADOR"),
                rs.getDouble("NOTA"), rs.getString("COMENTARIO"), rs.getTimestamp("DATA_AVALIACAO"),
                rs.getInt("QTD_LIKES"), rs.getInt("CURTIDO") > 0
        ), idUsuarioLogado != null ? idUsuarioLogado : -1, idFilme);
    }

    public List<AvaliacaoViewDTO> listarPorSerie(Integer idSerie, Integer idUsuarioLogado) {
        String sql = "SELECT v.ID_AVALIACAO, v.ID_USUARIO, v.AVALIADOR, v.NOTA, v.COMENTARIO, v.DATA_AVALIACAO, " +
                "(SELECT COUNT(*) FROM INTERAGIR i WHERE i.FK_AVALIACAO_ID = v.ID_AVALIACAO) AS QTD_LIKES, " +
                "(SELECT COUNT(*) FROM INTERAGIR i WHERE i.FK_AVALIACAO_ID = v.ID_AVALIACAO AND i.FK_USUARIO_ID_USER = ?) AS CURTIDO " +
                "FROM vw_avaliacoes_recentes v WHERE v.ID_SERIE = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new AvaliacaoViewDTO(
                rs.getInt("ID_AVALIACAO"), rs.getInt("ID_USUARIO"), rs.getString("AVALIADOR"),
                rs.getDouble("NOTA"), rs.getString("COMENTARIO"), rs.getTimestamp("DATA_AVALIACAO"),
                rs.getInt("QTD_LIKES"), rs.getInt("CURTIDO") > 0
        ), idUsuarioLogado != null ? idUsuarioLogado : -1, idSerie);
    }

    public boolean toggleLike(Integer idAvaliacao, Integer idUsuario) {
        String checkSql = "SELECT COUNT(*) FROM INTERAGIR WHERE FK_AVALIACAO_ID = ? AND FK_USUARIO_ID_USER = ?";
        Integer count = jdbcTemplate.queryForObject(checkSql, Integer.class, idAvaliacao, idUsuario);
        if (count != null && count > 0) {
            jdbcTemplate.update("DELETE FROM INTERAGIR WHERE FK_AVALIACAO_ID = ? AND FK_USUARIO_ID_USER = ?", idAvaliacao, idUsuario);
            return false;
        } else {
            jdbcTemplate.update("INSERT INTO INTERAGIR (FK_AVALIACAO_ID, FK_USUARIO_ID_USER) VALUES (?, ?)", idAvaliacao, idUsuario);
            return true;
        }
    }

    public void adicionarComentario(Integer idAvaliacao, Integer idUsuario, String texto) {
        jdbcTemplate.update("INSERT INTO COMENTARIO_AVALIACAO (FK_AVALIACAO_ID, FK_USUARIO_ID_USER, TEXTO) VALUES (?, ?, ?)", idAvaliacao, idUsuario, texto);
    }

    public List<ComentarioReviewDTO> listarComentariosDaAvaliacao(Integer idAvaliacao) {
        String sql = "SELECT c.ID_COMENTARIO, c.FK_USUARIO_ID_USER, u.NOME, c.TEXTO, c.DATA_COMENTARIO " +
                "FROM COMENTARIO_AVALIACAO c JOIN USUARIO u ON c.FK_USUARIO_ID_USER = u.ID_USER " +
                "WHERE c.FK_AVALIACAO_ID = ? ORDER BY c.DATA_COMENTARIO ASC";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new ComentarioReviewDTO(
                rs.getInt("ID_COMENTARIO"), rs.getInt("FK_USUARIO_ID_USER"), rs.getString("NOME"), rs.getString("TEXTO"), rs.getTimestamp("DATA_COMENTARIO")
        ), idAvaliacao);
    }

    public int atualizarComentario(Integer idComentario, Integer idUsuario, String novoTexto, boolean isAdmin) {
        if (isAdmin) {
            return jdbcTemplate.update("UPDATE COMENTARIO_AVALIACAO SET TEXTO = ? WHERE ID_COMENTARIO = ?", novoTexto, idComentario);
        } else {
            return jdbcTemplate.update("UPDATE COMENTARIO_AVALIACAO SET TEXTO = ? WHERE ID_COMENTARIO = ? AND FK_USUARIO_ID_USER = ?", novoTexto, idComentario, idUsuario);
        }
    }

    public int deletarComentarioResp(Integer idComentario, Integer idUsuario, boolean isAdmin) {
        if (isAdmin) {
            return jdbcTemplate.update("DELETE FROM COMENTARIO_AVALIACAO WHERE ID_COMENTARIO = ?", idComentario);
        } else {
            return jdbcTemplate.update("DELETE FROM COMENTARIO_AVALIACAO WHERE ID_COMENTARIO = ? AND FK_USUARIO_ID_USER = ?", idComentario, idUsuario);
        }
    }
}