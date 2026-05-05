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

            // O Ano entra aqui, nas tags gerais da Série!
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