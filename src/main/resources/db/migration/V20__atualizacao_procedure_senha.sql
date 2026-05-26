DELIMITER //
DROP PROCEDURE IF EXISTS atualizar_senha_usuario //

CREATE PROCEDURE atualizar_senha_usuario(
    IN p_id_user INT,
    IN p_nova_senha VARCHAR(255)
)
BEGIN
    DECLARE v_senha_atual VARCHAR(255);

    SELECT SENHA INTO v_senha_atual
    FROM USUARIO
    WHERE ID_USER = p_id_user;

    IF v_senha_atual = p_nova_senha THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'A nova senha não pode ser igual à senha atual.';
    ELSE
        UPDATE USUARIO
        SET SENHA = p_nova_senha
        WHERE ID_USER = p_id_user;
    END IF;

END //

DELIMITER ;