document.addEventListener("DOMContentLoaded", () => {
    const usuario = API.getSessao();
    if (!usuario) {
        window.location.href = 'index.html';
        return;
    }

    if (usuario.admin) {
        document.getElementById('linkAdmin').style.display = 'block';
    }

    document.getElementById('btnSair').addEventListener('click', (e) => {
        e.preventDefault();
        API.encerrarSessao();
    });

    carregarFilmes();
    carregarSeries();
});

async function carregarFilmes() {
    try {
        const response = await API.listarFilmes();
        if (response.ok) {
            const filmes = await response.json();
            const container = document.getElementById('listaFilmes');
            container.innerHTML = '';

            filmes.forEach(f => {
                const div = document.createElement('div');
                div.className = 'poster-card';
                div.innerHTML = `<img src="${f.posterUrl}" onerror="this.onerror=null;this.src='https://via.placeholder.com/200x300/2b2b2b/FFFFFF?text=Sem+Capa';">`;

                div.addEventListener('click', () => abrirDetalhesFilme(f));

                container.appendChild(div);
            });
        }
    } catch (e) {
        console.error("Erro ao buscar filmes", e);
    }
}

async function carregarSeries() {
    try {
        const response = await API.listarSeries();
        if (response.ok) {
            const series = await response.json();
            const container = document.getElementById('listaSeries');
            container.innerHTML = '';

            series.forEach(s => {
                const div = document.createElement('div');
                div.className = 'poster-card';
                div.innerHTML = `<img src="${s.posterUrl}" onerror="this.onerror=null;this.src='https://via.placeholder.com/200x300/2b2b2b/FFFFFF?text=Sem+Capa';">`;

                div.addEventListener('click', () => abrirDetalhesSerie(s));

                container.appendChild(div);
            });
        }
    } catch (e) {
        console.error("Erro ao buscar séries", e);
    }
}

function abrirDetalhesFilme(f) {
    window.location.href = `detalhes.html?id=${f.idMidia}&tipo=filme`;
}

function abrirDetalhesSerie(s) {
    window.location.href = `detalhes.html?id=${s.idMidia}&tipo=serie`;
}

async function pesquisarHome() {
    const titulo = document.getElementById('inputBuscaHome').value.trim();
    if (!titulo) return;

    try {
        const btnLimpar = document.getElementById('btnLimparBusca');
        btnLimpar.style.display = 'block';

        // Esconde as outras seções ("Aclamados", etc) para deixar a tela limpa
        document.querySelectorAll('.row').forEach((row, index) => {
            if (index > 1) row.style.display = 'none'; // Index 0 e 1 são Filmes e Séries
        });

        // Executa ambas as buscas simultaneamente no banco de dados
        const [resFilmes, resSeries] = await Promise.all([
            API.buscarFilmeBackend(titulo),
            API.buscarSerieBackend(titulo)
        ]);

        // Processamento de Filmes
        const containerFilmes = document.getElementById('listaFilmes');
        const tituloFilmes = document.getElementById('tituloSecaoFilmes');
        containerFilmes.innerHTML = '';

        if (resFilmes.ok) {
            const filmes = await resFilmes.json();
            tituloFilmes.innerText = `Filmes encontrados para "${titulo}" (${filmes.length})`;
            if (filmes.length === 0) {
                containerFilmes.innerHTML = `<p style="color: #888; font-size: 15px; padding-left: 5px;">Nenhum filme correspondente.</p>`;
            } else {
                filmes.forEach(f => {
                    const div = document.createElement('div');
                    div.className = 'poster-card';
                    div.innerHTML = `<img src="${f.posterUrl}" onerror="this.onerror=null;this.src='https://via.placeholder.com/200x300/2b2b2b/FFFFFF?text=Sem+Capa';">`;
                    div.addEventListener('click', () => abrirDetalhesFilme(f));
                    containerFilmes.appendChild(div);
                });
            }
        } else {
            tituloFilmes.innerText = `Filmes encontrados para "${titulo}" (0)`;
            containerFilmes.innerHTML = `<p style="color: #888; font-size: 15px; padding-left: 5px;">Nenhum filme correspondente.</p>`;
        }

        // Processamento de Séries
        const containerSeries = document.getElementById('listaSeries');
        const tituloSeries = document.getElementById('tituloSecaoSeries');
        containerSeries.innerHTML = '';

        if (resSeries.ok) {
            const series = await resSeries.json();
            tituloSeries.innerText = `Séries encontradas para "${titulo}" (${series.length})`;
            if (series.length === 0) {
                containerSeries.innerHTML = `<p style="color: #888; font-size: 15px; padding-left: 5px;">Nenhuma série correspondente.</p>`;
            } else {
                series.forEach(s => {
                    const div = document.createElement('div');
                    div.className = 'poster-card';
                    div.innerHTML = `<img src="${s.posterUrl}" onerror="this.onerror=null;this.src='https://via.placeholder.com/200x300/2b2b2b/FFFFFF?text=Sem+Capa';">`;
                    div.addEventListener('click', () => abrirDetalhesSerie(s));
                    containerSeries.appendChild(div);
                });
            }
        } else {
            tituloSeries.innerText = `Séries encontradas para "${titulo}" (0)`;
            containerSeries.innerHTML = `<p style="color: #888; font-size: 15px; padding-left: 5px;">Nenhuma série correspondente.</p>`;
        }
    } catch (e) {
        console.error("Erro ao realizar busca unificada:", e);
    }
}

function limparBuscaHome() {
    document.getElementById('inputBuscaHome').value = '';
    document.getElementById('tituloSecaoFilmes').innerText = 'Filmes no Catálogo';
    document.getElementById('tituloSecaoSeries').innerText = 'Séries no Catálogo';
    document.getElementById('btnLimparBusca').style.display = 'none';

    // Mostra novamente todas as seções de "Explorar"
    document.querySelectorAll('.row').forEach(row => row.style.display = 'block');

    carregarFilmes();
    carregarSeries();
}

// Atalho para pesquisar ao pressionar a tecla Enter
document.getElementById('inputBuscaHome')?.addEventListener('keypress', function (e) {
    if (e.key === 'Enter') {
        pesquisarHome();
    }
});