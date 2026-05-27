package com.bd.cinetracker.repository;

import com.bd.cinetracker.model.Lista;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class ListaRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public Integer criar(Lista lista, Integer idUsuario) {
        String sqlLista = "INSERT INTO LISTA (NOME_LISTA) VALUES (?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sqlLista, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, lista.getNomeLista());
            return ps;
        }, keyHolder);

        Integer idLista;
        if (keyHolder.getKeys() != null && !keyHolder.getKeys().isEmpty()) {
            idLista = ((Number) keyHolder.getKeys().values().iterator().next()).intValue();
        } else {
            idLista = keyHolder.getKey().intValue();
        }

        String sqlCria = "INSERT INTO CRIA (FK_LISTA_ID_LISTA, FK_USUARIO_ID_USER) VALUES (?, ?)";
        jdbcTemplate.update(sqlCria, idLista, idUsuario);

        return idLista;
    }

    public List<Lista> listarPorUsuario(Integer idUsuario) {
        String sql = """
            SELECT l.ID_LISTA, l.NOME_LISTA
            FROM LISTA l
            JOIN CRIA c ON l.ID_LISTA = c.FK_LISTA_ID_LISTA
            WHERE c.FK_USUARIO_ID_USER = ?
            ORDER BY l.ID_LISTA DESC
        """;
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Lista l = new Lista();
            l.setIdLista(rs.getInt("ID_LISTA"));
            l.setNomeLista(rs.getString("NOME_LISTA"));
            return l;
        }, idUsuario);
    }

    public boolean pertenceAoUsuario(Integer idLista, Integer idUsuario) {
        String sql = "SELECT COUNT(*) FROM CRIA WHERE FK_LISTA_ID_LISTA = ? AND FK_USUARIO_ID_USER = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, idLista, idUsuario);
        return count != null && count > 0;
    }

    public int renomear(Integer idLista, String novoNome) {
        String sql = "UPDATE LISTA SET NOME_LISTA = ? WHERE ID_LISTA = ?";
        return jdbcTemplate.update(sql, novoNome, idLista);
    }

    public int deletar(Integer idLista) {
        String sql = "DELETE FROM LISTA WHERE ID_LISTA = ?";
        return jdbcTemplate.update(sql, idLista);
    }

    public void adicionarFilme(Integer idLista, Integer idFilme) {
        String sql = "INSERT INTO CONTEM (FK_LISTA_ID_LISTA, FK_FILME_ID_MIDIA) VALUES (?, ?)";
        jdbcTemplate.update(sql, idLista, idFilme);
    }

    public void adicionarSerie(Integer idLista, Integer idSerie) {
        String sql = "INSERT INTO CONTEM (FK_LISTA_ID_LISTA, FK_SERIE_ID_MIDIA) VALUES (?, ?)";
        jdbcTemplate.update(sql, idLista, idSerie);
    }

    public void removerItem(Integer idContem) {
        String sql = "DELETE FROM CONTEM WHERE ID_CONTEM = ?";
        jdbcTemplate.update(sql, idContem);
    }

    public List<java.util.Map<String, Object>> listarItens(Integer idLista) {
        String sql = """
            SELECT c.ID_CONTEM,
                   COALESCE(f.TITULO, s.TITULO) AS titulo,
                   CASE WHEN c.FK_FILME_ID_MIDIA IS NOT NULL THEN 'FILME' ELSE 'SERIE' END AS tipo,
                   COALESCE(c.FK_FILME_ID_MIDIA, c.FK_SERIE_ID_MIDIA) AS idMidia
            FROM CONTEM c
            LEFT JOIN FILME f ON c.FK_FILME_ID_MIDIA = f.ID_MIDIA
            LEFT JOIN SERIE s ON c.FK_SERIE_ID_MIDIA = s.ID_MIDIA
            WHERE c.FK_LISTA_ID_LISTA = ?
            ORDER BY c.ID_CONTEM DESC
        """;
        return jdbcTemplate.queryForList(sql, idLista);
    }

    public boolean itemJaNaLista(Integer idLista, Integer idFilme, Integer idSerie) {
        if (idFilme != null) {
            String sql = "SELECT COUNT(*) FROM CONTEM WHERE FK_LISTA_ID_LISTA = ? AND FK_FILME_ID_MIDIA = ?";
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class, idLista, idFilme);
            return count != null && count > 0;
        } else {
            String sql = "SELECT COUNT(*) FROM CONTEM WHERE FK_LISTA_ID_LISTA = ? AND FK_SERIE_ID_MIDIA = ?";
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class, idLista, idSerie);
            return count != null && count > 0;
        }
    }
}