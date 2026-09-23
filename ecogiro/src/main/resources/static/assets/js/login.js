(() => {
    const password = document.querySelector('#password');
    const toggle = document.querySelector('.password-toggle');

    if (!password || !toggle) return;

    toggle.addEventListener('click', () => {
        const showPassword = password.type === 'password';
        password.type = showPassword ? 'text' : 'password';
        toggle.textContent = showPassword ? 'Ocultar' : 'Mostrar';
        toggle.setAttribute('aria-pressed', String(showPassword));
    });
})();