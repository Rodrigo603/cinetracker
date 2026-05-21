CREATE TABLE LOG_AVALIACAO (
    ID_LOG INT AUTO_INCREMENT PRIMARY KEY,
    ID_AVALIACAO INT,
    NOTA_ANTIGA DECIMAL(3,1),
    NOTA_NOVA DECIMAL(3,1),
    DATA_ALTERACAO TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Funções
CREATE FUNCTION classificar_nota(nota DECIMAL(3,1))
RETURNS VARCHAR(20)
DETERMINISTIC
BEGIN
    DECLARE classificacao VARCHAR(20);

    IF nota IS NULL THEN
        SET classificacao = 'Sem Avaliação';
    ELSEIF nota >= 8.5 THEN
        SET classificacao = 'Aclamado';
    ELSEIF nota >= 7.0 THEN
        SET classificacao = 'Muito Bom';
    ELSEIF nota >= 5.0 THEN
        SET classificacao = 'Bom';
    ELSE
        SET classificacao = 'Ruim';
    END IF;

    RETURN classificacao;
END ;


CREATE FUNCTION total_avaliacoes_usuario(p_id_usuario INT)
RETURNS INT
READS SQL DATA
BEGIN
    DECLARE v_total INT;

    SELECT COUNT(*) INTO v_total
    FROM AVALIACAO
    WHERE FK_USUARIO_ID_USER = p_id_usuario;

    RETURN v_total;
END ;


-- Procedures

CREATE PROCEDURE atualizar_senha_usuario(
    IN p_id_user INT,
    IN p_nova_senha VARCHAR(255)
)
BEGIN
    UPDATE USUARIO
    SET SENHA = p_nova_senha
    WHERE ID_USER = p_id_user;
END ;



CREATE PROCEDURE gerar_relatorio_usuario(
    IN p_id_usuario INT,
    OUT p_relatorio TEXT
)
BEGIN
    DECLARE done INT DEFAULT FALSE;
    DECLARE v_tipo VARCHAR(10);
    DECLARE v_titulo VARCHAR(255);
    DECLARE v_nota DECIMAL(3,1);
    DECLARE v_resultado TEXT DEFAULT '--- Histórico de Avaliações ---\n';

    -- O cursor busca dados de múltiplas tabelas para o relatório
    DECLARE cur_avaliacoes CURSOR FOR
        SELECT
            CASE WHEN a.FK_FILME_ID_MIDIA IS NOT NULL THEN 'Filme' ELSE 'Série' END as tipo,
            COALESCE(f.TITULO, s.TITULO) as titulo,
            a.NOTA
        FROM AVALIACAO a
        LEFT JOIN FILME f ON a.FK_FILME_ID_MIDIA = f.ID_MIDIA
        LEFT JOIN SERIE s ON a.FK_SERIE_ID_MIDIA = s.ID_MIDIA
        WHERE a.FK_USUARIO_ID_USER = p_id_usuario;

    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;

    OPEN cur_avaliacoes;

    read_loop: LOOP
        FETCH cur_avaliacoes INTO v_tipo, v_titulo, v_nota;
        IF done THEN
            LEAVE read_loop;
        END IF;

        -- Formatação linha a linha impossível de ser feita em um UPDATE simples
        IF v_tipo = 'Filme' THEN
            SET v_resultado = CONCAT(v_resultado, '🎬 [FILME] ', v_titulo, ' - Nota: ', v_nota, '\n');
        ELSE
            SET v_resultado = CONCAT(v_resultado, '📺 [SÉRIE] ', v_titulo, ' - Nota: ', v_nota, '\n');
        END IF;
    END LOOP;

    CLOSE cur_avaliacoes;
    SET p_relatorio = v_resultado;
END ;

-- Triggers

-- No CineTracker, é comum que os usuários mudem de opinião e editem suas notas ao longo do tempo.
-- Este gatilho garante uma trilha de auditoria automática e confiável, salvando o histórico (nota antiga e nova) sempre que uma avaliação for alterada,
-- permitindo análises futuras sem sobrecarregar o código da aplicação (back-end).


CREATE TRIGGER log_atualizacao_avaliacao
AFTER UPDATE ON AVALIACAO
FOR EACH ROW
BEGIN
    -- Só grava log se a nota realmente sofreu alteração
    IF OLD.NOTA <> NEW.NOTA THEN
        INSERT INTO LOG_AVALIACAO (ID_AVALIACAO, NOTA_ANTIGA, NOTA_NOVA)
        VALUES (OLD.ID_AVALIACAO, OLD.NOTA, NEW.NOTA);
    END IF;
END ;


-- A "Nota Média da Comunidade" é uma informação muito acessada e exibida na tela inicial do sistema.
-- Para evitar lentidão calculando essas médias em tempo real (lendo milhares de linhas a cada clique),
-- estes gatilhos atualizam os campos de média das tabelas FILME e SERIE automaticamente a cada nova avaliação inserida.
--  Isso garante alta performance e respostas rápidas para o front-end.


CREATE TRIGGER atualiza_media_filme
AFTER INSERT ON AVALIACAO
FOR EACH ROW
BEGIN
    IF NEW.FK_FILME_ID_MIDIA IS NOT NULL THEN
        UPDATE FILME
        SET AVALIACAO = (
            SELECT AVG(NOTA)
            FROM AVALIACAO
            WHERE FK_FILME_ID_MIDIA = NEW.FK_FILME_ID_MIDIA
        )
        WHERE ID_MIDIA = NEW.FK_FILME_ID_MIDIA;
    END IF;
END ;


CREATE TRIGGER atualiza_media_serie
AFTER INSERT ON AVALIACAO
FOR EACH ROW
BEGIN
    IF NEW.FK_SERIE_ID_MIDIA IS NOT NULL THEN
        UPDATE SERIE
        SET AVALIACAO = (
            SELECT AVG(NOTA)
            FROM AVALIACAO
            WHERE FK_SERIE_ID_MIDIA = NEW.FK_SERIE_ID_MIDIA
        )
        WHERE ID_MIDIA = NEW.FK_SERIE_ID_MIDIA;
    END IF;
END ;

