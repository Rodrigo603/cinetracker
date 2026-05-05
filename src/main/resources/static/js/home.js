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

                // Redireciona para a página de detalhes
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