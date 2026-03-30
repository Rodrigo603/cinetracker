let filmesCatalogo = [];

document.addEventListener("DOMContentLoaded", () => {
    const u = API.getSessao();
    if (!u || !u.admin) { window.location.href = 'home.html'; return; }
    carregarCatalogo();
});

async function carregarCatalogo() {
    const res = await API.listarFilmes();
    if (!res.ok) return;

    filmesCatalogo = await res.json();
    const tbody  = document.getElementById('listaFilmesTbody');
    tbody.innerHTML = '';

    filmesCatalogo.forEach(f => {
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td>${f.idMidia}</td>
            <td>${f.titulo}</td>
            <td class="action-buttons">
                <button class="btn-small btn-edit" onclick="prepararEdicao(${f.idMidia})">Editar</button>
                <button class="btn-small btn-danger" onclick="deletarMidia(${f.idMidia})">Excluir</button>
            </td>`;
        tbody.appendChild(tr);
    });
}

async function adicionarMidia(event) {
    event.preventDefault();
    const tituloMedia = document.getElementById('tituloMedia').value;
    const payload = { titulo: tituloMedia };

    document.getElementById('btn-salvar-midia').innerText = "Buscando na OMDb...";

    try {
        const res = await API.criarFilme(payload);
        if (res.ok) {
            alert(await res.text());
            document.getElementById('formMidia').reset();
            carregarCatalogo();
        } else {
            alert(await res.text() || "Erro ao adicionar filme.");
        }
    } catch (error) {
        console.error("Erro:", error);
        alert("Erro de comunicação com o servidor.");
    } finally {
        document.getElementById('btn-salvar-midia').innerText = "Adicionar ao Catálogo";
    }
}

function prepararEdicao(id) {
    const filme = filmesCatalogo.find(f => f.idMidia === id);
    if (!filme) return;

    document.getElementById('editIdMidia').value = filme.idMidia;
    document.getElementById('editTitulo').value = filme.titulo;
    document.getElementById('editDescricao').value = filme.descricao || '';
    document.getElementById('editPoster').value = filme.posterUrl || '';
    document.getElementById('editDuracao').value = filme.duracao || 0;
    document.getElementById('editBilheteria').value = filme.bilheteria || 0;

    document.getElementById('form-midia-container').classList.add('hidden');
    const editContainer = document.getElementById('form-editar-container');
    editContainer.classList.remove('hidden');
    editContainer.classList.add('editing-mode');

    editContainer.scrollIntoView({ behavior: 'smooth', block: 'center' });
    setTimeout(() => editContainer.classList.remove('editing-mode'), 2000);
}

async function salvarEdicao(event) {
    event.preventDefault();
    const id = document.getElementById('editIdMidia').value;

    const payload = {
        titulo: document.getElementById('editTitulo').value,
        descricao: document.getElementById('editDescricao').value,
        posterUrl: document.getElementById('editPoster').value,
        duracao: document.getElementById('editDuracao').value,
        bilheteria: document.getElementById('editBilheteria').value
    };

    try {
        const res = await API.atualizarFilme(id, payload);
        if (res.ok) {
            alert("Filme atualizado com sucesso!");
            cancelarEdicao();
            carregarCatalogo();
        } else {
            alert("Erro ao atualizar o filme.");
        }
    } catch (error) {
        console.error(error);
        alert("Erro de comunicação com o servidor.");
    }
}

function cancelarEdicao() {
    document.getElementById('formEditarMidia').reset();
    document.getElementById('form-editar-container').classList.add('hidden');
    document.getElementById('form-midia-container').classList.remove('hidden');
}

async function criarNovoAdmin(event) {
    event.preventDefault();
    const payload = {
        nome:      document.getElementById('nomeAdmin').value,
        email:     document.getElementById('emailAdmin').value,
        telefone:  document.getElementById('telAdmin').value,
        senha:     document.getElementById('senhaAdmin').value
    };
    const res = await API.criarAdmin(payload);
    if (res.ok) {
        alert("Sucesso: Novo administrador cadastrado!");
        event.target.reset();
    } else {
        alert("Erro ao cadastrar administrador. Verifique se o e-mail contém 'admin'.");
    }
}

async function excluirUsuario() {
    const id = document.getElementById('idUsuarioExcluir').value;
    if (!id) { alert("Por favor, insira um ID válido."); return; }
    if (!confirm(`Deseja realmente excluir permanentemente o ID ${id}?`)) return;

    const res = await API.excluirUsuario(id);
    if (res.ok) {
        alert("Usuário/Admin removido com sucesso!");
        document.getElementById('idUsuarioExcluir').value = '';
    } else {
        alert("Erro ao excluir. Verifique se o ID existe no banco de dados.");
    }
}

async function deletarMidia(id) {
    if (!confirm("Excluir filme do catálogo?")) return;
    await API.deletarFilme(id);
    carregarCatalogo();
}