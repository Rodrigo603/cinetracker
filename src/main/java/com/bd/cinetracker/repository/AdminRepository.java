package com.bd.cinetracker.repository;

import com.bd.cinetracker.model.Admin;
import org.springframework.beans.factory.annotation.Autowired;
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
            PreparedStatement ps = connection.prepareStatement(sqlTelefone, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, numeroTelefone);
            return ps;
        }, keyHolder);

        Integer idTelefone = keyHolder.getKey().intValue();

        String sqlAdmin = """
            INSERT INTO ADMIN (NOME, EMAIL, SENHA, DT_CADASTRO, FK_TELEFONE_TELEFONE_PK) 
            VALUES (?, ?, ?, ?, ?)
        """;

        jdbcTemplate.update(sqlAdmin,
                admin.getNome(),
                admin.getEmail(),
                admin.getSenha(),
                admin.getDataCadastro(),
                idTelefone
        );
    }

    public Admin buscarPorEmail(String email) {
        String sql = "SELECT * FROM ADMIN WHERE EMAIL = ?";
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> new Admin(
                rs.getInt("ID_ADMIN"),
                rs.getString("NOME"),
                rs.getString("EMAIL"),
                rs.getString("SENHA"),
                rs.getDate("DT_CADASTRO").toLocalDate(),
                rs.getInt("FK_TELEFONE_TELEFONE_PK")
        ), email);
    }

    public Admin buscarPorId(Integer id) {
        String sql = "SELECT * FROM ADMIN WHERE ID_ADMIN = ?";
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> new Admin(
                rs.getInt("ID_ADMIN"),
                rs.getString("NOME"),
                rs.getString("EMAIL"),
                rs.getString("SENHA"),
                rs.getDate("DT_CADASTRO").toLocalDate(),
                rs.getInt("FK_TELEFONE_TELEFONE_PK")
        ), id);
    }
}