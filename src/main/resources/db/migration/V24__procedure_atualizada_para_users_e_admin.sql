DELIMITER //

DROP PROCEDURE IF EXISTS atualizar_senha_usuario //

CREATE PROCEDURE atualizar_senha_conta(
    IN p_id INT,
    IN p_nova_senha VARCHAR(255),
    IN p_tipo VARCHAR(10)
)
BEGIN
    DECLARE v_senha_atual VARCHAR(255);

    IF p_tipo = 'ADMIN' THEN
        SELECT SENHA INTO v_senha_atual FROM ADMIN WHERE ID_ADMIN = p_id;

        IF v_senha_atual = p_nova_senha THEN
            SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'A nova senha não pode ser igual à senha atual.';
        ELSE
            UPDATE ADMIN SET SENHA = p_nova_senha WHERE ID_ADMIN = p_id;
        END IF;

    ELSE
        SELECT SENHA INTO v_senha_atual FROM USUARIO WHERE ID_USER = p_id;

        IF v_senha_atual = p_nova_senha THEN
            SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'A nova senha não pode ser igual à senha atual.';
        ELSE
            UPDATE USUARIO SET SENHA = p_nova_senha WHERE ID_USER = p_id;
        END IF;
    END IF;

END //
DELIMITER ;