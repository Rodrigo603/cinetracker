async function fazerCadastro(event) {
    event.preventDefault();
    const erro = document.getElementById('erroCadastro');
    erro.style.display = 'none';

    const payload = {
        nome:      document.getElementById('nome').value,
        email:     document.getElementById('email').value,
        telefone:  document.getElementById('telefone').value,
        senha:     document.getElementById('senha').value
    };

    try {
        const response = await API.cadastrarUsuario(payload);

        if (response.ok) {
            let sessao = { nome: payload.nome, email: payload.email, admin: false };

            const contentType = response.headers.get('Content-Type') || '';
            if (contentType.includes('application/json')) {
                try {
                    const novoUsuario = await response.json();
                    if (novoUsuario && novoUsuario.id) {
                        sessao = {
                            id:    novoUsuario.id,
                            nome:  novoUsuario.nome  || payload.nome,
                            email: novoUsuario.email || payload.email,
                            admin: false
                        };
                    }
                } catch (_) { }
            }

            API.setSessao(sessao);
            alert("Usuário cadastrado com sucesso!");
            window.location.href = 'index.html';
        } else {
            const contentType = response.headers.get('Content-Type') || '';
            let msg = 'Erro ao cadastrar. Tente novamente.';
            if (contentType.includes('application/json')) {
                try {
                    const body = await response.json();
                    if (body.message) msg = body.message;
                } catch (_) {}
            }
            document.getElementById('erroCadastro').innerText = msg;
            erro.style.display = 'block';
            console.error("Erro do servidor:", response.status);
        }
    } catch (error) {
        erro.style.display = 'block';
        console.error("Erro ao conectar com o servidor.", error);
    }
}