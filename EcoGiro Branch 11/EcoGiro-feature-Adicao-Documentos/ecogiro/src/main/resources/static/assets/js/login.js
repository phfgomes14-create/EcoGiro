(() => {
    document.querySelectorAll('.password-toggle').forEach((toggle) => {
        const password = document.getElementById(toggle.getAttribute('aria-controls'));
        const icon = toggle.querySelector('i');

        if (!password) return;

        toggle.addEventListener('click', () => {
            const showPassword = password.type === 'password';
            password.type = showPassword ? 'text' : 'password';
            toggle.setAttribute('aria-pressed', String(showPassword));
            toggle.setAttribute('aria-label', showPassword ? 'Ocultar senha' : 'Mostrar senha');
            if (icon) icon.className = showPassword ? 'fa-regular fa-eye-slash' : 'fa-regular fa-eye';
        });
    });
})();
(() => {
    const params = new URLSearchParams(window.location.search);
    const heading = document.querySelector('.login-heading, .register-heading');
    if (!heading) return;
    let message = '';
    let type = 'success';
    if (params.get('registered') === '1') message = 'Cadastro realizado com sucesso. Agora você já pode entrar.';
    else if (params.get('logout') === '1') message = 'Sessão encerrada com segurança.';
    else if (params.has('error')) {
        type = 'error';
        const raw = params.get('error');
        message = raw && raw !== '1' ? raw : 'Não foi possível autenticar. Confira seus dados e tente novamente.';
    }
    if (message) {
        const box = document.createElement('div');
        box.className = `auth-feedback ${type}`;
        box.textContent = message;
        heading.appendChild(box);
    }
})();
