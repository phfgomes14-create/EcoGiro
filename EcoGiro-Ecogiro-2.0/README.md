# EcoGiro

## Identificação Acadêmica

| Informação | Detalhe |
|---|---|
| **Instituição de Ensino** | CENTRO UNIVERSITÁRIO DO PLANALTO CENTRAL APPARECIDO DOS SANTOS |
| **Curso** | Análise e Desenvolvimento de Sistemas |
| **Disciplina** | Projeto Integrado em Programação WEB|
| **Orientador** | Profº Hudson Neves |

## Descrição

O EcoGiro é um projeto de sistema voltado à mobilidade sustentável urbana. A aplicação apresenta opções de veículos para aluguel, como motocicletas, bicicletas, bicicletas elétricas e patinetes elétricos, além de disponibilizar um motor de recomendação baseado nas respostas do usuário.

## Objetivos

### Objetivo geral

Oferecer uma experiência digital para consulta de opções de mobilidade urbana e recomendação de veículo de acordo com o perfil e as necessidades do usuário.

### Problema que o sistema resolve

O sistema auxilia pessoas que precisam escolher uma alternativa de transporte urbano sem necessariamente comprar um veículo, considerando fatores como finalidade de uso, distância, habilitação, esforço físico e orçamento.

### Público-alvo

Pessoas que buscam alternativas de mobilidade urbana por meio do aluguel de bicicletas, bicicletas elétricas, patinetes elétricos ou motocicletas.

## Funcionalidades

- Apresentação dos benefícios da mobilidade sustentável.
- Exibição da frota disponível:
  - Motocicleta elétrica.
  - Bicicleta.
  - Bicicleta elétrica.
  - Patinete elétrico.
- Consulta de informações dos veículos, incluindo descrição, especificações e preço diário apresentado na interface.
- Quiz de recomendação de veículo.
- Coleta de respostas sobre:
  - Finalidade principal do uso.
  - Distância média percorrida.
  - Habilitação.
  - Esforço físico desejado.
  - Orçamento aproximado.
- Cálculo de um ranking de perfis de veículos com base nas respostas do quiz.
- Exibição da recomendação principal, alternativa considerada e respostas fornecidas.
- Navegação responsiva para a página principal e para o quiz.

## Tecnologias Utilizadas

- Java 21.
- Spring Boot 4.1.1.
- Gradle.
- HTML.
- CSS.
- JavaScript.
- H2 Database.

## Frameworks e Bibliotecas

- Spring Boot Starter Web MVC.
- Spring Data JPA.
- Spring Boot H2 Console.
- jQuery, utilizado na página principal.
- Font Awesome, utilizado para ícones na página principal.
- Google Fonts, utilizado para a fonte da interface.

## Arquitetura da Solução

A aplicação utiliza uma arquitetura baseada em Spring Boot. O ponto de entrada do backend é a classe `EcogiroApplication`, responsável por inicializar a aplicação.

A interface web é composta por arquivos estáticos disponibilizados no diretório público do projeto. A página principal concentra a apresentação do serviço e da frota, enquanto a página de quiz implementa o fluxo de perguntas e o cálculo da recomendação no navegador.

A estrutura de persistência está preparada no Gradle com Spring Data JPA e H2 Database. Não foram identificadas entidades, repositórios ou fluxos de persistência implementados no código disponível.

## Modelagem do Banco de Dados

O projeto possui a dependência do H2 Database e do Spring Data JPA configuradas. Não foi identificada uma modelagem de banco de dados implementada no código disponível.

- Banco configurado: H2 Database.
- Entidades identificadas: A ser definido pela equipe.
- Relacionamentos: A ser definido pela equipe.
- Diagrama entidade-relacionamento: A ser definido pela equipe.

## Pré-requisitos

- Java Development Kit (JDK) 21 ou superior.
- Gradle Wrapper incluído no projeto (`gradlew` ou `gradlew.bat`).
- Sistema operacional compatível com a execução do Gradle Wrapper.

## Instalação

1. Clone ou copie este repositório para o ambiente local.
2. Acesse a pasta raiz do projeto.
3. Verifique se o JDK 21 está instalado e disponível no `PATH`.
4. No Windows, conceda permissão de execução ao wrapper caso necessário.
5. Baixe as dependências executando o comando correspondente ao seu sistema operacional:

```powershell
.\gradlew.bat build
```

Em sistemas Unix-like:

```bash
./gradlew build
```

## Como Executar

No Windows:

```powershell
.\gradlew.bat bootRun
```

Em sistemas Unix-like:

```bash
./gradlew bootRun
```

A porta de execução configurada: A ser definido pela equipe.

## Estrutura do Projeto

```text
.
├── build.gradle
├── gradlew
├── gradlew.bat
├── settings.gradle
├── gradle/
│   └── wrapper/
└── src/
    ├── main/
    │   ├── java/
    │   │   └── com/example/ecogiro/
    │   │       └── EcogiroApplication.java
    │   └── resources/
    │       ├── application.properties
    │       └── static/
    │           ├── index.htm
    │           ├── quiz.html
    │           └── morecodes/
    │               ├── css/
    │               ├── img/
    │               └── js/
    └── test/
        └── java/
            └── com/example/ecogiro/
                └── EcogiroApplicationTests.java
```

## Exemplos de Uso

### Consultar a frota

1. Inicie a aplicação.
2. Acesse a página principal.
3. Navegue até a seção de veículos.
4. Selecione uma categoria para consultar suas informações.

### Obter uma recomendação

1. Acesse a página do quiz pelo fluxo disponível na interface.
2. Responda às cinco perguntas apresentadas.
3. Consulte o veículo recomendado.
4. Analise a alternativa considerada e as respostas registradas.

## API

Não foram identificados endpoints REST ou uma API pública implementada no código disponível.

- Endpoints: A ser definido pela equipe.
- Autenticação: A ser definido pela equipe.
- Documentação OpenAPI/Swagger: A ser definido pela equipe.

## Capturas de Tela

Adicionar nesta seção as imagens da aplicação, por exemplo:

- `docs/screenshots/pagina-inicial.png`: página inicial e apresentação da frota.
- `docs/screenshots/quiz.png`: tela de perguntas do quiz.
- `docs/screenshots/recomendacao.png`: resultado da recomendação.

Os arquivos de captura de tela: A ser definido pela equipe.

## Equipe do Projeto

| Integrante | Responsabilidade |
| Integrante | Responsabilidade |
| Integrante | Responsabilidade |
| Integrante | Responsabilidade |
| Integrante | Responsabilidade |
| Integrante | Responsabilidade |
| Integrante | Responsabilidade |
| Integrante | Responsabilidade |
|---|---|
| A ser definido pela equipe | A ser definido pela equipe |

## Melhorias Futuras

- Ecopoints: Mapa com lugares disponiveis para a retirada dos veiculos.
- Pagamento via cartões.

## Status do Projeto

Em desenvolvimento.

## Licença

A ser definido pela equipe.
