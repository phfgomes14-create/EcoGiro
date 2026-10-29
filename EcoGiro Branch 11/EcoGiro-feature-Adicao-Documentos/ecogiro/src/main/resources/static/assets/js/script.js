$(document).ready(function() {
// Código para o menu mobile
    $('#mobile_btn').on('click', function() {
        const isExpanded = $(this).attr('aria-expanded') === 'true';
        $(this).attr('aria-expanded', String(!isExpanded));
        $(this).attr('aria-label', isExpanded ? 'Abrir menu' : 'Fechar menu');
        $('#primary-navigation').toggleClass('is-open', !isExpanded);
        $(this).find('i').toggleClass('fa-bars fa-xmark');
    });

    $('#primary-navigation a').on('click', function() {
        $('#primary-navigation').removeClass('is-open');
        $('#mobile_btn').attr('aria-expanded', 'false').attr('aria-label', 'Abrir menu');
        $('#mobile_btn').find('i').removeClass('fa-xmark').addClass('fa-bars');
    });

    const sectionLinks = $('#primary-navigation a[href^="#"]');
    const updateActiveSection = function() {
        const headerHeight = $('.app-header').outerHeight() || 0;
        const readingPosition = window.scrollY + headerHeight + window.innerHeight * 0.25;
        let activeLink = sectionLinks.first();

        sectionLinks.each(function() {
            const section = document.querySelector(this.getAttribute('href'));
            if (section && section.getBoundingClientRect().top + window.scrollY <= readingPosition) {
                activeLink = $(this);
            }
        });

        if (window.innerHeight + window.scrollY >= document.documentElement.scrollHeight - 2) {
            activeLink = sectionLinks.last();
        }

        sectionLinks.removeClass('active').removeAttr('aria-current');
        activeLink.addClass('active').attr('aria-current', 'location');
    };

    let scrollFrame = null;
    $(window).on('scroll resize', function() {
        if (scrollFrame !== null) return;
        scrollFrame = window.requestAnimationFrame(function() {
            updateActiveSection();
            scrollFrame = null;
        });
    });
    updateActiveSection();

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

    const subjectSelect = $('#subject-select');
    if (subjectSelect.length) {
        const subjectTrigger = $('#assunto-trigger');
        const subjectValue = $('#assunto-value');
        const subjectInput = $('#assunto');
        const subjectOptions = $('#assunto-options');
        const subjectItems = subjectOptions.find('[role="option"]');
        let activeSubjectIndex = 0;

        const setActiveSubjectOption = function(index) {
            activeSubjectIndex = (index + subjectItems.length) % subjectItems.length;
            const option = subjectItems.eq(activeSubjectIndex);
            subjectItems.removeClass('is-active');
            option.addClass('is-active');
            subjectTrigger.attr('aria-activedescendant', option.attr('id'));
            option[0].scrollIntoView({ block: 'nearest' });
        };

        const closeSubjectOptions = function() {
            subjectOptions.prop('hidden', true);
            subjectTrigger.attr('aria-expanded', 'false').removeAttr('aria-activedescendant');
            subjectItems.removeClass('is-active');
        };

        const openSubjectOptions = function() {
            subjectOptions.prop('hidden', false);
            subjectTrigger.attr('aria-expanded', 'true');
            const selectedIndex = subjectItems.index(subjectItems.filter('[aria-selected="true"]').first());
            setActiveSubjectOption(selectedIndex >= 0 ? selectedIndex : 0);
        };

        const selectSubjectOption = function(option) {
            subjectInput.val(option.attr('data-value'));
            subjectValue.text(option.text());
            subjectItems.attr('aria-selected', 'false').removeClass('is-active');
            option.attr('aria-selected', 'true');
            closeSubjectOptions();
            subjectTrigger.focus();
        };

        subjectTrigger.on('click', function() {
            if (subjectTrigger.attr('aria-expanded') === 'true') closeSubjectOptions();
            else openSubjectOptions();
        });

        subjectTrigger.on('keydown', function(event) {
            const isExpanded = subjectTrigger.attr('aria-expanded') === 'true';
            if (event.key === 'ArrowDown' || event.key === 'ArrowUp') {
                event.preventDefault();
                if (!isExpanded) openSubjectOptions();
                else setActiveSubjectOption(activeSubjectIndex + (event.key === 'ArrowDown' ? 1 : -1));
            } else if (event.key === 'Home' || event.key === 'End') {
                event.preventDefault();
                if (!isExpanded) openSubjectOptions();
                setActiveSubjectOption(event.key === 'Home' ? 0 : subjectItems.length - 1);
            } else if (event.key === 'Enter' || event.key === ' ') {
                event.preventDefault();
                if (!isExpanded) openSubjectOptions();
                else selectSubjectOption(subjectItems.eq(activeSubjectIndex));
            } else if (event.key === 'Escape' && isExpanded) {
                event.preventDefault();
                closeSubjectOptions();
            } else if (event.key === 'Tab' && isExpanded) {
                closeSubjectOptions();
            }
        });

        subjectOptions.on('mousemove', '[role="option"]', function() {
            setActiveSubjectOption(subjectItems.index(this));
        });

        subjectOptions.on('click', '[role="option"]', function() {
            selectSubjectOption($(this));
        });

        $(document).on('click', function(event) {
            if (subjectSelect.length && !subjectSelect[0].contains(event.target)) closeSubjectOptions();
        });
    }

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