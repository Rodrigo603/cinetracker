document.addEventListener("DOMContentLoaded", async () => {
    const sessao = API.getSessao();
    if (!sessao || !sessao.id) { window.location.href = 'index.html'; return; }

    try {
        let response;

        if (sessao.admin) {
            response = await API.buscarAdmin(sessao.id);
            document.getElementById('area-estatisticas').style.display = 'none';
            document.getElementById('area-relatorio').style.display = 'none';
        } else {
            response = await API.buscarUsuario(sessao.id);

            const resEstatistica = await API.buscarEstatisticasUsuario(sessao.id);
            if (resEstatistica.ok) {
                document.getElementById('contador-avaliacoes').innerText = await resEstatistica.json();
            }
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
            // Se a resposta não for OK, lê o corpo do erro retornado pelo back-end
            const errorText = await response.text();

            // Verifica se o erro gerado pela Procedure do banco de dados está presente no texto
            if (errorText.includes("A nova senha não pode ser igual à senha atual")) {
                alert("Atenção: A nova senha não pode ser igual à senha atual.");
            } else {
                alert("Erro ao atualizar perfil. Verifique os dados e tente novamente.");
            }
        }
    } catch (e) {
        alert("Erro de conexão com o servidor.");
    }
}

async function carregarRelatorioHistorico() {
    const sessao = API.getSessao();
    try {
        const res = await API.gerarRelatorioUsuario(sessao.id);
        if (res.ok) {
            const texto = await res.text();
            const pre = document.getElementById('texto-relatorio');
            pre.innerText = texto;
            pre.style.display = 'block';
        }
    } catch (e) { alert("Erro ao gerar relatório"); }
}