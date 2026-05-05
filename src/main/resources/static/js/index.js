async function fazerLogin(event) {
    event.preventDefault();

    const email = document.getElementById('email').value;
    const senha = document.getElementById('senha').value;
    const erro  = document.getElementById('erroLogin');
    erro.style.display = 'none';

    try {
        const resAdmin = await API.loginAdmin(email, senha);
        if (resAdmin.ok) {
            const admin = await resAdmin.json();
            API.setSessao({
                id:    admin.id,
                nome:  admin.nome,
                email: admin.email,
                admin: true
            });
            window.location.href = 'home.html';
            return;
        }
    } catch (e) {
        console.error("Erro ao tentar login admin", e);
    }

    try {
        const resUser = await API.loginUsuario(email, senha);
        if (resUser.ok) {
            const usuario = await resUser.json();
            API.setSessao({
                id:    usuario.id,
                nome:  usuario.nome,
                email: usuario.email,
                admin: false
            });
            window.location.href = 'home.html';
            return;
        }
    } catch (e) {
        console.error("Erro ao tentar login usuário", e);
    }

    erro.style.display = 'block';
}