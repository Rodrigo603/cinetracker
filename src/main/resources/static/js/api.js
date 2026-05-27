const API = (() => {
    async function _request(method, url, body = null) {
        const options = { method, headers: { 'Content-Type': 'application/json' } };
        if (body) options.body = JSON.stringify(body);
        return fetch(url, options);
    }

    function getSessao()      { return JSON.parse(localStorage.getItem('usuarioLogado')); }
    function setSessao(dados) { localStorage.setItem('usuarioLogado', JSON.stringify(dados)); }
    function encerrarSessao() { localStorage.removeItem('usuarioLogado'); window.location.href = 'index.html'; }

    async function loginAdmin(email, senha)        { return _request('POST', '/admin/login', { email, senha }); }
    async function loginUsuario(email, senha)       { return _request('POST', '/usuarios/login', { email, senha }); }
    async function cadastrarUsuario(payload)        { return _request('POST', '/usuarios/cadastrar', payload); }
    async function buscarUsuario(id)                { return _request('GET',  `/usuarios/${id}`); }
    async function atualizarUsuario(id, payload)    { return _request('PUT',  `/usuarios/atualizar/${id}`, payload); }
    async function excluirUsuario(id)               { return _request('DELETE', `/admin/usuarios/${id}`); }

    async function criarAdmin(payload)              { return _request('POST', '/admin/novo-admin', payload); }
    async function buscarAdmin(id)                  { return _request('GET',  `/admin/${id}`); }
    async function atualizarAdmin(id, payload)      { return _request('PUT',  `/admin/atualizar/${id}`, payload); }

    async function listarFilmes()                   { return _request('GET',    '/filmes'); }
    async function buscarFilmePorId(id)             { return _request('GET',    `/filmes/${id}`); }
    async function criarFilme(payload)              { return _request('POST',   '/admin/filmes', payload); }
    async function atualizarFilme(id, payload)      { return _request('PUT',    `/admin/filmes/${id}`, payload); }
    async function deletarFilme(id)                 { return _request('DELETE', `/admin/filmes/${id}`); }

    async function listarSeries()                   { return _request('GET',    '/series'); }
    async function buscarSeriePorId(id)             { return _request('GET',    `/series/${id}`); }
    async function listarEpisodiosDaSerie(idSerie)  { return _request('GET',    `/series/${idSerie}/episodios`); }
    async function criarSerie(payload)              { return _request('POST',   '/admin/series', payload); }
    async function atualizarSerie(id, payload)      { return _request('PUT',    `/admin/series/${id}`, payload); }
    async function deletarSerie(id)                 { return _request('DELETE', `/admin/series/${id}`); }

    async function criarAvaliacao(payload)          { return _request('POST',   '/avaliacoes', payload); }
    async function atualizarAvaliacao(id, payload)  { return _request('PUT',    `/avaliacoes/${id}`, payload); }
    async function deletarAvaliacao(id, idUsuario)  { return _request('DELETE', `/avaliacoes/${id}?idUsuario=${idUsuario}`); }
    async function listarAvaliacoesFilme(id)        { return _request('GET',    `/avaliacoes/filme/${id}`); }
    async function listarAvaliacoesSerie(id)        { return _request('GET',    `/avaliacoes/serie/${id}`); }
    async function deletarComentarioAdmin(id)       { return _request('DELETE', `/admin/comentarios/${id}`); }

    async function listarUsuariosAdmin()            { return _request('GET', '/admin/usuarios'); }
    async function buscarFilmeBackend(titulo)       { return _request('GET', `/filmes/buscar?titulo=${encodeURIComponent(titulo)}`); }
    async function buscarSerieBackend(titulo)       { return _request('GET', `/series/buscar?titulo=${encodeURIComponent(titulo)}`); }

    async function gerarRelatorioUsuario(id)        { return _request('GET', `/usuarios/${id}/relatorio`); }
    async function buscarEstatisticasUsuario(id)    { return _request('GET', `/usuarios/${id}/estatisticas`); }

    async function criarLista(payload)                            { return _request('POST',   '/listas', payload); }
    async function listarListasDoUsuario(idUsuario)               { return _request('GET',    `/listas/usuario/${idUsuario}`); }
    async function renomearLista(idLista, payload)                { return _request('PUT',    `/listas/${idLista}`, payload); }
    async function deletarLista(idLista, idUsuario)               { return _request('DELETE', `/listas/${idLista}?idUsuario=${idUsuario}`); }
    async function listarItensLista(idLista, idUsuario)           { return _request('GET',    `/listas/${idLista}/itens?idUsuario=${idUsuario}`); }
    async function adicionarItemLista(idLista, payload)           { return _request('POST',   `/listas/${idLista}/itens`, payload); }
    async function removerItemLista(idLista, idContem, idUsuario) { return _request('DELETE', `/listas/${idLista}/itens/${idContem}?idUsuario=${idUsuario}`); }

    return {
        getSessao, setSessao, encerrarSessao,
        loginAdmin, loginUsuario, cadastrarUsuario, buscarUsuario, atualizarUsuario,
        criarAdmin, buscarAdmin, atualizarAdmin,
        listarFilmes, buscarFilmePorId, criarFilme, atualizarFilme, deletarFilme,
        listarSeries, buscarSeriePorId, listarEpisodiosDaSerie, criarSerie, atualizarSerie, deletarSerie,
        criarAvaliacao, atualizarAvaliacao, deletarAvaliacao,
        listarAvaliacoesFilme, listarAvaliacoesSerie,
        deletarComentarioAdmin, listarUsuariosAdmin, buscarFilmeBackend, buscarSerieBackend,
        gerarRelatorioUsuario, buscarEstatisticasUsuario,
        criarLista, listarListasDoUsuario, renomearLista, deletarLista,
        listarItensLista, adicionarItemLista, removerItemLista
    };
})();