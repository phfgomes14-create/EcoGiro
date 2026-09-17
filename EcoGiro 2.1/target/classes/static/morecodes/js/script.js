document.addEventListener('DOMContentLoaded', () => {
// Código para o menu mobile
    const mobileButton = document.getElementById('mobile_btn');
    const mobileMenu = document.getElementById('mobile_menu');
    mobileButton?.addEventListener('click', () => {
        mobileMenu?.classList.toggle('active');
        const icon = mobileButton.querySelector('i');
        icon?.classList.toggle('fa-bars');
        icon?.classList.toggle('fa-x');
    });
// Código para o menu de abas
    document.querySelectorAll('.tab-btn').forEach((button) => {
        button.addEventListener('click', () => {
            document.querySelectorAll('.tab-btn, .veiculo-card').forEach((element) => element.classList.remove('active'));
            button.classList.add('active');
            document.getElementById(`tab-${button.dataset.tab}`)?.classList.add('active');
        });
    });

// Código para o FAQ
    document.querySelectorAll('.faq-question').forEach((question) => {
        question.addEventListener('click', () => {
            const item = question.closest('.faq-item');
            const wasActive = item?.classList.contains('active');
            document.querySelectorAll('.faq-item').forEach((faqItem) => faqItem.classList.remove('active'));
            if (!wasActive) item?.classList.add('active');
        });
    });
//modal de contato
    const contactModal = document.getElementById('contact-modal');
    document.getElementById('open-contact-modal')?.addEventListener('click', () => contactModal?.classList.add('active'));
    document.getElementById('close-contact-modal')?.addEventListener('click', () => contactModal?.classList.remove('active'));

    // Fecha ao clicar fora da caixa (no overlay escuro)
    contactModal?.addEventListener('click', (event) => {
        if (event.target === contactModal) {
            contactModal.classList.remove('active');
        }
    });

    // Fecha com a tecla ESC
    document.addEventListener('keydown', (event) => {
        if (event.key === 'Escape') {
            contactModal?.classList.remove('active');
        }
    });

    // Impede o formulário de recarregar a página ao enviar (sem backend ainda)
    document.getElementById('contact-form')?.addEventListener('submit', (event) => {
        event.preventDefault();
        alert('Mensagem enviada! Em breve retornaremos o contato.');
        contactModal?.classList.remove('active');
    });

    // Os links já têm href="quiz.html"; não é necessário redirecionamento extra.
});
