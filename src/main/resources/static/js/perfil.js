document.addEventListener("DOMContentLoaded", async () => {
    const sessao = API.getSessao();
    if (!sessao || !sessao.id) { window.location.href = 'index.html'; return; }

    try {
        let response;

        if (sessao.admin) {
            response = await API.buscarAdmin(sessao.id);
        } else {
            response = await API.buscarUsuario(sessao.id);
        }

        if (!response.ok) {
            return;
        }

        const usuario = await response.json();

        document.getElementById('userNameLabel').innerText        = usuario.nome;
        document.getElementById('userInitial').innerText          = usuario.nome.charAt(0).toUpperCase();
        document.getElementById('perfilNome').value               = usuario.nome;
        document.getElementById('perfilEmail').value              = usuario.email;
        document.getElementById('perfilTelefone').value           = usuario.telefone;

        if (sessao.admin) {
            document.getElementById('linkAdmin').style.display = 'block';
            const badge = document.getElementById('userBadge');
            badge.innerText = "Administrador";
            badge.classList.add('admin-badge');
        }

        API.setSessao({ ...sessao, nome: usuario.nome, email: usuario.email });

    } catch (e) {
        console.error(e);
    }
});

function habilitarEdicao() {
    document.getElementById('perfilNome').readOnly     = false;
    document.getElementById('perfilTelefone').readOnly = false;
    document.getElementById('divSenha').style.display  = 'block';
    document.getElementById('btnEditar').style.display  = 'none';
    document.getElementById('btnSalvar').style.display  = 'block';
    document.getElementById('btnCancelar').style.display = 'block';
}

async function salvarPerfil(event) {
    event.preventDefault();
    const sessao = API.getSessao();

    const novaSenha = document.getElementById('perfilSenha').value;

    const payload = {
        nome:      document.getElementById('perfilNome').value,
        email:     document.getElementById('perfilEmail').value,
        telefone:  document.getElementById('perfilTelefone').value,
        ...(novaSenha ? { senha: novaSenha } : {})
    };

    try {
        let response;

        if (sessao.admin) {
            response = await API.atualizarAdmin(sessao.id, payload);
        } else {
            response = await API.atualizarUsuario(sessao.id, payload);
        }

        if (response.ok) {
            API.setSessao({ ...sessao, nome: payload.nome, email: payload.email });
            alert("Perfil atualizado com sucesso!");
            location.reload();
        } else {
            alert("Erro ao atualizar perfil.");
        }
    } catch (e) {
        alert("Erro de conexão.");
    }
}