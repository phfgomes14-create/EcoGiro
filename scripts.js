/* =====================================================
   ECOGIRO
   SCRIPT.JS
===================================================== */


/* =====================================================
   MENU MOBILE
===================================================== */

const menuBtn = document.getElementById("menuBtn");
const nav = document.getElementById("nav");

menuBtn.addEventListener("click", () => {
    nav.classList.toggle("active");
});


/* Fecha o menu quando clicar em algum link */

document.querySelectorAll(".nav a").forEach(link => {

    link.addEventListener("click", () => {
        nav.classList.remove("active");
    });

});


/* =====================================================
   FAQ
===================================================== */

const faqQuestions = document.querySelectorAll(".faq-question");

faqQuestions.forEach(question => {

    question.addEventListener("click", () => {

        const currentItem = question.parentElement;

        document.querySelectorAll(".faq-item").forEach(item => {

            if (item !== currentItem) {
                item.classList.remove("open");
            }

        });

        currentItem.classList.toggle("open");

    });

});


/* =====================================================
   BOTÕES DE VEÍCULOS
===================================================== */

const vehicleButtons = document.querySelectorAll(".vehicle-btn");

vehicleButtons.forEach(button => {

    button.addEventListener("click", () => {

        const vehicle = button.dataset.vehicle;

        alert(
            `Você escolheu: ${vehicle}\n\n` +
            `Agora você pode continuar para escolher o plano ideal.`
        );

        document
            .getElementById("planos")
            .scrollIntoView({
                behavior: "smooth"
            });

    });

});


/* =====================================================
   BOTÕES DE PLANOS
===================================================== */

const planButtons = document.querySelectorAll(".plan-btn");

planButtons.forEach(button => {

    button.addEventListener("click", () => {

        const plan = button.dataset.plan;

        alert(
            `Plano selecionado: ${plan}\n\n` +
            `Preencha o formulário de contato para solicitar mais informações.`
        );

        document
            .getElementById("contato")
            .scrollIntoView({
                behavior: "smooth"
            });

    });

});


/* =====================================================
   MODAL DE RECOMENDAÇÃO
===================================================== */

const modal = document.getElementById("modal");
const modalClose = document.getElementById("modalClose");

const recommendationBtn =
    document.getElementById("recommendationBtn");

const generateRecommendation =
    document.getElementById("generateRecommendation");

const recommendationResult =
    document.getElementById("recommendationResult");


/* Abrir modal */

recommendationBtn.addEventListener("click", () => {

    modal.classList.add("active");

});


/* Fechar modal */

modalClose.addEventListener("click", () => {

    modal.classList.remove("active");

});


/* Fechar clicando fora */

modal.addEventListener("click", (event) => {

    if (event.target === modal) {

        modal.classList.remove("active");

    }

});


/* =====================================================
   SISTEMA DE RECOMENDAÇÃO
===================================================== */

generateRecommendation.addEventListener("click", () => {

    const distance =
        document.getElementById("distance").value;

    const cnh =
        document.getElementById("cnh").value;


    if (!distance || !cnh) {

        recommendationResult.style.display = "block";

        recommendationResult.innerHTML =
            "⚠️ Selecione todas as opções para receber uma recomendação.";

        return;

    }


    let vehicle = "";
    let plan = "";
    let explanation = "";


    /*
        Até 5 km
    */

    if (distance === "curta") {

        vehicle = "Bicicleta";
        plan = "Flex Semanal";

        explanation =
            "Excelente para trajetos curtos, econômicos " +
            "e sustentáveis.";

    }


    /*
        5 a 15 km
    */

    else if (distance === "media") {

        vehicle = "Bicicleta Elétrica";
        plan = "Urbano Mensal";

        explanation =
            "Uma boa opção para percursos médios " +
            "com mais conforto.";

    }


    /*
        Mais de 15 km
    */

    else {

        if (cnh === "sim") {

            vehicle = "Moto";
            plan = "Urbano Mensal";

            explanation =
                "Para distâncias maiores, a moto oferece " +
                "mais alcance e praticidade.";

        } else {

            vehicle = "Bicicleta Elétrica";
            plan = "Urbano Mensal";

            explanation =
                "Como você não possui CNH, uma bicicleta " +
                "elétrica pode ser uma alternativa adequada.";

        }

    }


    recommendationResult.style.display = "block";

    recommendationResult.innerHTML = `
        <strong>💡 Nossa recomendação:</strong><br><br>

        <strong>Veículo:</strong> ${vehicle}<br>

        <strong>Plano sugerido:</strong> ${plan}<br><br>

        ${explanation}
    `;

});


/* =====================================================
   FORMULÁRIO DE CONTATO
===================================================== */

const contactForm =
    document.getElementById("contactForm");

contactForm.addEventListener("submit", (event) => {

    event.preventDefault();


    const nome =
        document.getElementById("nome").value;


    const email =
        document.getElementById("email").value;


    const assunto =
        document.getElementById("assunto").value;


    const mensagem =
        document.getElementById("mensagem").value;


    if (!nome || !email || !mensagem) {

        alert(
            "Por favor, preencha os campos obrigatórios."
        );

        return;

    }


    alert(
        `Mensagem enviada com sucesso, ${nome}!\n\n` +
        `E-mail: ${email}\n` +
        `Assunto: ${assunto || "Não informado"}`
    );


    contactForm.reset();

});


/* =====================================================
   MÁSCARA DE TELEFONE
===================================================== */

const telefone =
    document.getElementById("telefone");

telefone.addEventListener("input", () => {

    let value = telefone.value
        .replace(/\D/g, "")
        .substring(0, 11);


    if (value.length <= 10) {

        value = value.replace(
            /^(\d{2})(\d{4})(\d{0,4})/,
            "($1) $2-$3"
        );

    } else {

        value = value.replace(
            /^(\d{2})(\d{5})(\d{0,4})/,
            "($1) $2-$3"
        );

    }


    telefone.value = value;

});


/* =====================================================
   LINK ATIVO DO MENU
===================================================== */

const sections =
    document.querySelectorAll("section[id]");

const navLinks =
    document.querySelectorAll(".nav a");


window.addEventListener("scroll", () => {

    let current = "";

    sections.forEach(section => {

        const sectionTop =
            section.offsetTop - 100;

        const sectionHeight =
            section.clientHeight;

        if (
            window.scrollY >= sectionTop &&
            window.scrollY < sectionTop + sectionHeight
        ) {

            current = section.getAttribute("id");

        }

    });


    navLinks.forEach(link => {

        link.classList.remove("active");

        if (
            link.getAttribute("href") ===
            `#${current}`
        ) {

            link.classList.add("active");

        }

    });

});


/* =====================================================
   ANIMAÇÃO DE ENTRADA DOS CARDS
===================================================== */

const animatedElements =
    document.querySelectorAll(
        ".benefit-card, .vehicle-card, .plan-card, .audience-card"
    );


const observer =
    new IntersectionObserver(
        entries => {

            entries.forEach(entry => {

                if (entry.isIntersecting) {

                    entry.target.style.opacity = "1";

                    entry.target.style.transform =
                        "translateY(0)";

                }

            });

        },
        {
            threshold: 0.1
        }
    );


animatedElements.forEach(element => {

    element.style.opacity = "0";

    element.style.transform =
        "translateY(20px)";

    element.style.transition =
        "opacity 0.5s ease, transform 0.5s ease";

    observer.observe(element);

});