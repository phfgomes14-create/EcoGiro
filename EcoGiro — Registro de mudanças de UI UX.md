# EcoGiro — Registro de mudanças de UI/UX

Resumo das alterações de front-end do projeto, organizado por área.

## 1. Visão geral

- **Escopo:** apenas front-end (HTML, CSS e JavaScript estáticos). Nenhuma alteração de rotas, controllers ou regras do Spring Security.
- **Principais padrões definidos:**
  - Marca: "Eco" em azul e "Giro" em verde, em todos os headers.
  - Destaque de navegação: pílula azul levemente translúcida (active) e hover azul translúcido.
  - Botões com cantos arredondados de 10 px em todo o projeto.
  - Área de usuário em verde e área de administrador em azul nas telas de login e cadastro.

## 2. Header da landing (`index.html`)

- Header alinhado ao padrão de `usuario.html`: menu único, com botão mobile acessível (atributos ARIA).
- Sublinhado dos links e do logo removido (a home não carregava o reset `a { text-decoration: none; }`).
- **Active por rolagem (scroll spy):** o link da seção visível recebe `.active` e `aria-current="location"`. Também funciona com links diretos (ex.: `#faq`) e mantém ativo o link da última seção ao chegar ao fim da página (inicialmente o FAQ; depois da inclusão de Soluções, ela passou a ser a última).
- Active em azul translúcido com contorno sutil; hover dos links comuns em azul translúcido, mais leve que o active.
- Botão "Fazer login" renomeado para "Login".
- Espaço entre o último link do menu e o botão Login definido em 20 px (versão mobile inalterada).
- Novo item no header para a seção `#publico` (antes da footer). Rótulos testados: "Para quem é", "Quem se move" e, por fim, **"Soluções"** (aprovado).
- Etiqueta da seção alterada para "// Para quem é?".

## 3. Marca e logo (todas as páginas)

- `icone.svg` recolorido: 83 tons antigos harmonizados em 6 tons da paleta (desenho preservado).
- Texto "EcoGiro" padronizado em todos os headers, incluindo a tela 403: **"Eco" azul original da paleta** e **"Giro" verde**.
- Testes descartados: ambos em verde e "Eco" em azul-marinho escuro (`--dark`), revertido.
- Logo nas telas de login/cadastro reduzido para 38 px (texto 1,25 rem, peso 800), igual ao header principal; adicionados hover e foco de teclado no link do logo.

## 4. Seções da landing

### Veículos

- Os quatro botões "Reservar" dos cartões agora levam a `planos-aluguel.html`. A página continua protegida pelo Spring Security (somente `USER`).
- Menu de seleção de veículo (Moto, Bicicleta, Bicicleta Elétrica, Patinete Elétrico): active em pílula azul translúcida, igual ao header, sem o sublinhado antigo; hover e foco alinhados.
- Fotos padronizadas: todos os cartões usam o mesmo quadro 4:3 com `object-fit: cover`, eliminando a mudança de tamanho do bloco entre veículos.

### Disponibilidade

- Título, filtros e mapa alinhados à esquerda, com recuo lateral de 8% a partir de 1024 px (igual a Veículos).
- Filtros do mapa em pílula **verde** translúcida (exceção deliberada ao azul do resto do site). Removido o deslocamento de 1 px que cortava o topo das pílulas.
- Ícones: moto e bicicleta mantêm os ícones Font Awesome originais; o patinete usa um pictograma SVG monocromático de 14 px (`fa-scooter` exige Font Awesome Pro). Tentativas com emojis coloridos e com silhuetas pretas foram descartadas.

### FAQ e contato

- Perguntas do FAQ e botão "Falar com suporte": hover azul translúcido e foco visível.
- Botão "Enviar mensagem" em azul `#0e6a8f` (contraste 6,04:1 com texto branco), com hover mais escuro, elevação e sombra.
- Campo "Assunto": o `<select>` nativo foi substituído por uma **lista personalizada acessível** (hover azul translúcido, selecionada em azul sólido, navegação por setas, Enter, Espaço, Escape e Tab). O valor continua enviado com `name="assunto"`.

### CTAs

- "Alugar Agora" (hero): fonte Poppins herdada e `font-weight: 700` (antes herdava peso normal); hover com verde escuro, elevação sutil e foco visível.
- "Encontrar meu veículo": hover com elevação 1 px e sombra, foco visível. Texto "Sem cadastro para cotação" removido.
- Quiz (`quiz.html`): "Reservar agora" e "Refazer" com elevação, sombra e foco; hover do "Refazer" em azul translúcido.

## 5. Botões (padronização global)

- Raio único de 10 px em botões e links com aparência de botão: CTAs, formulários, filtros, abas, FAQ, quiz, Login e Sair.
- Exceções: marcadores circulares do mapa. Cards, ícones e campos não foram alterados.

## 6. Login e cadastro (usuário × administrador)

- `<body>` com `data-area="usuario"` (login e cadastro) e `data-area="admin"` (admin-login e admin-cadastro).
- Tokens `--accent` e `--accent-soft` definidos em `login.css` (compartilhado pelas quatro telas): verde para usuário, azul `#0e6a8f` para admin. Aplicados em abas, selo "ÁREA RESTRITA", ícones, foco dos campos, links, hover e botão principal. Transições de 0,25 s.
- `register.css` não foi alterado (herda de `login.css`).

### Incidente e correção

- A primeira tentativa importou `style.css` (CSS da landing) nas telas de login e quebrou a formatação da landing, pois o reset do `<body>` foi alterado.
- Correção: `style.css` restaurado ao original, import removido dos formulários e tokens movidos para `login.css`.
- Parte das alterações de CSS não tinha sido salva e foi reaplicada em etapas menores, com conferência a cada uma.

## 7. Pontos de atenção para o back end

- Após o login, o sistema redireciona sempre para "Minha Conta", e não de volta à seleção de planos. Se o fluxo "Reservar → login → planos" for desejado, isso exige ajuste no back end.
- A tela de planos lista planos de todas as categorias; a categoria do veículo escolhido não é repassada nem usada como filtro.
- O mapa acessado pelo administrador (`mapa.html`) exibe o header da área de usuário (Minha conta, Planos, Mapa, Recomendação) em vez do da área restrita (Administração, Mapa, Site, Sair). Como o mapa do administrador e o do usuário terão funcionalidades diferentes, a decisão é criar uma página de mapa própria para o admin, com a rota restrita a esse perfil nas regras do Spring Security, assim como os endpoints que ela consumir.

## 8. Arquivos alterados

| Arquivo | Alterações |
| --- | --- |
| `index.html` | header, links dos veículos, remoção de texto, lista de assunto, item de navegação, etiqueta da seção, ícone do patinete |
| `header-home.css` | header, active, hover, espaçamento, marca |
| `script.js` | menu mobile, scroll spy, lista personalizada de assunto |
| `veiculos-style.css` | botões de plano, pílula do menu, imagens 4:3 |
| `disponibilidade.css` | alinhamento, recuo, pílulas verdes, ícones |
| `faq.css` | hover, foco, azul no assunto e no envio |
| `dashboard.css`, `style-quiz.css` | marca, active, raio de botões, hover do quiz |
| `login.css`, `style.css` | tema por área, logo, reset da landing restaurado |
| `login.html`, `cadastro.html`, `admin-login.html`, `admin-cadastro.html` | atributo `data-area` |
| `403.html` | cores da marca |
| `icone.svg` | recoloração com a paleta |

## 9. Paleta e tokens usados

- Azul: `#0c9bc9` (marca, active e hover em versão translúcida)
- Verde: `#2da44f` (marca e área de usuário)
- Verde escuro: `#16863d` (hover do CTA)
- Azul administrativo: `#0e6a8f` (contraste 6,04:1 com texto branco)
- Azul-marinho `--dark`: `#132b3b` (footer)
