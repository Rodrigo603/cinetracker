package com.bd.cinetracker.repository;

import com.bd.cinetracker.model.Filme;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class FilmeRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public int salvar(Filme filme) {
        String sql = """
            INSERT INTO FILME (ID_IMDB, TITULO, DESCRICAO, POSTER_URL, BILHETERIA, DURACAO, ANO_LANCAMENTO, PAIS_ORIGEM, NOTA_IMDB) 
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        return jdbcTemplate.update(
                sql,
                filme.getIdImdb(),
                filme.getTitulo(),
                filme.getDescricao(),
                filme.getPosterUrl(),
                filme.getBilheteria(),
                filme.getDuracao(),
                filme.getAnoLancamento(),
                filme.getPaisOrigem(),
                filme.getNotaImdb()
        );
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
            return f;
        }, termo);
    }

    public List<Filme> listarTodos() {
        String sql = "SELECT * FROM FILME ORDER BY ANO_LANCAMENTO DESC";

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
}