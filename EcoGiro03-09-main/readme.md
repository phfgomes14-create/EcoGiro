# EcoGiro - Mobilidade Sustentável Urbana

## Identificação Acadêmica

* **Instituição de Ensino:** A ser definido pela equipe
* **Curso:** A ser definido pela equipe
* **Disciplina:** A ser definido pela equipe
* **Orientador:** Profº Hudson Neves

---

## Descrição

O **EcoGiro** é uma plataforma digital de mobilidade sustentável que conecta usuários a opções flexíveis de transporte alternativo urbano, como bicicletas convencionais, bicicletas elétricas (e-bikes), patinetes elétricos e motocicletas. O sistema conta com um motor de recomendação inteligente que auxilia o usuário a escolher o veículo ideal com base em seu perfil de deslocamento e restrições operacionais.

---

## Objetivos

### Objetivo Geral
Desenvolver uma solução full stack para recomendar e facilitar o acesso a modalidades de transporte sustentável, promovendo escolhas mais conscientes e eficientes para o deslocamento urbano.

### Problemas que o sistema resolve
* **Dificuldade na escolha do transporte ideal:** Dúvidas do usuário em relação à autonomia, necessidade de CNH, custos e esforço físico necessário para cada tipo de veículo.
* **Barreira de entrada para uso de veículos ecológicos:** Falta de clareza sobre planos de contratação e adequação ao uso diário ou profissional.

---

## Funcionalidades

* **Landing Page Institucional:** Apresentação da plataforma, catálogo de frotas, tabela de planos, benefícios e seção de dúvidas frequentes (FAQ).
* **Motor de Recomendação (Quiz de Mobilidade):** Formulário interativo que processa dados de distância, nível de esforço desejado, finalidade de uso, posse de CNH e orçamento.
* **Algoritmo de Recomendação no Backend:** Processamento dos dados no Java/Spring Boot para indicar o veículo principal e uma alternativa secundária.
* **Persistência de Histórico:** Salvamento das respostas do formulário e das recomendações geradas no banco de dados.
* **Modal de Contato/Suporte:** Interface para envio de dúvidas e mensagens de suporte.

---

## Tecnologias Utilizadas

### Front-end
* HTML5
* CSS3
* JavaScript (ES6+)
* jQuery (v4.0.0)
* FontAwesome (v7.3.1)

### Back-end
* Java
* Spring Boot (Spring Web, Spring Data JPA)

### Banco de Dados
* H2 Database (Banco em memória)

---

## Arquitetura da Solução

O sistema adota uma arquitetura em camadas baseada no padrão REST/MVC:

1. **Camada de Apresentação (Front-end):** Interface em HTML/CSS/JS que faz requisições assíncronas (`fetch` API) enviando dados em formato JSON.
2. **Camada de Controle (Controller):** Endpoints REST em Spring Boot (`RecomendacaoController`) que recebem as requisições HTTP e validam os DTOs.
3. **Camada de Negócio (Service):** Regras de negócio da recomendação (`RecomendacaoService`) que calculam as escolhas de veículos.
4. **Camada de Persistência (Repository & Entity):** Mapeamento objeto-relacional (JPA/Hibernate) para armazenamento da entidade `Recomendacao` na tabela `TB_RECOMENDACAO`.

---

## Modelagem do Banco de Dados

### Tabela: `TB_RECOMENDACAO`

| Coluna | Tipo de Dado | Descrição |
| :--- | :--- | :--- |
| `ID` | BIGINT (PK, Auto-increment) | Identificador único do registro |
| `DISTANCIA` | VARCHAR | Faixa de distância informada |
| `ESFORCO` | VARCHAR | Preferência de esforço físico / ajuda elétrica |
| `FINALIDADE` | VARCHAR | Propósito de uso do veículo |
| `HABILITACAO` | VARCHAR | Situação do usuário em relação à CNH |
| `ORCAMENTO` | VARCHAR | Faixa de orçamento disponível |
| `RECOMENDACAO_PRINCIPAL` | VARCHAR | Veículo principal recomendado pelo sistema |
| `RECOMENDACAO_ALTERNATIVA` | VARCHAR | Segunda opção de veículo recomendada |

---

## Pré-requisitos

Para executar o projeto, é necessário ter instalado na máquina:

* **Java JDK:** Versão 17 ou superior
* **Apache Maven:** Versão 3.8+ (ou utilizar o wrapper `./mvnw`)
* **Navegador Web:** Google Chrome, Mozilla Firefox, Microsoft Edge ou similar

---

## Instalação

1. Clone o repositório para o seu ambiente local:
`git clone https://github.com/usuario/ecogiro.git`

2. Acesse a pasta do projeto:
`cd ecogiro`

3. Compile o projeto e baixe as dependências do Maven:
`mvn clean install`

---

## Como Executar

1. Inicie a aplicação Spring Boot através da sua IDE de preferência ou pelo terminal:
`mvn spring-boot:run`

2. Confirme se a aplicação subiu com sucesso na porta `8080` (mensagem no console: `Tomcat started on port(s): 8080`).

3. Acesse a aplicação no navegador:
* **Página Inicial:** `http://localhost:8080/index.html` (ou via Live Server / servidor estático)
* **Quiz de Recomendação:** `http://localhost:8080/quiz.html`
* **Console do Banco H2:** `http://localhost:8080/h2-console`

### Configurações de Acesso ao H2:
* **JDBC URL:** `jdbc:h2:mem:ecogirodb` (ou conforme configurado no `application.properties`)
* **User Name:** `sa`
* **Password:** *(deixe em branco)*

---

## Estrutura do Projeto

ecogiro/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── ecogiro/
│   │   │           ├── controller/
│   │   │           ├── dto/
│   │   │           ├── model/
│   │   │           ├── repository/
│   │   │           └── service/
│   │   └── resources/
│   │       ├── static/
│   │       │   ├── css/
│   │       │   ├── img/
│   │       │   ├── js/
│   │       │   ├── index.html
│   │       │   └── quiz.html
│   │       └── application.properties
└── pom.xml

---

## Exemplos de Uso

### Payload de Envio do Quiz (`POST /recomendacao`)

{
  "distancia": "Média — 3 a 10 km",
  "esforco": "Pouco — gosto de ajuda elétrica",
  "finalidade": "Lazer e passeios",
  "habilitacao": "Não tenho e não pretendo tirar",
  "orcamento": "Médio"
}

### Resposta do Sistema (`200 OK`)

{
  "recomendacaoPrincipal": "E-bike",
  "recomendacaoAlternativa": "Bicicleta"
}

---

## API

### Endpoints Disponíveis

#### Criar Recomendação
* **URL:** `/recomendacao`
* **Método:** `POST`
* **Consumes:** `application/json`
* **Produces:** `application/json`
* **Descrição:** Recebe os parâmetros preenchidos no quiz, calcula a recomendação ideal, salva a entidade no banco de dados H2 e devolve os veículos indicados.

---

## Capturas de Tela

* **Tela Principal (Landing Page):** `[ Inserir imagem da landing page aqui ]`
* **Quiz de Recomendação:** `[ Inserir imagem da tela do quiz aqui ]`
* **Banco de Dados H2:** `[ Inserir imagem do H2 Console com os registros gravados aqui ]`

---

## Equipe do Projeto

* **Integrantes:** A ser definido pela equipe

---

## Melhorias Futuras

* Integração completa do formulário de contato/suporte com envio de e-mails ou salvamento em banco de dados.
* Implementação de autenticação e cadastro de usuários para histórico individual de recomendações.
* Integração com APIs externas de geolocalização para mapeamento dos pontos de retirada de veículos.
* Mapeamento de persistência em banco de dados relacional permanente (PostgreSQL/MySQL) para ambiente de produção.

---

## Licença

A ser definido pela equipe