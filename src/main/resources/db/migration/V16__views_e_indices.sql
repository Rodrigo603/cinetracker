-- Visão 1: 3 JOINs + WHERE (Avaliações positivas com Gênero)
CREATE VIEW vw_avaliacoes_positivas_com_genero AS
SELECT u.NOME AS Avaliador, f.TITULO AS Filme, p.NOME_GENERO AS Genero, a.NOTA, a.COMENTARIO
FROM AVALIACAO a
JOIN USUARIO u ON a.FK_USUARIO_ID_USER = u.ID_USER
JOIN FILME f ON a.FK_FILME_ID_MIDIA = f.ID_MIDIA
JOIN PERTENCER p ON f.ID_MIDIA = p.FK_FILME_ID_MIDIA
WHERE a.NOTA >= 4.0;

-- Visão 2: 1 JOIN + Subconsulta (Filmes aclamados)
CREATE VIEW vw_filmes_aclamados_bilheteria AS
SELECT f.TITULO, f.BILHETERIA, f.NOTA_IMDB, p.NOME_GENERO
FROM FILME f
JOIN PERTENCER p ON f.ID_MIDIA = p.FK_FILME_ID_MIDIA
WHERE f.NOTA_IMDB > (SELECT AVG(NOTA_IMDB) FROM FILME WHERE NOTA_IMDB IS NOT NULL);


-- Índice 1: Na coluna NOTA da tabela AVALIACAO
CREATE INDEX idx_avaliacao_nota ON AVALIACAO(NOTA);

-- Índice 2: Na coluna NOTA_IMDB da tabela FILME
CREATE INDEX idx_filme_notaimdb ON FILME(NOTA_IMDB);

-- Índice 3: Na coluna NOTA_IMDB da tabela SERIE
CREATE INDEX idx_serie_notaimdb ON SERIE(NOTA_IMDB);