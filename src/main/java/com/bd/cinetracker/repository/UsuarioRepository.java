package com.bd.cinetracker.repository;

import com.bd.cinetracker.model.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.dao.EmptyResultDataAccessException;

import java.util.List;
import java.sql.PreparedStatement;
import java.sql.Statement;

@Repository
public class UsuarioRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void cadastrarComTelefone(Usuario usuario, String numeroTelefone) {
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

        String sqlUsuario = """
        INSERT INTO USUARIO (NOME, EMAIL, SENHA, DT_CADASTRO, FK_TELEFONE_TELEFONE_PK) 
        VALUES (?, ?, ?, ?, ?)
    """;

        jdbcTemplate.update(sqlUsuario,
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getSenha(),
                java.sql.Date.valueOf(usuario.getDataCadastro()),
                idTelefone
        );
    }


    public void atualizarPerfilCompleto(Usuario usuario, String novoTelefone) {
        String sqlUsuario = "UPDATE USUARIO SET NOME = ?, EMAIL = ?, SENHA = ? WHERE ID_USER = ?";
        jdbcTemplate.update(sqlUsuario,
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getSenha(),
                usuario.getId()
        );

        if (usuario.getFkTelefone() != null && novoTelefone != null) {
            String sqlTelefone = "UPDATE TELEFONE SET TELEFONE = ? WHERE TELEFONE_PK = ?";
            jdbcTemplate.update(sqlTelefone,
                    novoTelefone,
                    usuario.getFkTelefone()
            );
        }
    }

    public int deletar(Integer id) {
        String sql = "DELETE FROM USUARIO WHERE ID_USER = ?";
        return jdbcTemplate.update(sql, id);
    }

    public Usuario buscarPorId(Integer id) {
        String sql = """
        SELECT u.*, t.TELEFONE 
        FROM USUARIO u 
        JOIN TELEFONE t ON u.FK_TELEFONE_TELEFONE_PK = t.TELEFONE_PK 
        WHERE u.ID_USER = ?
    """;
        try {
            return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
                Usuario u = new Usuario(
                        rs.getInt("ID_USER"),
                        rs.getString("NOME"),
                        rs.getString("EMAIL"),
                        rs.getString("SENHA"),
                        rs.getDate("DT_CADASTRO").toLocalDate(),
                        rs.getInt("FK_TELEFONE_TELEFONE_PK")
                );
                u.setTelefone(rs.getString("TELEFONE"));
                return u;
            }, id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public Usuario buscarPorEmail(String email) {
        String sql = "SELECT * FROM USUARIO WHERE EMAIL = ?";
        try {
            return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> new Usuario(
                    rs.getInt("ID_USER"),
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

    public List<Usuario> listarTodos() {
        String sql = "SELECT * FROM USUARIO";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Usuario(
                rs.getInt("ID_USER"),
                rs.getString("NOME"),
                rs.getString("EMAIL"),
                rs.getString("SENHA"),
                rs.getDate("DT_CADASTRO").toLocalDate(),
                rs.getInt("FK_TELEFONE_TELEFONE_PK")
        ));
    }
}