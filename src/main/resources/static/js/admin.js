let filmesCatalogo = [];
let seriesCatalogo = [];
let tipoMidiaSelecionado = 'filme';

document.addEventListener("DOMContentLoaded", () => {
    const u = API.getSessao();
    if (!u || !u.admin) { window.location.href = 'home.html'; return; }
    carregarFilmes();
    carregarSeries();
    carregarUsuarios();
});

function irParaSecao(idSecao, btnClicado) {
    document.querySelectorAll('.tab-btn').forEach(b => b.classList.remove('active'));
    btnClicado.classList.add('active');
    document.getElementById(idSecao).scrollIntoView({ behavior: 'smooth', block: 'start' });
}

function mostrarToast(msg, tipo = 'success') {
    const t = document.getElementById('toast');
    t.textContent = msg;
    t.className = 'show ' + tipo;
    clearTimeout(t._timer);
    t._timer = setTimeout(() => { t.className = ''; }, 3500);
}

async function carregarFilmes() {
    const res = await API.listarFilmes();
    if (!res.ok) return;
    filmesCatalogo = await res.json();
    renderizarTabelaFilmes(filmesCatalogo);
}

function renderizarTabelaFilmes(lista) {
    const tbody    = document.getElementById('listaFilmesTbody');
    const msgVazia = document.getElementById('msgBuscaVazia');
    const countEl  = document.getElementById('count-filmes');
    tbody.innerHTML = '';
    countEl.textContent = lista.length + ' filme' + (lista.length !== 1 ? 's' : '');

    if (lista.length === 0) { msgVazia.style.display = 'block'; return; }
    msgVazia.style.display = 'none';

    lista.forEach(f => {
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td>${f.idMidia}</td>
            <td>${f.titulo}</td>
            <td>
                <div class="action-buttons">
                    <button class="btn-small btn-edit"   onclick="prepararEdicao(${f.idMidia})">Editar</button>
                    <button class="btn-small btn-danger" onclick="deletarFilme(${f.idMidia})">Excluir</button>
                </div>
            </td>`;
        tbody.appendChild(tr);
    });
}

function filtrarFilmes() {
    const termo = document.getElementById('inputBuscaFilme').value.toLowerCase();
    renderizarTabelaFilmes(filmesCatalogo.filter(f => f.titulo.toLowerCase().includes(termo)));
}

async function carregarSeries() {
    try {
        const res = await API.listarSeries();
        if (!res.ok) {
            mostrarToast('Erro ao carregar séries do servidor.', 'error');
            return;
        }
        seriesCatalogo = await res.json();
        renderizarTabelaSeries(seriesCatalogo);
    } catch (err) {
        mostrarToast('Falha de comunicação ao carregar séries.', 'error');
    }
}

function renderizarTabelaSeries(lista) {
    const tbody    = document.getElementById('listaSeriesTbody');
    const msgVazia = document.getElementById('msgBuscaSerieVazia');
    const countEl  = document.getElementById('count-series');
    tbody.innerHTML = '';
    countEl.textContent = lista.length + ' série' + (lista.length !== 1 ? 's' : '');

    if (lista.length === 0) { msgVazia.style.display = 'block'; return; }
    msgVazia.style.display = 'none';

    lista.forEach(s => {
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td>${s.idMidia}</td>
            <td>${s.titulo}</td>
            <td style="color:#888">${s.qtdTemporadas ?? '—'}</td>
            <td>
                <div class="action-buttons">
                    <button class="btn-small btn-edit"   onclick="prepararEdicaoSerie(${s.idMidia})">Editar</button>
                    <button class="btn-small btn-danger" onclick="deletarSerie(${s.idMidia})">Excluir</button>
                </div>
            </td>`;
        tbody.appendChild(tr);
    });
}

function filtrarSeries() {
    const termo = document.getElementById('inputBuscaSerie').value.toLowerCase();
    renderizarTabelaSeries(seriesCatalogo.filter(s => s.titulo.toLowerCase().includes(termo)));
}

function selecionarTipo(tipo) {
    tipoMidiaSelecionado = tipo;

    const btnFilme = document.getElementById('btn-tipo-filme');
    const btnSerie = document.getElementById('btn-tipo-serie');

    if (tipo === 'filme') {
        btnFilme.classList.add('active');
        btnSerie.classList.remove('active');
    } else {
        btnSerie.classList.add('active');
        btnFilme.classList.remove('active');
    }

    const labelBusca  = document.getElementById('label-busca');
    const placeholder = document.getElementById('tituloMedia');
    if (tipo === 'serie') {
        labelBusca.textContent  = 'Qual série pretende adicionar? (Busca automática no OMDb)';
        placeholder.placeholder = 'Ex: Breaking Bad';
    } else {
        labelBusca.textContent  = 'Qual filme pretende adicionar? (Busca automática no OMDb)';
        placeholder.placeholder = 'Ex: The Matrix';
    }
}

async function adicionarMidia(event) {
    event.preventDefault();
    const titulo = document.getElementById('tituloMedia').value.trim();
    if (!titulo) return;

    const btn = document.getElementById('btn-salvar-midia');
    btn.textContent = 'Buscando na OMDb…';
    btn.disabled = true;

    try {
        const res   = tipoMidiaSelecionado === 'serie'
            ? await API.criarSerie({ titulo })
            : await API.criarFilme({ titulo });
        const texto = await res.text();
        if (res.ok) {
            mostrarToast(texto, 'success');
            document.getElementById('formMidia').reset();
            carregarFilmes();
            carregarSeries();
        } else {
            mostrarToast(texto || 'Erro ao adicionar mídia.', 'error');
        }
    } catch (err) {
        mostrarToast('Erro de comunicação com o servidor.', 'error');
    } finally {
        btn.textContent = 'Adicionar ao Catálogo';
        btn.disabled = false;
    }
}

function prepararEdicao(id) {
    const filme = filmesCatalogo.find(f => f.idMidia === id);
    if (!filme) return;

    document.getElementById('editIdMidia').value    = filme.idMidia;
    document.getElementById('editTitulo').value     = filme.titulo;
    document.getElementById('editDescricao').value  = filme.descricao || '';
    document.getElementById('editPoster').value     = filme.posterUrl || '';
    document.getElementById('editDuracao').value    = filme.duracao || 1;
    document.getElementById('editBilheteria').value = filme.bilheteria || 0;

    document.getElementById('form-midia-container').classList.add('hidden');
    const ed = document.getElementById('form-editar-container');
    ed.classList.remove('hidden');
    ed.classList.add('editing-mode');
    ed.scrollIntoView({ behavior: 'smooth', block: 'center' });
    setTimeout(() => ed.classList.remove('editing-mode'), 2000);
}

async function salvarEdicao(event) {
    event.preventDefault();
    const id = document.getElementById('editIdMidia').value;
    const payload = {
        titulo:     document.getElementById('editTitulo').value,
        descricao:  document.getElementById('editDescricao').value,
        posterUrl:  document.getElementById('editPoster').value,
        duracao:    parseInt(document.getElementById('editDuracao').value) || 1,
        bilheteria: parseFloat(document.getElementById('editBilheteria').value) || 0
    };
    try {
        const res = await API.atualizarFilme(id, payload);
        if (res.ok) {
            mostrarToast('Filme atualizado com sucesso!', 'success');
            cancelarEdicao();
            carregarFilmes();
        } else {
            mostrarToast('Erro ao atualizar o filme.', 'error');
        }
    } catch (err) {
        mostrarToast('Erro de comunicação com o servidor.', 'error');
    }
}

function cancelarEdicao() {
    document.getElementById('formEditarMidia').reset();
    document.getElementById('form-editar-container').classList.add('hidden');
    document.getElementById('form-midia-container').classList.remove('hidden');
}

function prepararEdicaoSerie(id) {
    const serie = seriesCatalogo.find(s => s.idMidia === id);
    if (!serie) return;

    document.getElementById('editSerieId').value         = serie.idMidia;
    document.getElementById('editSerieTitulo').value     = serie.titulo;
    document.getElementById('editSerieDescricao').value  = serie.descricao || '';
    document.getElementById('editSeriePoster').value     = serie.posterUrl || '';
    document.getElementById('editSerieTemporadas').value = serie.qtdTemporadas || 1;
    document.getElementById('editSerieAno').value        = serie.anoLancamento || '';

    document.getElementById('form-midia-container').classList.add('hidden');
    const ed = document.getElementById('form-editar-serie-container');
    ed.classList.remove('hidden');
    ed.classList.add('editing-mode');
    ed.scrollIntoView({ behavior: 'smooth', block: 'center' });
    setTimeout(() => ed.classList.remove('editing-mode'), 2000);
}

async function salvarEdicaoSerie(event) {
    event.preventDefault();
    const id = document.getElementById('editSerieId').value;
    const payload = {
        titulo:         document.getElementById('editSerieTitulo').value,
        descricao:      document.getElementById('editSerieDescricao').value,
        posterUrl:      document.getElementById('editSeriePoster').value,
        qtdTemporadas:  parseInt(document.getElementById('editSerieTemporadas').value) || 1,
        anoLancamento:  parseInt(document.getElementById('editSerieAno').value) || null
    };
    try {
        const res = await API.atualizarSerie(id, payload);
        if (res.ok) {
            mostrarToast('Série atualizada com sucesso!', 'success');
            cancelarEdicaoSerie();
            carregarSeries();
        } else {
            mostrarToast('Erro ao atualizar a série.', 'error');
        }
    } catch (err) {
        mostrarToast('Erro de comunicação com o servidor.', 'error');
    }
}

function cancelarEdicaoSerie() {
    document.getElementById('formEditarSerie').reset();
    document.getElementById('form-editar-serie-container').classList.add('hidden');
    document.getElementById('form-midia-container').classList.remove('hidden');
}

async function deletarFilme(id) {
    if (!confirm('Excluir este filme do catálogo? Esta ação é irreversível.')) return;
    const res = await API.deletarFilme(id);
    if (res.ok) {
        mostrarToast('Filme removido do catálogo.', 'success');
        carregarFilmes();
    } else {
        mostrarToast('Erro ao remover o filme.', 'error');
    }
}

async function deletarSerie(id) {
    if (!confirm('Excluir esta série do catálogo? Esta ação é irreversível.')) return;
    const res = await API.deletarSerie(id);
    if (res.ok) {
        mostrarToast('Série removida do catálogo.', 'success');
        carregarSeries();
    } else {
        mostrarToast('Erro ao remover a série.', 'error');
    }
}

async function buscarComentarios() {
    const tipo = document.getElementById('tipoComentario').value;
    const id   = document.getElementById('idMidiaComentario').value;
    const container = document.getElementById('resultado-comentarios');

    if (!id) {
        container.innerHTML = `<div class="empty-state">Informe o ID da mídia.</div>`;
        return;
    }

    container.innerHTML = `<div class="empty-state">Carregando comentários…</div>`;

    try {
        const res = tipo === 'filme'
            ? await API.listarAvaliacoesFilme(id)
            : await API.listarAvaliacoesSerie(id);

        if (!res.ok) {
            container.innerHTML = `<div class="empty-state">Erro ao buscar comentários.</div>`;
            return;
        }

        const lista = await res.json();

        if (lista.length === 0) {
            container.innerHTML = `<div class="empty-state">Nenhum comentário encontrado para este ${tipo}.</div>`;
            return;
        }

        container.innerHTML = `
            <p style="font-size:13px;color:#666;margin-bottom:12px;">
                ${lista.length} comentário(s) encontrado(s) para o ${tipo} ID <strong style="color:#e5e5e5">${id}</strong>
            </p>
            <div class="comment-list">
                ${lista.map(c => `
                    <div class="comment-card" id="comment-${c.idAvaliacao}">
                        <div style="flex:1;min-width:0;">
                            <div class="comment-meta">
                                ${c.avaliador || 'Usuário #' + c.idUsuario}
                                &nbsp;·&nbsp; ID avaliação: <strong>${c.idAvaliacao}</strong>
                                &nbsp;·&nbsp; ${c.data ? new Date(c.data).toLocaleDateString('pt-BR') : ''}
                            </div>
                            <div class="comment-text">${c.comentario || '<em style="color:#555">Sem texto</em>'}</div>
                            <div class="comment-nota">Nota: ${c.nota ?? '—'} / 5</div>
                        </div>
                        <div class="comment-actions">
                            <button class="btn-small btn-danger" onclick="deletarComentario(${c.idAvaliacao})">Deletar</button>
                        </div>
                    </div>`).join('')}
            </div>`;
    } catch (err) {
        container.innerHTML = `<div class="empty-state">Erro de comunicação com o servidor.</div>`;
    }
}

async function deletarComentario(idAvaliacao) {
    if (!confirm(`Deletar o comentário ID ${idAvaliacao}? Esta ação é irreversível.`)) return;

    const card = document.getElementById('comment-' + idAvaliacao);
    if (card) card.classList.add('deleting');

    const res = await API.deletarComentarioAdmin(idAvaliacao);
    if (res.ok) {
        mostrarToast('Comentário removido com sucesso.', 'success');
        if (card) card.remove();
    } else {
        mostrarToast('Erro ao remover o comentário.', 'error');
        if (card) card.classList.remove('deleting');
    }
}

async function deletarComentarioDireto() {
    const id = document.getElementById('idComentarioDireto').value;
    if (!id) { mostrarToast('Informe o ID do comentário.', 'error'); return; }
    if (!confirm(`Deletar o comentário ID ${id}? Esta ação é irreversível.`)) return;

    const res   = await API.deletarComentarioAdmin(id);
    const texto = await res.text();
    if (res.ok) {
        mostrarToast(texto, 'success');
        document.getElementById('idComentarioDireto').value = '';
    } else {
        mostrarToast(texto || 'Avaliação não encontrada.', 'error');
    }
}


async function criarNovoAdmin(event) {
    event.preventDefault();
    const payload = {
        nome:     document.getElementById('nomeAdmin').value,
        email:    document.getElementById('emailAdmin').value,
        telefone: document.getElementById('telAdmin').value,
        senha:    document.getElementById('senhaAdmin').value
    };
    const res = await API.criarAdmin(payload);
    if (res.ok) {
        mostrarToast('Novo administrador cadastrado!', 'success');
        event.target.reset();
    } else {
        mostrarToast('Erro ao cadastrar. Verifique os dados.', 'error');
    }
}


async function carregarUsuarios() {
    try {
        const res = await API.listarUsuariosAdmin();
        if (!res.ok) return;
        const usuarios = await res.json();
        const tbody = document.getElementById('listaUsuariosTbody');
        const count = document.getElementById('count-usuarios');
        tbody.innerHTML = '';
        count.textContent = usuarios.length + ' usuário(s)';

        if (usuarios.length === 0) {
            tbody.innerHTML = '<tr><td colspan="4" style="text-align:center;">Nenhum usuário encontrado.</td></tr>';
            return;
        }

        usuarios.forEach(u => {
            const tr = document.createElement('tr');
            tr.innerHTML = `
                <td>${u.id}</td>
                <td>${u.nome}</td>
                <td>${u.email}</td>
                <td>
                    <button class="btn-small btn-danger" onclick="excluirUsuarioDireto(${u.id})">Excluir</button>
                </td>`;
            tbody.appendChild(tr);
        });
    } catch (err) { console.error("Erro ao carregar usuários:", err); }
}

async function excluirUsuarioDireto(id) {
    if (!confirm(`Excluir permanentemente o usuário ID ${id}? Esta ação não tem volta.`)) return;

    const res = await API.excluirUsuario(id);
    if (res.ok) {
        mostrarToast('Usuário removido com sucesso.', 'success');
        carregarUsuarios();
    } else {
        mostrarToast('Erro ao excluir usuário.', 'error');
    }
}