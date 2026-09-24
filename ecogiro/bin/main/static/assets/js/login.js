(() => {
    // 1. Funcionalidade de Mostrar/Ocultar Senha
    const password = document.querySelector('#password');
    const toggle = document.querySelector('.password-toggle');

    if (password && toggle) {
        toggle.addEventListener('click', () => {
            const showPassword = password.type === 'password';
            password.type = showPassword ? 'text' : 'password';
            toggle.textContent = showPassword ? 'Ocultar' : 'Mostrar';
            toggle.setAttribute('aria-pressed', String(showPassword));
        });
    }

    // 2. Integração com o Back-end Java (Envio via Fetch)
    const loginForm = document.querySelector('form');
    const emailInput = document.getElementById('username');

    if (loginForm && emailInput && password) {
        loginForm.addEventListener('submit', async (event) => {
            event.preventDefault(); // Impede o envio tradicional do HTML

            const payload = {
                email: emailInput.value,
                senha: password.value
            };

            try {
                const response = await fetch('/api/usuarios/login', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify(payload)
                });

                if (response.ok) {
                    alert('Login realizado com sucesso!');
                    window.location.href = 'index.htm'; // Redireciona para a página inicial
                } else {
                    const errorText = await response.text();
                    alert('Falha no login: ' + errorText);
                }
            } catch (error) {
                console.error('Erro na requisição:', error);
                alert('Erro ao conectar com o servidor.');
            }
        });
    }
})();