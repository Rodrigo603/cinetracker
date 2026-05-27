const API_EXPLORAR = "http://localhost:8080/api/explorar";

function criarPosterIdentico(id, posterUrl, tipoMidia) {
    const linkDestino = `detalhes.html?id=${id}&tipo=${tipoMidia}`;
    const tipoUpper = tipoMidia.toUpperCase() === 'FILME' ? 'FILME' : 'SERIE';

    const div = document.createElement('div');
    div.className = 'poster-card';

    div.innerHTML = `
        <img src="${posterUrl}"
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
        </button>`;

    div.addEventListener('click', () => {
        window.location.href = linkDestino;
    });

    return div;
}

async function carregarHomeMediaAlta() {
    try {
        const res = await fetch(`${API_EXPLORAR}/filmes-media-alta`);
        const dados = await res.json();
        const container = document.getElementById("home-media-alta");
        if (!container) return;
        container.innerHTML = "";

        dados.forEach(f => {
            container.appendChild(criarPosterIdentico(f.idMidia, f.posterUrl, 'filme'));
        });
    } catch (error) {
        console.error("Erro ao carregar Média Alta", error);
    }
}

async function carregarHomePorGenero(genero) {
    try {
        const res = await fetch(`${API_EXPLORAR}/filmes-por-genero/${genero}`);
        const dados = await res.json();
        const container = document.getElementById("home-genero-dinamico");
        if (!container) return;
        container.innerHTML = "";

        const tituloSecao = document.getElementById("titulo-genero-dinamico");
        if (tituloSecao) {
            tituloSecao.innerText = `Destaques em ${genero}`;
        }

        if (dados.length === 0) {
            container.innerHTML = `<p style='color:#ccc; padding-left:20px;'>Ainda não há filmes de ${genero} com avaliações altas.</p>`;
            return;
        }

        dados.forEach(f => {
            container.appendChild(criarPosterIdentico(f.idMidia, f.posterUrl, 'filme'));
        });

    } catch (error) {
        console.error("Erro ao carregar Por Gênero", error);
    }
}

async function carregarHomeSeriesMedia() {
    try {
        const res = await fetch(`${API_EXPLORAR}/series-acima-da-media`);
        const dados = await res.json();
        const container = document.getElementById("home-series-media");
        if (!container) return;
        container.innerHTML = "";

        dados.forEach(s => {
            container.appendChild(criarPosterIdentico(s.idMidia, s.posterUrl, 'serie'));
        });
    } catch (error) {
        console.error("Erro ao carregar Séries", error);
    }
}

async function carregarHomeSemAvaliacao() {
    try {
        const [resFilmes, resSeries] = await Promise.all([
            fetch(`${API_EXPLORAR}/filmes-sem-avaliacao`),
            fetch(`${API_EXPLORAR}/series-sem-avaliacao`)
        ]);

        const filmes = await resFilmes.json();
        const series = await resSeries.json();

        const container = document.getElementById("home-sem-avaliacao");
        if (!container) return;
        container.innerHTML = "";

        const filmesComTipo = filmes.map(f => ({ ...f, tipoMidia: 'filme' }));
        const seriesComTipo = series.map(s => ({ ...s, tipoMidia: 'serie' }));
        let obrasMisturadas = [...filmesComTipo, ...seriesComTipo];
        obrasMisturadas.sort(() => Math.random() - 0.5);

        if (obrasMisturadas.length === 0) {
            container.innerHTML = "<p style='color:#ccc; padding-left:20px;'>A comunidade já avaliou todo o catálogo!</p>";
            return;
        }

        obrasMisturadas.forEach(obra => {
            container.appendChild(criarPosterIdentico(obra.idMidia, obra.posterUrl, obra.tipoMidia));
        });

    } catch (error) {
        console.error("Erro ao carregar obras sem avaliação", error);
    }
}

document.addEventListener("DOMContentLoaded", () => {
    const listaGeneros = ["Ação", "Drama", "Comédia", "Ficção Científica", "Terror", "Romance", "Fantasia", "Mistério"];
    const generoSorteado = listaGeneros[Math.floor(Math.random() * listaGeneros.length)];

    carregarHomeMediaAlta();
    carregarHomePorGenero(generoSorteado);
    carregarHomeSeriesMedia();
    carregarHomeSemAvaliacao();
});