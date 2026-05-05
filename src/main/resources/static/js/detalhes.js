let idAvaliacaoEditando = null;

document.addEventListener("DOMContentLoaded", async () => {
    const usuario = API.getSessao();
    if (!usuario) { window.location.href = 'index.html'; return; }
    if (usuario.admin) { document.getElementById('linkAdmin').style.display = 'block'; }

    const urlParams = new URLSearchParams(window.location.search);
    const id = urlParams.get('id');
    const tipo = urlParams.get('tipo');

    if (!id || !tipo) {
        document.getElementById('titulo').innerText = "Mídia não encontrada.";
        return;
    }

    if (tipo === 'filme') {
        carregarFilme(id);
    } else if (tipo === 'serie') {
        carregarSerie(id);
    }

    carregarAvaliacoesDaComunidade(id, tipo);

    document.getElementById('formAvaliacao').addEventListener('submit', async (e) => {
        e.preventDefault();

        const notaValor = parseFloat(document.getElementById('notaAvaliacao').value);
        const comentarioValor = document.getElementById('comentarioAvaliacao').value;

        try {
            if (idAvaliacaoEditando) {
                const payloadUpdate = { idUsuario: usuario.id, nota: notaValor, comentario: comentarioValor };
                const res = await API.atualizarAvaliacao(idAvaliacaoEditando, payloadUpdate);
                if(res.ok) {
                    cancelarEdicaoAvaliacao();
                    carregarAvaliacoesDaComunidade(id, tipo);
                } else {
                    alert("Erro ao editar avaliação.");
                }
            } else {
                const payloadNew = {
                    idUsuario: usuario.id,
                    idMidia: parseInt(id),
                    tipo: tipo,
                    nota: notaValor,
                    comentario: comentarioValor
                };
                const res = await API.criarAvaliacao(payloadNew);
                if(res.ok) {
                    document.getElementById('comentarioAvaliacao').value = '';
                    carregarAvaliacoesDaComunidade(id, tipo);
                } else {
                    alert("Erro ao publicar avaliação.");
                }
            }
        } catch (err) {
            alert("Erro de conexão ao salvar avaliação.");
        }
    });
});

async function carregarFilme(id) {
    try {
        const res = await API.buscarFilmePorId(id);
        if (res.ok) {
            const filme = await res.json();
            document.title = `${filme.titulo} - CineTracker`;
            document.getElementById('poster').src = filme.posterUrl;
            document.getElementById('titulo').innerText = filme.titulo;
            document.getElementById('descricao').innerText = filme.descricao || 'Sem sinopse disponível.';

            document.getElementById('tags').innerHTML = `
                <span class="detalhes-tag-item">Ano: ${filme.anoLancamento}</span>
                <span class="detalhes-tag-item">Duração: ${filme.duracao} min</span>
                <span class="detalhes-tag-item" style="background:#e50914;">⭐ IMDb: ${filme.notaImdb}</span>
            `;
        }
    } catch (e) {
        console.error("Erro ao carregar filme:", e);
    }
}

async function carregarSerie(id) {
    try {
        const res = await API.buscarSeriePorId(id);
        if (res.ok) {
            const serie = await res.json();
            document.title = `${serie.titulo} - CineTracker`;
            document.getElementById('poster').src = serie.posterUrl;
            document.getElementById('titulo').innerText = serie.titulo;
            document.getElementById('descricao').innerText = serie.descricao || 'Sem sinopse disponível.';

            document.getElementById('tags').innerHTML = `
                <span class="detalhes-tag-item">Ano: ${serie.anoLancamento}</span>
                <span class="detalhes-tag-item">Temporadas: ${serie.qtdTemporadas}</span>
                <span class="detalhes-tag-item" style="background:#e50914;">⭐ IMDb: ${serie.notaImdb}</span>
            `;

            carregarEpisodios(id);
        }
    } catch (e) {
        console.error("Erro ao carregar série:", e);
    }
}

async function carregarEpisodios(idSerie) {
    const area = document.getElementById('areaEpisodios');
    try {
        const res = await API.listarEpisodiosDaSerie(idSerie);
        if (res.ok) {
            const episodios = await res.json();

            if (episodios.length === 0) {
                area.innerHTML = '<p style="color:#aaa;">Nenhum episódio cadastrado para esta série ainda.</p>';
                return;
            }

            const temporadasMap = {};
            episodios.forEach(ep => {
                if(!temporadasMap[ep.numTemporada]) temporadasMap[ep.numTemporada] = [];
                temporadasMap[ep.numTemporada].push(ep);
            });

            const temporadasDisponiveis = Object.keys(temporadasMap).sort((a, b) => a - b);

            let seletorHtml = `<select id="seletorTemporada" class="season-selector">`;
            temporadasDisponiveis.forEach(temp => {
                seletorHtml += `<option value="${temp}">Temporada ${temp}</option>`;
            });
            seletorHtml += `</select>`;

            let listaHtml = `<div id="listaEpisodiosRender" class="temporada-bloco" style="margin-top: 0;"></div>`;
            area.innerHTML = seletorHtml + listaHtml;

            const seletor = document.getElementById('seletorTemporada');
            const listaContainer = document.getElementById('listaEpisodiosRender');

            const renderizarTemporada = (numTemporada) => {
                const eps = temporadasMap[numTemporada] || [];
                let htmlEps = '';
                eps.forEach(ep => {
                    htmlEps += `
                        <div class="ep-item">
                            <h3 style="color:#fff; font-size:18px;">
                                <span style="color:#e50914;">${ep.numEpisodio}.</span> ${ep.titulo}
                                <span style="color:#888; font-size:14px; font-weight:normal;">(${ep.duracao} min)</span>
                            </h3>
                            <p style="font-size:15px; color:#aaa; margin-top:8px;">${ep.descricao || 'Sem sinopse.'}</p>
                        </div>`;
                });
                listaContainer.innerHTML = htmlEps;
            };

            seletor.addEventListener('change', (event) => {
                renderizarTemporada(event.target.value);
            });
            renderizarTemporada(temporadasDisponiveis[0]);
        }
    } catch (e) {
        area.innerHTML = '<p style="color:#e50914;">Erro ao buscar episódios.</p>';
    }
}

async function carregarAvaliacoesDaComunidade(id, tipo) {
    const usuarioLogado = API.getSessao();
    const container = document.getElementById('listaAvaliacoesRender');
    try {
        const res = tipo === 'filme' ? await API.listarAvaliacoesFilme(id) : await API.listarAvaliacoesSerie(id);
        if (res.ok) {
            const avaliacoes = await res.json();
            if(avaliacoes.length === 0) {
                container.innerHTML = '<p style="color:#aaa;">Nenhuma avaliação ainda. Seja o primeiro a comentar!</p>';
                return;
            }

            let html = '';
            avaliacoes.forEach(a => {
                const dataFormatada = new Date(a.data).toLocaleDateString('pt-BR');

                let botoesAcao = '';
                if (usuarioLogado.id === a.idUsuario) {
                    botoesAcao = `
                        <div style="margin-top: 10px; display: flex; gap: 10px;">
                            <button type="button" class="btn-small btn-edit" onclick="prepararEdicaoAvaliacao(${a.idAvaliacao}, ${a.nota}, '${a.comentario.replace(/'/g, "\\'")}')">Editar</button>
                            <button type="button" class="btn-small btn-danger" onclick="excluirAvaliacaoDaTela(${a.idAvaliacao})">Excluir</button>
                        </div>
                    `;
                }

                html += `
                    <div style="background: #222; padding: 15px; border-radius: 8px; border-left: 3px solid ${usuarioLogado.id === a.idUsuario ? '#4caf50' : '#e50914'};">
                        <div style="display: flex; justify-content: space-between; margin-bottom: 8px;">
                            <strong style="color: #fff;">${a.avaliador} ${usuarioLogado.id === a.idUsuario ? '(Você)' : ''}</strong>
                            <span style="color: #888; font-size: 13px;">${dataFormatada}</span>
                        </div>
                        <div style="color: #e50914; font-weight: bold; margin-bottom: 8px;">${a.nota} ⭐</div>
                        <p style="color: #ccc; font-size: 15px;">${a.comentario}</p>
                        ${botoesAcao}
                    </div>
                `;
            });
            container.innerHTML = html;
        }
    } catch (e) {
        console.error("Erro ao carregar avaliações", e);
    }
}

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
    if (!confirm("Deseja realmente apagar sua avaliação?")) return;

    const usuarioLogado = API.getSessao();
    try {
        const res = await API.deletarAvaliacao(idAvaliacao, usuarioLogado.id);
        if (res.ok) {
            const urlParams = new URLSearchParams(window.location.search);
            carregarAvaliacoesDaComunidade(parseInt(urlParams.get('id')), urlParams.get('tipo'));
        } else {
            alert("Erro ao excluir.");
        }
    } catch (e) {
        alert("Erro de conexão.");
    }
}