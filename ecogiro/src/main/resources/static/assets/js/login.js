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