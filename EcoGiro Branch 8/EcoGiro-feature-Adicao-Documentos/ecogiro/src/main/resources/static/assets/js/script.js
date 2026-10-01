$(document).ready(function() {
// Código para o menu mobile
    $('#mobile_btn').on('click', function() {
        $('#mobile_menu').toggleClass('active');
        $('#mobile_btn').find('i').toggleClass('fa-bars fa-x');
    });
// Código para o menu de abas
    $('.tab-btn').on('click', function() {
        const tab = $(this).data('tab');

        $('.tab-btn').removeClass('active');
        $(this).addClass('active');

        $('.veiculo-card').removeClass('active');
        $('#tab-' + tab).addClass('active');
    });

    // Filtros e pontos de acesso do mapa de disponibilidade
    const mapPoints = $('.map-point');
    const updateAccessPoint = function(point) {
        const region = point.data('region');
        const access = point.data('access');

        mapPoints.removeClass('selected');
        point.addClass('selected');
        $('#selected-region').text(region);
        $('#selected-access').text(access);
        $('#selected-vehicles').text(point.data('vehicles'));
        $('#access-route').attr('href', 'https://www.google.com/maps/search/?api=1&query=' + encodeURIComponent(access + ', ' + region + ', Distrito Federal'));
    };

    $('.availability-filter').on('click', function() {
        const vehicle = $(this).data('vehicle');
        $('.availability-filter').removeClass('active');
        $(this).addClass('active');

        let firstVisiblePoint = null;
        mapPoints.each(function() {
            const point = $(this);
            const visible = vehicle === 'all' || point.data('types').split(' ').includes(vehicle);
            point.toggleClass('is-hidden', !visible);
            if (visible && !firstVisiblePoint) firstVisiblePoint = point;
        });

        if (firstVisiblePoint) updateAccessPoint(firstVisiblePoint);
    });

    mapPoints.on('click', function() {
        updateAccessPoint($(this));
    });

// Código para o FAQ
    $('.faq-question').on('click', function() {
        const item = $(this).closest('.faq-item');
        const isActive = item.hasClass('active');

        $('.faq-item').removeClass('active');

        if (!isActive) {
            item.addClass('active');
        }
    });
//modal de contato
    $('#open-contact-modal').on('click', function(e) {
        e.preventDefault();
        $('#contact-modal').addClass('active');
    });

    $('#close-contact-modal').on('click', function() {
        $('#contact-modal').removeClass('active');
    });

    // Fecha ao clicar fora da caixa (no overlay escuro)
    $('#contact-modal').on('click', function(e) {
        if ($(e.target).is('#contact-modal')) {
            $('#contact-modal').removeClass('active');
        }
    });

    // Fecha com a tecla ESC
    $(document).on('keydown', function(e) {
        if (e.key === 'Escape') {
            $('#contact-modal').removeClass('active');
        }
    });

    // Impede o formulário de recarregar a página ao enviar (sem backend ainda)
    $('#contact-form').on('submit', function(e) {
        e.preventDefault();
        alert('Mensagem enviada! (isso é só um placeholder — ainda não há backend configurado)');
        $('#contact-modal').removeClass('active');
    });

        // Redireciona todos os botões "Alugar Agora" (.btn-default) e o CTA final para o quiz
    $('.btn-default:not(.btn-login), .btn-cta-final').on('click', function() {
        window.location.href = 'quiz.html';
    });
});