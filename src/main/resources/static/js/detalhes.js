let idAvaliacaoEditando = null;

document.addEventListener("DOMContentLoaded", async () => {
    const usuario = API.getSessao();
    if (!usuario) { window.location.href = 'index.html'; return; }
    if (usuario.admin) {
        document.getElementById('linkAdmin').style.display = 'block';
        document.getElementById('formAvaliacao').style.display = 'none';
    }

    const urlParams = new URLSearchParams(window.location.search);
    const id = urlParams.get('id');
    const tipo = urlParams.get('tipo');

    if (!id || !tipo) {
        document.getElementById('titulo').innerText = "Mídia não encontrada.";
        return;
    }

    tipo === 'filme' ? carregarFilme(id) : carregarSerie(id);
    carregarAvaliacoesDaComunidade(id, tipo);

    document.getElementById('formAvaliacao').addEventListener('submit', async (e) => {
        e.preventDefault();
        if (usuario.admin) return;

        const nota = parseFloat(document.getElementById('notaAvaliacao').value);
        const comentario = document.getElementById('comentarioAvaliacao').value;

        try {
            let res;
            if (idAvaliacaoEditando) {
                res = await API.atualizarAvaliacao(idAvaliacaoEditando, { idUsuario: usuario.id, nota, comentario });
                if (res.ok) {
                    cancelarEdicaoAvaliacao();
                    carregarAvaliacoesDaComunidade(id, tipo);
                    tipo === 'filme' ? carregarFilme(id) : carregarSerie(id);
                } else alert("Erro ao editar avaliação.");
            } else {
                res = await API.criarAvaliacao({ idUsuario: usuario.id, idMidia: parseInt(id), tipo, nota, comentario });
                if (res.ok) {
                    document.getElementById('comentarioAvaliacao').value = '';
                    carregarAvaliacoesDaComunidade(id, tipo);
                    tipo === 'filme' ? carregarFilme(id) : carregarSerie(id);
                } else alert("Erro ao publicar avaliação.");
            }
        } catch (err) {
            alert("Erro de conexão ao salvar avaliação.");
        }
    });
});

async function carregarFilme(id) {
    try {
        const res = await API.buscarFilmePorId(id);
        if (!res.ok) return;
        const filme = await res.json();
        document.title = `${filme.titulo} - CineTracker`;
        document.getElementById('poster').src = filme.posterUrl;
        document.getElementById('titulo').innerText = filme.titulo;
        document.getElementById('descricao').innerText = filme.descricao || 'Sem sinopse disponível.';
        document.getElementById('tags').innerHTML = `
            <span class="detalhes-tag-item">Ano: ${filme.anoLancamento}</span>
            <span class="detalhes-tag-item">Duração: ${filme.duracao} min</span>
            <span class="detalhes-tag-item">Gênero: ${filme.generos || 'Não informado'}</span>
            <span class="detalhes-tag-item" style="background:#e50914;">⭐ IMDb: ${filme.notaImdb}</span>
            <span class="detalhes-tag-item" style="background:#e50914;">🎬 CineTracker: ${Number(filme.avaliacao || 0).toFixed(1)}</span>
        `;
        await buscarEExibirStreamings(filme.idImdb, 'filme');
    } catch (e) { console.error("Erro ao carregar filme:", e); }
}

async function carregarSerie(id) {
    try {
        const res = await API.buscarSeriePorId(id);
        if (!res.ok) return;
        const serie = await res.json();
        document.title = `${serie.titulo} - CineTracker`;
        document.getElementById('poster').src = serie.posterUrl;
        document.getElementById('titulo').innerText = serie.titulo;
        document.getElementById('descricao').innerText = serie.descricao || 'Sem sinopse disponível.';
        document.getElementById('tags').innerHTML = `
            <span class="detalhes-tag-item">Ano: ${serie.anoLancamento}</span>
            <span class="detalhes-tag-item">Temporadas: ${serie.qtdTemporadas}</span>
            <span class="detalhes-tag-item">Gênero: ${serie.generos || 'Não informado'}</span>
            <span class="detalhes-tag-item" style="background:#e50914;">⭐ IMDb: ${serie.notaImdb}</span>
            <span class="detalhes-tag-item" style="background:#e50914;">📺 CineTracker: ${Number(serie.avaliacao || 0).toFixed(1)}</span>
        `;
        carregarEpisodios(id);
        await buscarEExibirStreamings(serie.idImdb, 'serie');
    } catch (e) { console.error("Erro ao carregar série:", e); }
}

async function buscarEExibirStreamings(imdbId, tipo) {
    const section = document.getElementById('streamingSection');
    const container = document.getElementById('listaStreamings');
    section.style.display = 'block';
    container.innerHTML = '<span style="color:#ccc;font-size:14px;">A procurar streamings...</span>';
    try {
        const streamings = await API.getStreaming(imdbId, tipo);
        let plataformas = streamings?.flatrate || [];
        let unicos = Array.from(new Set(plataformas.map(p => p.provider_id)))
                         .map(id => plataformas.find(p => p.provider_id === id));
        const permitidos = ["netflix","amazon prime video","max","hbo max","disney plus","disney+",
            "apple tv plus","apple tv+","globoplay","paramount plus","paramount+",
            "crunchyroll","mubi","mercado play"];
        unicos = unicos.filter(p => permitidos.includes(p.provider_name.toLowerCase()));

        if (unicos.length > 0) {
            container.innerHTML = '';
            unicos.forEach(p => {
                const link = document.createElement('a');
                link.href = streamings.link || '#';
                link.target = '_blank';
                const img = document.createElement('img');
                img.src = `https://image.tmdb.org/t/p/original${p.logo_path}`;
                img.title = p.provider_name;
                img.alt = p.provider_name;
                Object.assign(img.style, { width:'45px', height:'45px', borderRadius:'10px',
                    boxShadow:'0 2px 5px rgba(0,0,0,0.5)', transition:'transform 0.2s', cursor:'pointer' });
                img.onmouseover = () => img.style.transform = 'scale(1.1)';
                img.onmouseout  = () => img.style.transform = 'scale(1)';
                link.appendChild(img);
                container.appendChild(link);
            });
        } else {
            container.innerHTML = '<span style="color:#888;font-size:15px;font-style:italic;">Não disponível em assinaturas conhecidas no momento.</span>';
        }
    } catch (e) {
        container.innerHTML = '<span style="color:#888;font-size:15px;font-style:italic;">Não disponível em assinaturas conhecidas no momento.</span>';
    }
}

async function carregarEpisodios(idSerie) {
    const area = document.getElementById('areaEpisodios');
    try {
        const res = await API.listarEpisodiosDaSerie(idSerie);
        if (!res.ok) return;
        const episodios = await res.json();
        if (episodios.length === 0) {
            area.innerHTML = '<p style="color:#aaa;">Nenhum episódio cadastrado.</p>';
            return;
        }

        // Agrupa por temporada
        const temporadasMap = {};
        episodios.forEach(ep => {
            if (!temporadasMap[ep.numTemporada]) temporadasMap[ep.numTemporada] = [];
            temporadasMap[ep.numTemporada].push(ep);
        });
        const temporadas = Object.keys(temporadasMap).sort((a, b) => a - b);
        const totalEps = episodios.length;

        // Monta o HTML do painel
        const abas = temporadas.map((t, i) =>
            `<button class="btn-temporada${i === 0 ? ' ativa' : ''}" data-temp="${t}" onclick="selecionarTemporada('${t}', this)">
                Temporada ${t}
            </button>`
        ).join('');

        area.innerHTML = `
            <div class="episodios-wrapper">
                <button class="episodios-toggle-btn" id="btnToggleEps" onclick="toggleEpisodios()">
                    <span>Episódios</span>
                    <span class="ep-count">${totalEps} episódio${totalEps !== 1 ? 's' : ''} · ${temporadas.length} temporada${temporadas.length !== 1 ? 's' : ''}</span>
                    <span class="ep-chevron">▾</span>
                </button>
                <div class="episodios-painel" id="painelEpisodios">
                    <div class="temporadas-nav" id="temporadasNav">${abas}</div>
                    <div class="episodios-lista" id="listaEps"></div>
                </div>
            </div>
        `;

        // Guarda os dados para uso nas abas
        window._temporadasData = temporadasMap;
        renderizarEpisodios(temporadas[0]);

    } catch (e) {
        area.innerHTML = '<p style="color:#e50914;">Erro ao buscar episódios.</p>';
    }
}

window.toggleEpisodios = function() {
    const btn   = document.getElementById('btnToggleEps');
    const painel = document.getElementById('painelEpisodios');
    const aberto = painel.classList.toggle('aberto');
    btn.classList.toggle('aberto', aberto);
};

window.selecionarTemporada = function(numTemporada, btnClicado) {
    document.querySelectorAll('.btn-temporada').forEach(b => b.classList.remove('ativa'));
    btnClicado.classList.add('ativa');
    renderizarEpisodios(numTemporada);
};

function renderizarEpisodios(numTemporada) {
    const lista = document.getElementById('listaEps');
    const eps = (window._temporadasData || {})[numTemporada] || [];
    lista.innerHTML = eps.map(ep => `
        <div class="ep-item">
            <div class="ep-num">${ep.numEpisodio}</div>
            <div class="ep-body">
                <div class="ep-titulo">${ep.titulo}</div>
                <div class="ep-meta">${ep.duracao} min</div>
                <div class="ep-desc">${ep.descricao || 'Sem sinopse.'}</div>
            </div>
        </div>
    `).join('');
}

async function carregarAvaliacoesDaComunidade(id, tipo) {
    const usuarioLogado = API.getSessao();
    const container = document.getElementById('listaAvaliacoesRender');
    try {
        const res = tipo === 'filme'
            ? await API.listarAvaliacoesFilme(id, usuarioLogado?.id)
            : await API.listarAvaliacoesSerie(id, usuarioLogado?.id);
        if (!res.ok) return;
        const avaliacoes = await res.json();

        if (avaliacoes.length === 0) {
            container.innerHTML = '<p style="color:#aaa;">Nenhuma avaliação ainda.</p>';
            return;
        }

        container.innerHTML = avaliacoes.map(a => {
            const dataFormatada = new Date(a.data).toLocaleDateString('pt-BR');
            const ehDono = usuarioLogado.id === a.idUsuario;
            const ehAdmin = usuarioLogado.admin;

            // Admin: só excluir. Dono: editar + excluir. Outros: nada.
            let botoesAcao = '';
            if (ehAdmin) {
                botoesAcao = `
                    <div style="margin-top:10px;">
                        <button type="button" class="btn-small btn-danger" onclick="excluirAvaliacaoDaTela(${a.idAvaliacao})">Excluir</button>
                    </div>`;
            } else if (ehDono) {
                botoesAcao = `
                    <div style="margin-top:10px;display:flex;gap:10px;">
                        <button type="button" class="btn-small btn-edit" onclick="prepararEdicaoAvaliacao(${a.idAvaliacao}, ${a.nota}, '${a.comentario.replace(/'/g, "\\'")}')">Editar</button>
                        <button type="button" class="btn-small btn-danger" onclick="excluirAvaliacaoDaTela(${a.idAvaliacao})">Excluir</button>
                    </div>`;
            }

            // Input de comentário: admin vê a seção mas não pode enviar
            const inputDisabled = ehAdmin ? 'disabled style="opacity:0.4;cursor:not-allowed;"' : '';
            const btnDisabled   = ehAdmin ? 'disabled style="opacity:0.4;cursor:not-allowed;"' : '';

            return `
                <div style="background:#222;padding:15px;border-radius:8px;border-left:3px solid ${ehDono ? '#4caf50' : '#e50914'};">
                    <div style="display:flex;justify-content:space-between;margin-bottom:8px;">
                        <strong style="color:#fff;">${a.avaliador}${ehDono ? ' (Você)' : ''}</strong>
                        <span style="color:#888;font-size:13px;">${dataFormatada}</span>
                    </div>
                    <div style="color:#e50914;font-weight:bold;margin-bottom:8px;">${a.nota} ⭐</div>
                    <p style="color:#ccc;font-size:15px;">${a.comentario}</p>
                    <div style="margin-top:12px;border-top:1px solid #333;padding-top:10px;">
                        <button onclick="toggleCurtir(${a.idAvaliacao}, this)" class="btn-interacao ${a.curtidoPeloUsuario ? 'curtido' : ''}">♥ <span class="like-count">${a.qtdLikes || 0}</span></button>
                        <button id="btn-comentar-${a.idAvaliacao}" onclick="toggleSecaoComentarios(${a.idAvaliacao})" class="btn-interacao">💬 Comentar</button>
                    </div>
                    <div id="comentarios-avaliacao-${a.idAvaliacao}" class="comentarios-container" style="display:none;">
                        <div id="lista-respostas-${a.idAvaliacao}">Carregando...</div>
                        <div class="form-comentario-reply">
                            <input type="text" id="input-resposta-${a.idAvaliacao}" placeholder="Escreva uma resposta..." ${inputDisabled} />
                            <button onclick="enviarRespostaAvaliacao(${a.idAvaliacao})" ${btnDisabled}>Enviar</button>
                        </div>
                    </div>
                    ${botoesAcao}
                </div>`;
        }).join('');

        avaliacoes.forEach(a => carregarRespostasAvaliacao(a.idAvaliacao, true));
    } catch (e) { console.error("Erro ao carregar avaliações", e); }
}

window.carregarRespostasAvaliacao = async function(idAvaliacao, apenasContagem = false) {
    const lista     = document.getElementById(`lista-respostas-${idAvaliacao}`);
    const btnComentar = document.getElementById(`btn-comentar-${idAvaliacao}`);
    const usuarioLogado = API.getSessao();
    try {
        const res = await API.listarComentariosAvaliacao(idAvaliacao);
        if (!res.ok) return;
        const respostas = await res.json();
        if (btnComentar) btnComentar.innerText = `💬 Comentar (${respostas.length})`;
        if (apenasContagem) return;

        if (respostas.length === 0) {
            lista.innerHTML = '<p style="color:#666;font-size:12px;">Nenhuma resposta.</p>';
            return;
        }

        lista.innerHTML = respostas.map(r => {
            const ehDono  = usuarioLogado?.id === r.idUsuario;
            const ehAdmin = usuarioLogado?.admin;
            let btns = '';
            // Editar: só o dono (nunca admin)
            if (ehDono && !ehAdmin) {
                btns += `<button onclick="habilitarEdicaoInline(${r.idComentario}, ${idAvaliacao}, '${r.texto.replace(/'/g, "\\'")}')"
                    style="background:none;border:none;color:#888;cursor:pointer;" title="Editar">
                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none"
                        stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <path d="M12 20h9"/><path d="M16.5 3.5a2.121 2.121 0 0 1 3 3L7 19l-4 1 1-4L16.5 3.5z"/>
                    </svg></button>`;
            }
            // Excluir: dono ou admin
            if (ehDono || ehAdmin) {
                btns += `<button onclick="excluirComentarioDaLista(${r.idComentario}, ${idAvaliacao})"
                    style="background:none;border:none;color:#e50914;cursor:pointer;" title="Excluir">
                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none"
                        stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <polyline points="3 6 5 6 21 6"/>
                        <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"/>
                    </svg></button>`;
            }
            return `
                <div class="comentario-reply-item" style="display:flex;justify-content:space-between;align-items:flex-start;gap:10px;margin-bottom:8px;">
                    <div id="corpo-comentario-${r.idComentario}" style="flex-grow:1;min-width:0;">
                        <strong>${r.nomeUsuario}</strong>: <span>${r.texto}</span>
                    </div>
                    <div id="acoes-comentario-${r.idComentario}" style="display:flex;gap:10px;flex-shrink:0;">${btns}</div>
                </div>`;
        }).join('');
    } catch (e) { if (!apenasContagem) lista.innerHTML = '<p style="color:#e50914;font-size:12px;">Erro.</p>'; }
};

window.habilitarEdicaoInline = function(idComentario, idAvaliacao, textoAtual) {
    const corpo  = document.getElementById(`corpo-comentario-${idComentario}`);
    const acoes  = document.getElementById(`acoes-comentario-${idComentario}`);
    if (acoes) acoes.style.display = 'none';
    corpo.innerHTML = `
        <div style="display:flex;flex-direction:column;gap:6px;width:100%;">
            <input type="text" id="input-edit-${idComentario}" value="${textoAtual.replace(/"/g, '&quot;')}"
                style="width:100%;padding:6px;border:1px solid #444;border-radius:4px;background:#1f1f1f;color:#fff;" />
            <div style="display:flex;gap:8px;justify-content:flex-end;">
                <button onclick="salvarEdicaoInline(${idComentario}, ${idAvaliacao})"
                    style="background:#007bff;color:white;border:none;padding:4px 10px;border-radius:4px;cursor:pointer;">Salvar</button>
                <button onclick="carregarRespostasAvaliacao(${idAvaliacao})"
                    style="background:#555;color:white;border:none;padding:4px 10px;border-radius:4px;cursor:pointer;">Cancelar</button>
            </div>
        </div>`;
    document.getElementById(`input-edit-${idComentario}`).focus();
};

window.salvarEdicaoInline = async function(idComentario, idAvaliacao) {
    const usuarioLogado = API.getSessao();
    const novoTexto = document.getElementById(`input-edit-${idComentario}`).value.trim();
    if (!novoTexto) return;
    const res = await API.atualizarComentarioResp(idComentario, { idUsuario: usuarioLogado.id, texto: novoTexto, admin: usuarioLogado.admin });
    if (res.ok) await carregarRespostasAvaliacao(idAvaliacao);
    else alert("Erro ao editar.");
};

window.excluirComentarioDaLista = async function(idComentario, idAvaliacao) {
    if (!confirm("Tem certeza que deseja excluir?")) return;
    const usuarioLogado = API.getSessao();
    const res = await API.deletarComentarioResp(idComentario, usuarioLogado.id, usuarioLogado.admin);
    if (res.ok) await carregarRespostasAvaliacao(idAvaliacao);
    else alert("Erro ao excluir.");
};

window.toggleCurtir = async function(idAvaliacao, btn) {
    const usuarioLogado = API.getSessao();
    if (!usuarioLogado) { alert("Faça login!"); return; }
    if (usuarioLogado.admin) return; // Admin não pode curtir
    const res = await API.curtirAvaliacao(idAvaliacao, usuarioLogado.id);
    if (res.ok) {
        const result = await res.json();
        const span = btn.querySelector('.like-count');
        span.innerText = result.curtido ? parseInt(span.innerText) + 1 : Math.max(0, parseInt(span.innerText) - 1);
        btn.classList.toggle('curtido', result.curtido);
    }
};

window.toggleSecaoComentarios = function(idAvaliacao) {
    const container = document.getElementById(`comentarios-avaliacao-${idAvaliacao}`);
    const aberto = container.style.display !== 'none';
    container.style.display = aberto ? 'none' : 'block';
    if (!aberto) carregarRespostasAvaliacao(idAvaliacao);
};

window.enviarRespostaAvaliacao = async function(idAvaliacao) {
    const usuarioLogado = API.getSessao();
    if (usuarioLogado.admin) return;
    const input = document.getElementById(`input-resposta-${idAvaliacao}`);
    const texto = input.value.trim();
    if (!texto) return;
    const res = await API.comentarAvaliacao(idAvaliacao, usuarioLogado.id, texto);
    if (res.ok) { input.value = ''; await carregarRespostasAvaliacao(idAvaliacao); }
    else alert("Erro ao enviar.");
};

function prepararEdicaoAvaliacao(id, notaAtual, comentarioAtual) {
    idAvaliacaoEditando = id;
    document.getElementById('tituloFormAvaliacao').innerText = 'Editando sua avaliação';
    document.getElementById('notaAvaliacao').value = notaAtual;
    document.getElementById('comentarioAvaliacao').value = comentarioAtual;
    document.getElementById('btnSubmitAvaliacao').innerText = 'Salvar Alterações';
    document.getElementById('btnCancelarEdicao').style.display = 'inline-block';
    document.getElementById('areaAvaliacoes').scrollIntoView({ behavior: 'smooth' });
}

function cancelarEdicaoAvaliacao() {
    idAvaliacaoEditando = null;
    document.getElementById('formAvaliacao').reset();
    document.getElementById('tituloFormAvaliacao').innerText = 'Deixe sua avaliação';
    document.getElementById('btnSubmitAvaliacao').innerText = 'Publicar Avaliação';
    document.getElementById('btnCancelarEdicao').style.display = 'none';
}

async function excluirAvaliacaoDaTela(idAvaliacao) {
    if (!confirm("Deseja realmente apagar?")) return;
    const usuarioLogado = API.getSessao();

    // Admin usa rota própria que não verifica dono da avaliação
    const res = usuarioLogado.admin
        ? await API.deletarComentarioAdmin(idAvaliacao)
        : await API.deletarAvaliacao(idAvaliacao, usuarioLogado.id);

    if (res.ok) {
        const urlParams = new URLSearchParams(window.location.search);
        carregarAvaliacoesDaComunidade(urlParams.get('id'), urlParams.get('tipo'));
        window.location.reload();
    } else {
        alert("Erro ao excluir avaliação.");
    }
}