const API = (() => {

    async function _request(method, url, body = null) {
        const options = {
            method,
            headers: { 'Content-Type': 'application/json' }
        };
        if (body) options.body = JSON.stringify(body);

        const response = await fetch(url, options);
        return response;
    }

    function getSessao() {
        return JSON.parse(localStorage.getItem('usuarioLogado'));
    }

    function setSessao(dados) {
        localStorage.setItem('usuarioLogado', JSON.stringify(dados));
    }

    function encerrarSessao() {
        localStorage.removeItem('usuarioLogado');
        window.location.href = 'index.html';
    }

    async function loginAdmin(email, senha) {
        return _request('POST', '/admin/login', { email, senha });
    }

    async function loginUsuario(email, senha) {
        return _request('POST', '/usuarios/login', { email, senha });
    }

    async function cadastrarUsuario(payload) {
        return _request('POST', '/usuarios/cadastrar', payload);
    }

    async function buscarUsuario(id) {
        return _request('GET', `/usuarios/${id}`);
    }

    async function atualizarUsuario(id, payload) {
        return _request('PUT', `/usuarios/atualizar/${id}`, payload);
    }

    async function excluirUsuario(id) {
        return _request('DELETE', `/admin/usuarios/${id}`);
    }

    async function listarFilmes() {
        return _request('GET', '/filmes');
    }

    async function criarFilme(payload) {
        return _request('POST', '/admin/filmes', payload);
    }

    async function atualizarFilme(id, payload) {
        return _request('PUT', `/admin/filmes/${id}`, payload);
    }

    async function deletarFilme(id) {
        return _request('DELETE', `/admin/filmes/${id}`);
    }

    async function criarAdmin(payload) {
        return _request('POST', '/admin/novo-admin', payload);
    }

    return {
        getSessao,
        setSessao,
        encerrarSessao,
        loginAdmin,
        loginUsuario,
        cadastrarUsuario,
        buscarUsuario,
        atualizarUsuario,
        excluirUsuario,
        listarFilmes,
        criarFilme,
        atualizarFilme,
        deletarFilme,
        criarAdmin
    };
})();