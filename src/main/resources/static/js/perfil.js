document.addEventListener("DOMContentLoaded", async () => {
    const sessao = API.getSessao();
    if (!sessao || !sessao.id) { window.location.href = 'index.html'; return; }

    try {
        let response;

        if (sessao.admin) {
            response = await API.buscarAdmin(sessao.id);
            document.getElementById('area-estatisticas').style.display = 'none';
            document.getElementById('area-relatorio').style.display = 'none';
            document.getElementById('area-listas').style.display = 'none';
        } else {
            response = await API.buscarUsuario(sessao.id);
        }

        if (!response.ok) {
            console.error('[Perfil] Erro ao buscar usuario:', response.status);
            return;
        }

        const usuario = await response.json();

        document.getElementById('userNameLabel').innerText  = usuario.nome;
        document.getElementById('userInitial').innerText    = usuario.nome.charAt(0).toUpperCase();
        document.getElementById('perfilNome').value         = usuario.nome;
        document.getElementById('perfilEmail').value        = usuario.email;
        document.getElementById('perfilTelefone').value     = usuario.telefone;

        if (sessao.admin) {
            document.getElementById('linkAdmin').style.display = 'block';
            const badge = document.getElementById('userBadge');
            badge.innerText = "Administrador";
            badge.classList.add('admin-badge');
        }

        API.setSessao({ ...sessao, nome: usuario.nome, email: usuario.email });

        // Carrega estatísticas e listas SÓ depois que o usuário foi carregado com sucesso
        if (!sessao.admin) {
            API.buscarEstatisticasUsuario(sessao.id).then(res => {
                if (res.ok) res.json().then(n => {
                    document.getElementById('contador-avaliacoes').innerText = n;
                });
            }).catch(() => {});

            await carregarListas();
        }

    } catch (e) {
        console.error('[Perfil] Excecao:', e);
    }
});

function habilitarEdicao() {
    document.getElementById('perfilNome').readOnly      = false;
    document.getElementById('perfilTelefone').readOnly  = false;
    document.getElementById('divSenha').style.display   = 'block';
    document.getElementById('btnEditar').style.display  = 'none';
    document.getElementById('btnSalvar').style.display  = 'block';
    document.getElementById('btnCancelar').style.display = 'block';
}

async function salvarPerfil(event) {
    event.preventDefault();
    const sessao = API.getSessao();
    const novaSenha = document.getElementById('perfilSenha').value;

    const payload = {
        nome:     document.getElementById('perfilNome').value,
        email:    document.getElementById('perfilEmail').value,
        telefone: document.getElementById('perfilTelefone').value,
        ...(novaSenha ? { senha: novaSenha } : {})
    };

    try {
        const response = sessao.admin
            ? await API.atualizarAdmin(sessao.id, payload)
            : await API.atualizarUsuario(sessao.id, payload);

        if (response.ok) {
            API.setSessao({ ...sessao, nome: payload.nome, email: payload.email });
            alert("Perfil atualizado com sucesso!");
            location.reload();
        } else {
            const errorText = await response.text();
            if (errorText.includes("A nova senha não pode ser igual à senha atual")) {
                alert("Atenção: A nova senha não pode ser igual à senha atual.");
            } else {
                alert("Erro ao atualizar perfil. Verifique os dados e tente novamente.");
            }
        }
    } catch (e) {
        console.error('[salvarPerfil] Erro:', e);
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

async function carregarListas() {
    const sessao = API.getSessao();
    const container = document.getElementById('container-listas');
    try {
        const res = await API.listarListasDoUsuario(sessao.id);
        if (!res.ok) {
            const msg = await res.text().catch(() => `HTTP ${res.status}`);
            console.error('[Listas] Erro HTTP:', res.status, msg);
            container.innerHTML = `<p style="color:#555;font-size:14px;">Erro ao carregar listas (${res.status}).</p>`;
            return;
        }
        const listas = await res.json();
        renderizarListas(listas);
    } catch (e) {
        console.error('[Listas] Excecao de rede:', e);
        container.innerHTML = '<p style="color:#555;font-size:14px;">Erro de conexão com o servidor.</p>';
    }
}

function renderizarListas(listas) {
    const container = document.getElementById('container-listas');
    if (listas.length === 0) {
        container.innerHTML = '<p style="color:#555;font-size:14px;">Você ainda não criou nenhuma lista.</p>';
        return;
    }
    container.innerHTML = listas.map(l => `
        <div class="lista-card" id="lista-card-${l.idLista}">
            <div class="lista-card-header" onclick="toggleItensLista(${l.idLista})">
                <div>
                    <div class="lista-card-title">📋 ${escHtml(l.nomeLista)}</div>
                    <div class="lista-card-meta">🎬 Filmes &amp; 📺 Séries</div>
                </div>
                <div class="lista-card-actions" onclick="event.stopPropagation()">
                    <button class="btn-icon" onclick="editarLista(${l.idLista}, '${escHtml(l.nomeLista)}')">Editar</button>
                    <button class="btn-icon danger" onclick="confirmarExcluirLista(${l.idLista})">Excluir</button>
                </div>
            </div>
            <div class="lista-itens" id="itens-lista-${l.idLista}">
                <p style="color:#555;font-size:13px;padding:10px 0;">Clique para expandir e ver os itens.</p>
            </div>
        </div>
    `).join('');
}

async function toggleItensLista(idLista) {
    const sessao = API.getSessao();
    const div = document.getElementById(`itens-lista-${idLista}`);
    const aberta = div.classList.contains('aberta');
    if (aberta) { div.classList.remove('aberta'); return; }

    div.innerHTML = '<p style="color:#555;font-size:13px;padding:10px 0;">Carregando...</p>';
    div.classList.add('aberta');

    try {
        const res = await API.listarItensLista(idLista, sessao.id);
        if (!res.ok) { div.innerHTML = '<p class="sem-itens">Erro ao carregar itens.</p>'; return; }
        const itens = await res.json();

        if (itens.length > 0) console.log('[Itens] Exemplo:', JSON.stringify(itens[0]));

        if (itens.length === 0) {
            div.innerHTML = '<p class="sem-itens">Nenhum item nesta lista ainda.</p>';
        } else {
            div.innerHTML = itens.map(item => {
                const idContem  = item.ID_CONTEM ?? item.id_contem  ?? item.idContem;
                const idMidia   = item.IDMIDIA   ?? item.idMidia    ?? item.ID_MIDIA  ?? item.idmidia;
                const tipo      = item.tipo      ?? item.TIPO;
                const titulo    = item.titulo    ?? item.TITULO;
                const tipoParam = tipo === 'FILME' ? 'filme' : 'serie';
                const href      = idMidia ? `detalhes.html?id=${idMidia}&tipo=${tipoParam}` : '#';

                return `
                <div class="item-linha">
                    <a href="${href}" class="item-titulo-link">
                        <span class="item-tipo">${tipo === 'FILME' ? '🎬' : '📺'}</span>
                        ${escHtml(titulo || '(sem título)')}
                    </a>
                    <button class="btn-icon danger" onclick="removerItemDaLista(${idLista}, ${idContem})">Remover</button>
                </div>`;
            }).join('');
        }
    } catch (e) {
        console.error('[toggleItensLista] Erro:', e);
        div.innerHTML = '<p class="sem-itens">Erro ao carregar itens.</p>';
    }
}

async function criarNovaLista() {
    const sessao = API.getSessao();
    const nome = document.getElementById('novaListaNome').value.trim();
    if (!nome) { alert('Informe um nome para a lista.'); return; }

    try {
        const res = await API.criarLista({ nomeLista: nome, idUsuario: sessao.id });
        if (res.ok) {
            document.getElementById('novaListaNome').value = '';
            await carregarListas();
        } else {
            alert('Erro ao criar lista: ' + await res.text());
        }
    } catch (e) {
        alert('Erro de conexão com o servidor.');
    }
}

async function editarLista(idLista, nomeAtual) {
    const sessao = API.getSessao();
    const novoNome = prompt('Novo nome da lista:', nomeAtual);
    if (novoNome === null) return;
    if (!novoNome.trim()) { alert('O nome não pode ser vazio.'); return; }

    try {
        const res = await API.renomearLista(idLista, { nomeLista: novoNome.trim(), idUsuario: sessao.id });
        if (res.ok) {
            await carregarListas();
        } else {
            alert('Erro ao editar lista: ' + await res.text());
        }
    } catch (e) {
        alert('Erro de conexão com o servidor.');
    }
}

async function confirmarExcluirLista(idLista) {
    const sessao = API.getSessao();
    if (!confirm('Tem certeza que deseja excluir esta lista? Todos os itens serão removidos.')) return;
    try {
        const res = await API.deletarLista(idLista, sessao.id);
        if (res.ok) {
            await carregarListas();
        } else {
            alert('Erro ao excluir lista: ' + await res.text());
        }
    } catch (e) {
        alert('Erro de conexão com o servidor.');
    }
}

async function removerItemDaLista(idLista, idContem) {
    const sessao = API.getSessao();
    if (!confirm('Remover este item da lista?')) return;
    try {
        const res = await API.removerItemLista(idLista, idContem, sessao.id);
        if (res.ok) {
            const div = document.getElementById(`itens-lista-${idLista}`);
            div.classList.remove('aberta');
            await toggleItensLista(idLista);
        } else {
            alert('Erro ao remover item.');
        }
    } catch (e) {
        alert('Erro de conexão com o servidor.');
    }
}

function escHtml(str) {
    if (!str) return '';
    return str.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;').replace(/"/g, '&quot;');
}