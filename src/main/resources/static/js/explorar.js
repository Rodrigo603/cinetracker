const API_EXPLORAR = "http://localhost:8080/api/explorar";

function criarPosterIdentico(id, posterUrl, tipoMidia) {
    const linkDestino = `detalhes.html?id=${id}&tipo=${tipoMidia}`;
    return `
        <div class="poster-card" onclick="window.location.href='${linkDestino}'">
            <img src="${posterUrl}" onerror="this.onerror=null;this.src='https://via.placeholder.com/200x300/2b2b2b/FFFFFF?text=Sem+Capa';">
        </div>`;
}

async function carregarHomeMediaAlta() {
    try {
        const res = await fetch(`${API_EXPLORAR}/filmes-media-alta`);
        const dados = await res.json();
        const container = document.getElementById("home-media-alta");
        container.innerHTML = "";

        dados.forEach(f => {
            container.innerHTML += criarPosterIdentico(f.idMidia, f.posterUrl, 'filme');
        });
    } catch (error) {
        console.error("Erro ao carregar Média Alta", error);
    }
}

async function carregarHomePorGenero(genero) {
    try {
        const res = await fetch(`${API_EXPLORAR}/filmes-por-genero/${genero}`);
        const dados = await res.json();
        const container = document.getElementById("home-genero-acao");
        container.innerHTML = "";

        dados.forEach(f => {
            container.innerHTML += criarPosterIdentico(f.idMidia, f.posterUrl, 'filme');
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
        container.innerHTML = "";

        dados.forEach(s => {
            container.innerHTML += criarPosterIdentico(s.idMidia, s.posterUrl, 'serie');
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
        container.innerHTML = "";
        const filmesComTipo = filmes.map(f => ({ ...f, tipoMidia: 'filme' }));
        const seriesComTipo = series.map(s => ({ ...s, tipoMidia: 'serie' }));
        let obrasMisturadas = [...filmesComTipo, ...seriesComTipo];
        obrasMisturadas.sort(() => Math.random() - 0.5);
        obrasMisturadas.forEach(obra => {
            container.innerHTML += criarPosterIdentico(obra.idMidia, obra.posterUrl, obra.tipoMidia);
        });
        if(obrasMisturadas.length === 0) {
            container.innerHTML = "<p style='color:#ccc; padding-left:20px;'>A comunidade já avaliou todo o catálogo!</p>";
        }

    } catch (error) {
        console.error("Erro ao carregar obras sem avaliação", error);
    }
}

document.addEventListener("DOMContentLoaded", () => {
    carregarHomeMediaAlta();
    carregarHomePorGenero("Ação");
    carregarHomeSeriesMedia();
    carregarHomeSemAvaliacao();
});