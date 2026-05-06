package com.bd.cinetracker.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class GeneroRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void salvarGeneroSeNaoExistir(String nomeGenero) {
        String sql = "INSERT IGNORE INTO GENERO (NOME_GENERO) VALUES (?)";
        jdbcTemplate.update(sql, nomeGenero);
    }

    public void vincularFilme(Integer idFilme, String nomeGenero) {
        String sql = "INSERT INTO PERTENCER (FK_FILME_ID_MIDIA, NOME_GENERO) VALUES (?, ?)";
        jdbcTemplate.update(sql, idFilme, nomeGenero);
    }

    public void vincularSerie(Integer idSerie, String nomeGenero) {
        String sql = "INSERT INTO PERTENCER (FK_SERIE_ID_MIDIA, NOME_GENERO) VALUES (?, ?)";
        jdbcTemplate.update(sql, idSerie, nomeGenero);
    }
}