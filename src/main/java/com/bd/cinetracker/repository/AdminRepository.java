package com.bd.cinetracker.repository;

import com.bd.cinetracker.model.Admin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;

@Repository
public class AdminRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void cadastrarComTelefone(Admin admin, String numeroTelefone) {
        String sqlTelefone = "INSERT INTO TELEFONE (TELEFONE) VALUES (?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sqlTelefone, new String[]{"TELEFONE_PK"});
            ps.setString(1, numeroTelefone);
            return ps;
        }, keyHolder);

        Integer idTelefone;
        if (keyHolder.getKeys() != null && !keyHolder.getKeys().isEmpty()) {
            idTelefone = ((Number) keyHolder.getKeys().values().iterator().next()).intValue();
        } else {
            idTelefone = keyHolder.getKey().intValue();
        }

        String sqlAdmin = """
            INSERT INTO ADMIN (NOME, EMAIL, SENHA, DT_CADASTRO, FK_TELEFONE_TELEFONE_PK) 
            VALUES (?, ?, ?, ?, ?)
        """;

        jdbcTemplate.update(sqlAdmin,
                admin.getNome(),
                admin.getEmail(),
                admin.getSenha(),
                java.sql.Date.valueOf(admin.getDataCadastro()),
                idTelefone
        );
    }

    public void atualizarPerfilCompleto(Admin admin, String novoTelefone) {
        String sqlAdmin = "UPDATE ADMIN SET NOME = ?, EMAIL = ?, SENHA = ? WHERE ID_ADMIN = ?";
        jdbcTemplate.update(sqlAdmin,
                admin.getNome(),
                admin.getEmail(),
                admin.getSenha(),
                admin.getIdAdmin()
        );

        if (admin.getFkTelefone() != null && novoTelefone != null) {
            String sqlTelefone = "UPDATE TELEFONE SET TELEFONE = ? WHERE TELEFONE_PK = ?";
            jdbcTemplate.update(sqlTelefone,
                    novoTelefone,
                    admin.getFkTelefone()
            );
        }
    }

    public Admin buscarPorEmail(String email) {
        String sql = "SELECT * FROM ADMIN WHERE EMAIL = ?";
        try {
            return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> new Admin(
                    rs.getInt("ID_ADMIN"),
                    rs.getString("NOME"),
                    rs.getString("EMAIL"),
                    rs.getString("SENHA"),
                    rs.getDate("DT_CADASTRO").toLocalDate(),
                    rs.getInt("FK_TELEFONE_TELEFONE_PK")
            ), email);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public Admin buscarPorId(Integer id) {
        String sql = "SELECT * FROM ADMIN WHERE ID_ADMIN = ?";
        try {
            return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> new Admin(
                    rs.getInt("ID_ADMIN"),
                    rs.getString("NOME"),
                    rs.getString("EMAIL"),
                    rs.getString("SENHA"),
                    rs.getDate("DT_CADASTRO").toLocalDate(),
                    rs.getInt("FK_TELEFONE_TELEFONE_PK")
            ), id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public String buscarTelefonePorFk(Integer fkTelefone) {
        if (fkTelefone == null) return null;
        String sql = "SELECT TELEFONE FROM TELEFONE WHERE TELEFONE_PK = ?";
        try {
            return jdbcTemplate.queryForObject(sql, String.class, fkTelefone);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
}