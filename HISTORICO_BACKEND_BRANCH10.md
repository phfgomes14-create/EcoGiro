# EcoGiro - Registro de Alteracoes do Backend

Este documento registra as implementacoes, correcoes, configuracoes de infraestrutura e validacoes realizadas no backend do projeto EcoGiro.

---

## 1. Backend e arquitetura

O projeto utiliza:

- Java 21
- Spring Boot
- Spring Security
- Spring Data JPA / Hibernate
- Gradle
- PostgreSQL
- Neon como banco PostgreSQL em nuvem
- Render para execucao da aplicacao
- Docker para build e execucao em producao

O backend foi preparado para trabalhar com o perfil de producao e banco PostgreSQL externo.

---

## 2. Banco de dados PostgreSQL / Neon

Foi realizada a integracao da aplicacao com um banco PostgreSQL hospedado no Neon.

Principais entidades/tabelas utilizadas pela aplicacao:

- `tb_usuario`
- `tb_plano_aluguel`
- `tb_solicitacao_plano`
- `tb_assinatura_plano`
- `tb_veiculo`
- `tb_recomendacao`

A conexao em producao utiliza variaveis de ambiente, evitando armazenar credenciais diretamente no codigo-fonte.

Foram configuradas variaveis para:

- URL do banco
- usuario do banco
- senha do banco
- datasource do Spring
- perfil de producao

A conexao entre Render e Neon foi validada com sucesso.

---

## 3. Cadastro e autenticacao

Foi implementado o sistema de autenticacao utilizando Spring Security e BCrypt.

Foram criados dois perfis de acesso:

- `USER`
- `ADMIN`

O usuario comum possui fluxo proprio de cadastro e autenticacao.

O administrador possui fluxo administrativo separado.

As senhas sao armazenadas utilizando hash BCrypt.

---

## 4. Separacao entre usuario e administrador

Foi corrigida a autenticacao para impedir que contas administrativas utilizem o login destinado aos usuarios comuns.

Tambem foi mantida a verificacao do fluxo administrativo para impedir que contas comuns obtenham acesso ao painel administrativo.

Comportamentos validados em producao:

- USER -> login de usuario: permitido
- USER -> login de administrador: bloqueado
- ADMIN -> login de administrador: permitido
- ADMIN -> login de usuario: bloqueado

Quando uma conta administrativa tenta utilizar o login comum, a sessao de autenticacao e invalidada e o usuario e direcionado novamente para a pagina de login.

Quando uma conta comum tenta utilizar o acesso administrativo, o sistema informa que a conta nao possui permissao de administrador.

---

## 5. Painel do usuario

Foi implementado painel destinado ao usuario autenticado.

O painel permite visualizar informacoes relacionadas a:

- plano atual
- solicitacoes de planos
- status das solicitacoes
- assinatura ativa
- recomendacoes de mobilidade

---

## 6. Painel administrativo

Foi implementada uma area administrativa restrita.

O painel administrativo permite acompanhar e gerenciar:

- usuarios
- solicitacoes de planos
- planos de aluguel
- assinaturas
- veiculos

O acesso aos endpoints administrativos e protegido pelo papel `ADMIN`.

---

## 7. Planos de aluguel

Foi implementado o fluxo de planos de aluguel.

O usuario pode solicitar um plano.

O administrador pode analisar a solicitacao e:

- aprovar
- rejeitar

Quando uma solicitacao e aprovada, o sistema pode gerar a assinatura correspondente para o usuario.

O status da solicitacao fica disponivel para consulta no painel.

---

## 8. Recomendacao de mobilidade

Foi implementado o registro das respostas do questionario de recomendacao.

As informacoes sao persistidas no banco e utilizadas para apresentar uma recomendacao de meio de transporte ao usuario.

O funcionamento da persistencia das recomendacoes foi validado no PostgreSQL.

---

## 9. Veiculos e mapa

Foi implementada persistencia de veiculos e endpoints utilizados pela funcionalidade de mapa.

A aplicacao possui integracao entre os dados de veiculos armazenados no backend e a interface de mapa.

---

## 10. Deploy no Render

O backend foi publicado como Web Service no Render.

A aplicacao utiliza Docker para realizar o build e iniciar o Spring Boot.

O Render foi configurado para executar o projeto a partir da branch:

`branch10---erros-corrigidos`

O servico foi validado utilizando:

- Spring Boot em producao
- PostgreSQL/Neon
- Hibernate/JPA
- HikariCP
- Tomcat

A aplicacao foi iniciada corretamente no ambiente de producao.

---

## 11. Atualizacao para Java 21

O projeto foi atualizado para utilizar Java 21.

O `build.gradle` passou a utilizar:

`JavaLanguageVersion.of(21)`

A compilacao local foi validada com sucesso utilizando Gradle.

---

## 12. Correcao do Docker para Java 21

Apos a atualizacao do Gradle para Java 21, o primeiro deploy falhou porque o Dockerfile ainda utilizava Java 17.

O ambiente de build utilizava:

`eclipse-temurin:17-jdk`

e o ambiente de execucao utilizava:

`eclipse-temurin:17-jre`

O Dockerfile foi corrigido para:

`eclipse-temurin:21-jdk`

e:

`eclipse-temurin:21-jre`

Apos a correcao, o build foi executado novamente no Render e o deploy foi concluido com sucesso.

Commit relacionado:

`1f40107 - Atualiza Docker para Java 21`

---

## 13. Correcao da mensagem de login

Foi identificada uma incompatibilidade de codificacao ao enviar diretamente um caractere acentuado no parametro da URL de redirecionamento do login.

A mensagem foi ajustada para evitar a exibicao do caractere invalido na interface.

A compilacao foi novamente validada com:

`gradlew compileJava`

Resultado:

`BUILD SUCCESSFUL`

Commit relacionado:

`c088421 - Corrige mensagem do login administrativo`

---

## 14. Testes realizados em producao

Foram realizados testes diretamente na aplicacao publicada no Render.

Validacoes concluidas:

- conexao com PostgreSQL/Neon
- inicializacao do Spring Boot
- persistencia de usuario
- login de usuario
- login administrativo
- bloqueio de ADMIN no login de USER
- bloqueio de USER no login de ADMIN
- acesso ao painel do usuario
- acesso ao painel administrativo
- solicitacao de plano
- aprovacao de solicitacao
- geracao/visualizacao de assinatura
- persistencia de recomendacao
- consulta dos dados administrativos
- carregamento dos veiculos

---

## 15. Seguranca

Medidas aplicadas:

- senhas protegidas com BCrypt
- separacao entre papeis USER e ADMIN
- endpoints administrativos protegidos por role
- credenciais do banco configuradas por variaveis de ambiente
- sessao invalidada quando uma conta administrativa tenta autenticar pelo fluxo comum
- banco PostgreSQL externo utilizado no ambiente de producao

---

## 16. Historico recente de commits

### `4831fb4`
Implementa autenticacao, planos, painel admin, banco e mapa.

### `6a534fc`
Corrige separacao de login usuario e admin e atualiza Java 21.

### `1f40107`
Atualiza Docker para Java 21.

### `c088421`
Corrige mensagem do login administrativo.

---

## Estado atual

O backend EcoGiro encontra-se integrado ao PostgreSQL/Neon e publicado no Render.

Os fluxos de usuario e administrador foram separados e testados em producao.

O projeto utiliza Java 21 tanto na compilacao Gradle quanto no ambiente Docker.

Este documento deve ser atualizado juntamente com novas implementacoes, correcoes relevantes, alteracoes de infraestrutura e testes realizados.

---

## 17. Restricao das rotas por perfil

Durante os testes de autorizacao em producao, foi identificado que uma conta ADMIN autenticada conseguia acessar diretamente a pagina `/usuario.html`.

A causa era a configuracao das rotas de usuario utilizando apenas:

`authenticated()`

Essa regra permitia o acesso de qualquer conta autenticada, independentemente do papel USER ou ADMIN.

A configuracao de seguranca foi alterada para separar as permissoes:

- `/admin.html` e `/api/admin/**`: somente `ADMIN`
- `/usuario.html`, `/planos-aluguel.html` e `/api/user/**`: somente `USER`
- `/mapa.html` e `/api/map/**`: acessiveis por USER e ADMIN autenticados

O mapa permaneceu compartilhado porque faz parte tanto da experiencia do usuario quanto das funcionalidades disponiveis para a administracao.

Testes de seguranca realizados:

- usuario deslogado tentando acessar `/admin.html`: bloqueado
- usuario deslogado tentando acessar `/usuario.html`: bloqueado
- USER autenticado tentando acessar `/admin.html`: bloqueado com HTTP 403
- ADMIN acessando `/usuario.html`: falha identificada antes da correcao

A nova configuracao foi compilada localmente com sucesso utilizando:

`gradlew compileJava`

Resultado:

`BUILD SUCCESSFUL`

Apos o deploy, devera ser validado que ADMIN tambem nao consegue mais acessar diretamente as rotas exclusivas de USER.


### Validacao apos deploy

A correcao de autorizacao foi publicada e validada em producao.

Teste realizado:

- ADMIN autenticado tentando acessar diretamente `/usuario.html`: bloqueado com HTTP 403 (Forbidden)

Com isso, foi confirmado que as rotas exclusivas de USER nao podem mais ser acessadas por contas ADMIN.

A separacao de autorizacao entre USER e ADMIN esta funcionando conforme esperado.

Observacao: atualmente o bloqueio HTTP 403 utiliza a pagina Whitelabel padrao do Spring Boot. A substituicao por uma pagina de acesso negado personalizada fica registrada como melhoria de interface.


---

## 18. Pagina personalizada de acesso negado

Foi criada uma pagina personalizada para respostas HTTP 403 (Forbidden), substituindo a pagina Whitelabel padrao do Spring Boot quando um usuario autenticado tenta acessar uma area para a qual nao possui permissao.

Arquivo criado:

`src/main/resources/static/403.html`

A pagina informa ao usuario que sua conta nao possui permissao para acessar a area solicitada e disponibiliza opcoes para retornar a pagina anterior ou voltar para a pagina inicial do EcoGiro.

O Spring Security foi configurado utilizando:

`exceptionHandling(exception -> exception.accessDeniedPage("/403.html"))`

A rota `/403.html` foi adicionada as rotas publicas para permitir que a pagina de acesso negado seja exibida independentemente do perfil autenticado.

A alteracao nao modifica as regras de autorizacao existentes:

- rotas administrativas continuam exclusivas de `ADMIN`
- rotas de usuario continuam exclusivas de `USER`
- mapa continua disponivel para usuarios autenticados
- acessos sem permissao continuam sendo bloqueados com HTTP 403

A nova configuracao foi compilada localmente com:

`gradlew compileJava`

Resultado:

`BUILD SUCCESSFUL`

Validacao em producao pendente apos deploy.


---

## 19. Integracao Cloudflare Worker, Render e Neon

Foi concluida a integracao do Cloudflare com o backend hospedado no Render.

### Problema identificado

O projeto originalmente publicado no Cloudflare Pages servia apenas os arquivos estaticos do frontend.

Como os formularios e scripts utilizam rotas relativas como `/login`, `/register` e `/api/**`, requisicoes POST realizadas diretamente pelo dominio `pages.dev` eram enviadas ao Cloudflare Pages em vez do backend Spring Boot.

Isso provocava respostas HTTP 405 (Method Not Allowed).

A variavel `BACKEND_URL` existente no Cloudflare Pages nao resolvia o problema automaticamente, pois os arquivos HTML e JavaScript sao publicados diretamente, sem uma etapa de build que injete essa variavel no frontend.

### Solucao implementada

Foi criado o Cloudflare Worker:

`ecogiro-proxy`

O Worker atua como proxy reverso entre o navegador e o backend hospedado no Render.

Fluxo atual:

`Navegador -> Cloudflare Worker -> Render / Spring Boot -> Neon PostgreSQL`

O Worker preserva:

- metodo HTTP
- caminho da requisicao
- query parameters
- headers
- corpo das requisicoes
- cookies e sessao de autenticacao
- redirecionamentos do Spring Security

Tambem foi implementada a reescrita de redirecionamentos absolutos do Render para o dominio atual do Worker, mantendo a navegacao do usuario dentro do endereco Cloudflare.

### Validacao

A integracao foi validada em producao pelo endereco do Cloudflare Worker.

Testes realizados com sucesso:

- carregamento da pagina inicial
- login de usuario via POST
- manutencao da sessao Spring Security
- redirecionamento para `/usuario.html`
- carregamento dos dados da conta
- carregamento do plano ativo
- acesso a `/planos-aluguel.html`
- carregamento dos planos disponiveis
- acesso a `/mapa.html`
- carregamento do mapa Leaflet / OpenStreetMap
- carregamento dos veiculos e suas localizacoes
- comunicacao das APIs com o backend no Render
- persistencia e leitura dos dados pelo PostgreSQL no Neon

O erro HTTP 405 encontrado anteriormente no Cloudflare Pages nao ocorre utilizando o Worker.

### Arquitetura final

Cloudflare Worker:
entrada publica e proxy reverso da aplicacao.

Render:
execucao do backend Java / Spring Boot, Spring Security e APIs.

Neon:
banco de dados PostgreSQL da aplicacao.

Cloudflare Pages:
permanece como publicacao estatica/legada do frontend. Como nao executa o backend Spring Boot, nao deve ser utilizado como endereco principal para os fluxos autenticados.

O endereco funcional da aplicacao atraves da infraestrutura Cloudflare passa a ser o dominio `workers.dev` do Worker `ecogiro-proxy`.


---

## 20. Correcao da navegacao do mapa para administradores

O mapa compartilhado apresentava menu fixo de usuario e apontava para `/usuario.html`, rota exclusiva de USER, causando HTTP 403 ao retornar para a conta administrativa. O menu do mapa passou a consultar `/api/admin/summary` para identificar o perfil autenticado: ADMIN recebe link para `/admin.html`; USER recebe links para `/usuario.html` e `/planos-aluguel.html`. Em erros inesperados os links de conta ficam ocultos. Nao foram alteradas as regras de seguranca do backend. Validacao em producao pendente.

---

## 21. Plano aprovado - central administrativa, lojas e distribuicao inteligente de setores (08/10/2026)

**Estado: requisitos aprovados e documentados; implementacao funcional ainda pendente.**
Nao confundir esta especificacao com recursos ja publicados ou testados.

### Decisoes aprovadas

- O mapa de USER permanece voltado a consulta de veiculos, disponibilidade e rotas.
- O mapa de ADMIN sera uma interface propria de operacao: lojas EcoGiro clicaveis, veiculos associados, disponibilidade, realocacao, destino e trajetos.
- Trajetos que atravessam areas congestionadas devem ser avaliados; se a regra de restricao for acionada, exibir o motivo e propor rota alternativa.
- Congestionamento real exige provedor de dados de trafego; sem esse provedor, a interface deve distinguir simulacao ou informacao desatualizada de condicao verificada em tempo real.
- Setores territoriais terao administradores responsaveis. A distribuicao buscara equilibrio por carga operacional (lojas, frota, demanda e ocorrencias), nao apenas numero de setores.
- A entrada de um administrador novo gera **proposta**, sem redistribuir automaticamente setores existentes.
- Um ADMIN_GERAL podera aprovar, ajustar ou rejeitar a proposta. ADMIN_SETORIAL gerenciara apenas setores designados.
- Desativacao ou ausencia de responsavel exige proposta de redistribuicao; manter historico e auditoria.
- Preservar USER e ADMIN atuais ate haver migracao controlada e autorizada, sem promocao automatica de contas.

### Etapas previstas

1. Modelagem de setores, lojas, vinculos de veiculos, atribuicoes de administradores, propostas e historico de aprovacoes; migracoes de banco seguras e reversiveis.
2. Politica de autorizacao ADMIN_GERAL / ADMIN_SETORIAL com estrategia explicita de bootstrap do primeiro administrador geral e migracao das contas ADMIN existentes.
3. API de setores, capacidade/carga, recomendacoes de redistribuicao e aprovacao transacional com auditoria e verificacao de conflitos.
4. Mapa administrativo dedicado com selecao de loja, gerenciamento de frota e visualizacao dos setores atribuidos; manter o mapa USER isolado.
5. Servico de rotas e restricoes de congestionamento com fonte de dados, limites, fallback e explicacoes das alternativas.
6. Testes de papeis, limites territoriais, concorrencia de aprovacoes, rotas, integridade do banco e regressao de login; deploy controlado Render/Cloudflare/Neon.

### Criterios de aceite

- USER nao visualiza nem executa operacoes administrativas.
- ADMIN_SETORIAL nao modifica setores de outros administradores.
- ADMIN_GERAL consegue revisar e decidir propostas, com registro de quem decidiu e quando.
- Cadastro de novo administrador nao altera distribuicao em vigor sem aprovacao.
- Rotas alternativas sao exibidas com motivo e origem dos dados de congestionamento.
- Nao apagar ou reatribuir contas e veiculos existentes sem migracao validada.
- Registrar no presente documento cada implementacao, commit, teste, deploy e pendencia.

### Comunicacao

Ao final de cada entrega, preparar uma mensagem curta de WhatsApp distinguindo recursos concluidos, recursos pendentes e testes realizados, para compartilhamento com a equipe.

---

## 22. Primeira entrega de modelagem de setores e lojas (08/10/2026)

**Implementado na branch `branch10---erros-corrigidos`:**

- `AdministrativeSector` / `tb_setor_administrativo`: identificador, codigo unico, nome e status.
- `EcoGiroStore` / `tb_loja_ecogiro`: codigo unico, nome, coordenadas, status e referencia obrigatoria a setor.
- `AdministratorSectorAssignment` / `tb_atribuicao_setor`: referencia a conta existente em `TB_USUARIO`, setor e data de atribuicao; impede duplicidade do par administrador/setor.
- Interfaces Spring Data JPA para as tres entidades.

**Limites desta entrega:** nao ha endpoints, interfaces de operacao, atribuicao automatica, auditoria de aprovacao, integracao de veiculos a lojas ou verificacao de perfil ADMIN_GERAL/ADMIN_SETORIAL. As entidades nao alteram as permissoes atuais, nao criam usuarios e nao migram registros existentes. A atribuicao deve ser autorizada pelo backend em etapa posterior.

**Validacoes pendentes:** compilacao Gradle Java 21, migracao do esquema no ambiente de testes, revisao de indices e constraints, integridade referencial e testes de seguranca. Com `spring.jpa.hibernate.ddl-auto=update`, novas tabelas poderao ser criadas quando o backend for implantado; conferir primeiro a configuracao de deploy e fazer backup do banco antes de publicar.

**Proximos passos:** definir ADMIN_GERAL/ADMIN_SETORIAL e estrategia de migracao sem perda de acesso; introduzir propostas de distribuicao com aprovacao auditavel; construir API e interface de mapa administrativo; integrar dados de congestionamento e rotas alternativas.

---

## 23. Preparacao dos perfis administrativos (08/10/2026)

**Implementado no codigo, nao validado por compilacao ou deploy:**
- `Role` inclui `ADMIN_GERAL` e `ADMIN_SETORIAL` e preserva `ADMIN` legado.
- `CustomUserDetailsService` fornece `ROLE_ADMIN` de compatibilidade para contas com perfil administrativo e tambem a autoridade especifica do perfil.
- `SecurityConfig` aceita os tres perfis administrativos nas rotas administrativas existentes.
- Novo cadastro via `/admin/register` passa a gerar `ADMIN_SETORIAL`, nao `ADMIN_GERAL`.
- Contas `ADMIN` existentes continuam com o valor original; nenhuma conta foi promovida ou migrada.

**Limitacoes e seguranca:**
- A permissao `ROLE_ADMIN` de compatibilidade ainda permite a ADMIN_SETORIAL acessar os endpoints administrativos legados sem filtro territorial. Nao publicar como controle setorial finalizado.
- Ainda nao existe fluxo seguro de criacao/promocao de ADMIN_GERAL. Implementar designacao controlada por operador autorizado e auditoria, nunca promocao automatica pelo formulario publico.
- O segredo `ADMIN_REGISTRATION_CODE` precisa ser configurado no Render; retirar o fallback de desenvolvimento antes da abertura publica do cadastro.
- Faltam regras de escopo por setor no service/repository, endpoints de aprovacao, associacao de veiculos a lojas, testes e migracao.
- Compilacao, testes automatizados e deploy **nao executados nesta etapa**.

**Proxima entrega:** impor controle de acesso por setor no backend e proposta de distribuicao sujeita a aprovacao do ADMIN_GERAL. Revisar e testar o login dos perfis antigos antes de deploy.


## 24. Consulta territorial (08/10/2026)

Criado GET /api/admin/sectors. ADMIN_GERAL consulta todos os setores; ADMIN_SETORIAL consulta os setores atribuidos; ADMIN legado recebe 403 nesta nova rota. Endpoint somente leitura. Compilacao, testes e deploy pendentes. Os endpoints administrativos antigos ainda exigem isolamento territorial antes do uso operacional.


## 25. Previa de redistribuicao de setores (08/10/2026)

Criado SectorDistributionPreviewController, endpoint GET /api/admin/sector-distribution/preview. Exige ADMIN_GERAL ativo e devolve sugestoes sem gravar mudancas. Considera administradores setoriais ativos, setores ativos, atribuicoes atuais e setores sem responsavel. A heuristica preliminar equilibra quantidade de setores, nao frota, lojas, demanda ou congestionamento. Nao existe aprovacao ou aplicacao nesta entrega. Sem administrador geral configurado, a rota responde 403; sem administradores setoriais, nao gera transferencias. Compilacao, testes e deploy pendentes. Antes de producao, validar dados inconsistentes (setor atribuido a multiplos administradores), revisao de permissoes legadas e migracao segura.


## 26. Estrutura para propostas de redistribuicao (08/10/2026)

Criados os arquivos SectorRedistributionProposal.java, SectorRedistributionProposalRepository.java e SectorRedistributionQueueController.java.

A entidade guarda setor, administrador de origem (opcional), administrador de destino, solicitante, decisor, justificativa, estado PENDING/APPROVED/REJECTED/CANCELLED e datas de criacao/decisao. GET /api/admin/redistribution-proposals/pending lista propostas pendentes somente para ADMIN_GERAL ativo, usando DTO sem expor dados pessoais.

**Ainda nao implementado:** criacao e aprovacao/rejeicao de propostas, alteracao real de atribuicoes, bootstrap do primeiro ADMIN_GERAL e protecao territorial de endpoints legados. Sem operacoes de escrita nesta etapa. O esquema pode ser criado pelo Hibernate em proximo deploy conforme configuracao ddl-auto; realizar backup e homologacao antes de publicar. Compilacao, testes e deploy pendentes.


---

## 27. Criacao, aprovacao e rejeicao de redistribuicoes (08/10/2026)

**Codigo implementado nesta branch (nao implantado):**

- `SectorRedistributionService`: cria proposta de alteracao de responsavel por setor e realiza decisao transacional.
- `POST /api/admin/redistribution-proposals`: cria proposta; corpo JSON: `sectorId`, `toAdministratorId`, `reason`.
- `POST /api/admin/redistribution-proposals/{id}/approve`: aprova e aplica atribuicao, com verificacao da origem esperada.
- `POST /api/admin/redistribution-proposals/{id}/reject`: rejeita sem alterar atribuicao.
- `GET /api/admin/redistribution-proposals/csrf`: obtem cabecalho e token CSRF para chamadas de escrita autenticadas.
- Todas as operacoes exigem conta ativa `ADMIN_GERAL` no banco, nao apenas uma autoridade de sessao.
- O destino de uma redistribuicao deve ser `ADMIN_SETORIAL` ativo.
- A criacao bloqueia o registro do setor, recusa multiplos responsaveis e propostas pendentes duplicadas; a decisao bloqueia proposta e setor, impede decisao repetida e verifica se atribuicao atual ainda corresponde a origem.
- A decisao registra `decidedBy` e `decidedAt` para auditoria. A exclusao da atribuicao anterior e inclusao da nova ocorrem na mesma transacao.
- As novas requisicoes POST usam protecao CSRF com cookie/token; os formularios antigos permanecem temporariamente sem CSRF para compatibilidade. **Este risco legado deve ser corrigido antes de homologacao final.**
- Incluidos tres testes automatizados de autorizacao/decisao repetida em `SectorRedistributionServiceTest` (arquivos criados, testes nao executados).

**Bloqueadores antes do deploy de producao:**

1. Executar `gradlew test` e `gradlew build` em Java 21. Confirmar compilacao Spring Boot e testes de integracao com H2 e PostgreSQL.
2. Fazer backup do Neon e aplicar/modelar migracoes de banco de maneira controlada: atualmente `spring.jpa.hibernate.ddl-auto=update` pode criar tabelas automaticamente no startup.
3. Os endpoints administrativos antigos ainda aceitam `ROLE_ADMIN` de compatibilidade para `ADMIN_SETORIAL`, sem filtrar por setor. Corrigir antes de habilitar administradores setoriais em producao.
4. Definir a conta existente que recebera `ADMIN_GERAL`; a atribuicao inicial de proprietario exige confirmacao humana e procedimento auditavel. **Nao ha promocao automatica.**
5. Configurar `ADMIN_REGISTRATION_CODE` privado no Render e eliminar o valor-padrao inseguro depois de verificar a configuracao real; nao enviar segredos em conversas.
6. Implementar UI administrativa para revisar e aprovar propostas, associar lojas/veiculos a setores e revisar o fluxo de cadastro. Os dados de congestionamento e rotas alternativas dependem de integracao de trafego.
7. Adicionar testes concorrentes reais, protecao CSRF nos formularios antigos, validacao de origem de requisicoes e monitoramento das operacoes administrativas.

**Estado:** alteracoes enviadas somente a `branch10---erros-corrigidos` no fork do projeto. Nenhum deploy Render, alteracao manual no Neon, merge em Main ou atualizacao no repositorio de terceiros foi realizada nesta entrega.

**IDE / stack para informacao da equipe:** Visual Studio Code (VS Code), backend Java 21 / Spring Boot 4 / Gradle; PostgreSQL no Neon, Render (Docker) e Cloudflare Worker no acesso web. A IDE nao e requisito exclusivo; Eclipse ou IntelliJ podem importar o projeto Gradle.



---

## 28. Isolamento da interface do ADMIN_SETORIAL e integracao de login (08/10/2026)

**Implementado no codigo, nao testado em execucao:**

- `CustomUserDetailsService` adiciona autoridade `ROLE_ADMIN_LEGACY` apenas a contas ADMIN antigas; conserva `ROLE_ADMIN` para o login administrativo existente.
- `SecurityConfig` permite leitura `GET /api/admin/sectors` a ADMIN_SETORIAL e ADMIN_GERAL; endpoints de propostas e simulacao de redistribuicao exigem ADMIN_GERAL; `/admin.html` e as demais rotas `/api/admin/**` legadas aceitam somente ADMIN_GERAL e ADMIN legado (via ROLE_ADMIN_LEGACY), nao ADMIN_SETORIAL.
- Criada pagina `admin-setorial.html` e script `assets/js/admin-setorial.js`, com lista de setores atribuídos somente leitura e aviso de funcionalidades pendentes.
- Login administrativo redireciona ADMIN_SETORIAL para `/admin-setorial.html`; ADMIN_GERAL e ADMIN legado seguem para `/admin.html`.
- Menu de `mapa.html` e `mapa.js` identifica conta setorial e apresenta link `Meus setores` em vez de exibir links de usuario comum que retornariam 403.

**Limitacoes relevantes:**

- Tela e consulta setorial nao conferem ainda gerenciamento de lojas ou veiculos; o mapa compartilhado nao filtra veiculos territorialmente, e a interface administrativa completa nao foi criada.
- E necessario criar e migrar o primeiro ADMIN_GERAL a partir de conta designada pelo responsavel do projeto, sob procedimento de autorizacao e auditoria. Esta etapa nao foi realizada.
- A administracao legada permanece disponivel a ADMIN antigo; revisar os papeis em banco e os endpoints globais antes de uso multiadministrador.
- Build/testes de integracao, tests de autorizacao, deploy Render e validacao no Neon estao pendentes.
- Endpoints de criacao e decisao de propostas requerem token CSRF obtido do novo endpoint de CSRF e sessao administrativa valida.
- A aplicacao usa `ddl-auto=update`; proteger o Neon com backup e teste de migracao antes de deploy.

**Entrega da equipe:** desenvolver no Visual Studio Code (VS Code), projeto Java 21 / Spring Boot 4.1.1 / Gradle; infraestrutura: Neon PostgreSQL, Render Docker, Cloudflare Worker. O projeto Gradle tambem pode ser importado em Eclipse ou IntelliJ.
