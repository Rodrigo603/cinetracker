document.addEventListener("DOMContentLoaded", () => {
    const usuario = API.getSessao();
    if (!usuario) { window.location.href = 'index.html'; return; }

    if (usuario.admin) {
        document.getElementById('linkAdmin').style.display = 'block';
    }
    carregarFilmes();
});

async function carregarFilmes() {
    try {
        const response = await API.listarFilmes();
        if (response.ok) {
            const filmes    = await response.json();
            const container = document.getElementById('listaFilmes');
            container.innerHTML = '';

            filmes.forEach(f => {
                const div   = document.createElement('div');
                div.className = 'poster-card';
                const titulo = f.titulo   || 'Filme';
                const poster = f.posterUrl || '';

                div.innerHTML = `<img src="${poster}" alt="${titulo}" title="${titulo}"
                                 onerror="this.onerror=null;this.src='https://via.placeholder.com/200x300/2b2b2b/FFFFFF?text=Sem+Capa';">`;
                container.appendChild(div);
            });
        }
    } catch (e) {
        console.error("Erro ao buscar filmes", e);
    }
}