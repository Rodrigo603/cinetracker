let filmesGlobais = [];
let seriesGlobais = [];

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
            filmesGlobais = await response.json();
            renderizarFilmes(filmesGlobais);
        }
    } catch (e) {
        console.error("Erro ao buscar filmes", e);
    }
}

async function carregarSeries() {
    try {
        const response = await API.listarSeries();
        if (response.ok) {
            seriesGlobais = await response.json();
            renderizarSeries(seriesGlobais);
        }
    } catch (e) {
        console.error("Erro ao buscar séries", e);
    }
}

function criarPosterCard(item, tipo) {
    const div = document.createElement('div');
    div.className = 'poster-card';

    const id    = item.idMidia;
    const tipoUpper = tipo === 'FILME' ? 'FILME' : 'SERIE';

    div.innerHTML = `
        <img src="${item.posterUrl}"
             onerror="this.onerror=null;this.src='https://via.placeholder.com/200x300/2b2b2b/FFFFFF?text=Sem+Capa';">
        <button
            class="btn-add-lista"
            title="Salvar na lista"
            onclick="event.stopPropagation(); abrirModalAdicionarItem(${id}, '${tipoUpper}', this)">
            <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24"
                 fill="none" stroke="currentColor" stroke-width="2.5"
                 stroke-linecap="round" stroke-linejoin="round">
                <line x1="12" y1="5" x2="12" y2="19"/>
                <line x1="5" y1="12" x2="19" y2="12"/>
            </svg>
        </button>
    `;

    div.addEventListener('click', () => {
        if (tipo === 'FILME') abrirDetalhesFilme(item);
        else abrirDetalhesSerie(item);
    });

    return div;
}

function renderizarFilmes(lista) {
    const container = document.getElementById('listaFilmes');
    container.innerHTML = '';

    if (lista.length === 0) {
        container.innerHTML = `<p style="color: #888; font-size: 15px; padding-left: 5px;">Nenhum filme correspondente.</p>`;
        return;
    }

    lista.forEach(f => container.appendChild(criarPosterCard(f, 'FILME')));
}

function renderizarSeries(lista) {
    const container = document.getElementById('listaSeries');
    container.innerHTML = '';

    if (lista.length === 0) {
        container.innerHTML = `<p style="color: #888; font-size: 15px; padding-left: 5px;">Nenhuma série correspondente.</p>`;
        return;
    }

    lista.forEach(s => container.appendChild(criarPosterCard(s, 'SERIE')));
}

function abrirDetalhesFilme(f) {
    window.location.href = `detalhes.html?id=${f.idMidia}&tipo=filme`;
}

function abrirDetalhesSerie(s) {
    window.location.href = `detalhes.html?id=${s.idMidia}&tipo=serie`;
}

function filtrarCatalogo(genero, btnClicado) {
    document.querySelectorAll('.filter-btn').forEach(btn => {
        btn.classList.remove('active');
        btn.style.backgroundColor = '#333';
    });

    if (btnClicado) {
        btnClicado.classList.add('active');
        btnClicado.style.backgroundColor = '#e50914';
    }

    const tituloFilmes = document.getElementById('tituloSecaoFilmes');
    const tituloSeries = document.getElementById('tituloSecaoSeries');

    if (genero === 'Todos') {
        tituloFilmes.innerText = 'Filmes no Catálogo';
        tituloSeries.innerText = 'Séries no Catálogo';

        renderizarFilmes(filmesGlobais);
        renderizarSeries(seriesGlobais);

        document.querySelectorAll('.row').forEach(row => row.style.display = 'block');
    } else {
        tituloFilmes.innerText = `Filmes de ${genero}`;
        tituloSeries.innerText = `Séries de ${genero}`;

        if (filmesGlobais.length > 0 && typeof filmesGlobais[0].generos === 'undefined') {
            console.error("⚠️ ERRO: A propriedade 'generos' não existe nos objetos enviados pelo back-end. Certifique-se de que reiniciou o Spring Boot após atualizar os repositórios!");
        }

        const filmesFiltrados = filmesGlobais.filter(f => f.generos && f.generos.includes(genero));
        const seriesFiltradas = seriesGlobais.filter(s => s.generos && s.generos.includes(genero));

        renderizarFilmes(filmesFiltrados);
        renderizarSeries(seriesFiltradas);

        document.querySelectorAll('.row').forEach((row, index) => {
            if (index > 1) row.style.display = 'none';
        });
    }
}

async function pesquisarHome() {
    const titulo = document.getElementById('inputBuscaHome').value.trim();
    if (!titulo) return;

    try {
        const btnLimpar = document.getElementById('btnLimparBusca');
        btnLimpar.style.display = 'block';

        document.querySelectorAll('.filter-btn').forEach(btn => {
            btn.classList.remove('active');
            btn.style.backgroundColor = '#333';
        });
        const btnTodos = document.querySelector('.filter-btn');
        if(btnTodos) {
            btnTodos.classList.add('active');
            btnTodos.style.backgroundColor = '#e50914';
        }

        document.querySelectorAll('.row').forEach((row, index) => {
            if (index > 1) row.style.display = 'none';
        });

        const [resFilmes, resSeries] = await Promise.all([
            API.buscarFilmeBackend(titulo),
            API.buscarSerieBackend(titulo)
        ]);

        const tituloFilmes = document.getElementById('tituloSecaoFilmes');
        if (resFilmes.ok) {
            const filmes = await resFilmes.json();
            tituloFilmes.innerText = `Filmes encontrados para "${titulo}" (${filmes.length})`;
            renderizarFilmes(filmes);
        } else {
            tituloFilmes.innerText = `Filmes encontrados para "${titulo}" (0)`;
            renderizarFilmes([]);
        }

        const tituloSeries = document.getElementById('tituloSecaoSeries');
        if (resSeries.ok) {
            const series = await resSeries.json();
            tituloSeries.innerText = `Séries encontradas para "${titulo}" (${series.length})`;
            renderizarSeries(series);
        } else {
            tituloSeries.innerText = `Séries encontradas para "${titulo}" (0)`;
            renderizarSeries([]);
        }
    } catch (e) {
        console.error("Erro ao realizar busca unificada:", e);
    }
}

function limparBuscaHome() {
    document.getElementById('inputBuscaHome').value = '';
    document.getElementById('btnLimparBusca').style.display = 'none';
    filtrarCatalogo('Todos', document.querySelector('.filter-btn'));
}

document.getElementById('inputBuscaHome')?.addEventListener('keypress', function (e) {
    if (e.key === 'Enter') {
        pesquisarHome();
    }
});