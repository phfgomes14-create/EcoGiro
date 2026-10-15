# EcoGiro - Mobilidade Sustentável Urbana

## Identificação Acadêmica

* **Instituição de Ensino:** Centro Universitário do Planalto Central Apparecido dos Santos
* **Curso:** Análise e Desenvolvimento de Sistemas
* **Disciplina:** Projeto Integrado WEB
* **Orientador:** Profº Hudson Neves
* ## Equipe do Projeto

* **Integrantes:**
* - Alessandro Falcão Zovico
* - Arthur Cauã Machado De Souza
* - Cauã Alves Fernandes
* - Edna Júlia Menezes Marques
* - Lucas Ibiapina De Morais
* - Pedro Henrique Ferreira Gomes
* - Sophia Fonseca Mandovano Moreira de Azevedo
* - Yudi Fidelis De Morais

---

## Aplicação atual — branch 11

- Site: [EcoGiro no Render](https://ecogiro.onrender.com/)
- [Login administrativo](https://ecogiro.onrender.com/admin-login.html)
- Aplicativo: `EcoGiro Branch 11/EcoGiro-feature-Adicao-Documentos/ecogiro`
- Java 21, Gradle Wrapper, Spring Boot, Security, JPA e Validation.
- H2 para desenvolvimento/testes; PostgreSQL/Neon no ambiente publicado.

## Documentação para o time

| Documento | Conteúdo |
| --- | --- |
| [Auditoria das tarefas](docs/AUDITORIA_BACKLOG_20261015.md) | BL03, BL05, BL22, BL23, BL29 e BL30; entregas, testes e decisões |
| [Modelo de dados](docs/MODELO_DE_DADOS.md) | DER, dez entidades, chaves, enumerações e dicionário de dados |
| [Regras de recomendação](docs/REGRAS_RECOMENDACAO.md) | Matriz, desempate, justificativas, entradas/saídas e limites |
| [Operação territorial](OPERACAO_TERRITORIAL.md) | Perfis, lojas, frota, propostas e homologação |
| [Integração da branch 11](INTEGRACAO_BRANCH11.md) | Histórico de integração e deploy |
| [Histórico da branch 10](HISTORICO_BACKEND_BRANCH10.md) | Entregas anteriores |

## Funcionalidades implementadas

Cadastro e login separados para usuário e administrador; sessão, BCrypt e permissões por papel. Usuário consulta planos, solicita contratação escolhendo veículo compatível, acompanha protocolo, resposta e assinatura. Administração geral gerencia usuários, planos e frota. Administrador setorial opera o território atribuído. Há mapas distintos, lojas, capacidade, bloqueio manual e redistribuição por proposta/aprovação.

O quiz calcula um ranking de preferências no JavaScript compartilhado e testado; `POST /api/recommendations` persiste o histórico. O backend não recalcula esse ranking. As regras de compatibilidade de contratação são verificadas separadamente no servidor e não confiam na recomendação enviada pelo cliente.

## Execução local

Instale JDK 21. Dentro do diretório do aplicativo:

```bash
bash gradlew test bootJar --no-daemon
bash gradlew bootRun
```

No Windows, use `gradlew.bat test bootJar` e `gradlew.bat bootRun`. Acesse `http://localhost:8080/` (ou a porta definida por PORT). Não é necessário instalar Maven.

Na raiz do repositório, com Node 22:

```bash
node --test tests/*.test.cjs
```

## Configuração

Consulte `src/main/resources/application.properties`. Produção utiliza SPRING_DATASOURCE_URL (JDBC), SPRING_DATASOURCE_USERNAME e SPRING_DATASOURCE_PASSWORD. ADMIN_REGISTRATION_CODE é configurado no ambiente do servidor. Não publique senhas, tokens ou URLs com credenciais.

O Dockerfile utiliza Java 21. O Render hospeda o Spring Boot e seus arquivos estáticos no mesmo domínio. A configuração externa do Cloudflare Pages não é alterada nesta entrega; integrações Git existentes podem executar deploy automático.

## API de contratação

- `GET /api/user/plans`: planos ativos.
- `GET /api/user/vehicles?planId=ID`: veículos disponíveis compatíveis.
- `POST /api/user/requests`: entrada `{planId, vehicleId, message}`; protocolo gerado pelo servidor.
- `GET /api/user/requests`: histórico com protocolo, veículo, status e resposta.
- `POST /api/user/requests/{id}/cancel`: cancela solicitação própria pendente.
- `PATCH /api/admin/requests/{id}/respond`: aprova/rejeita no painel global autorizado.

O protocolo de novas solicitações é UUID; registros anteriores podem aparecer como LEGACY-id. A seleção de unidade não é reserva física de veículo. Contratos completos e limitações estão na auditoria.

## Limites e validação

Consulte os documentos de homologação antes de afirmar que um fluxo foi testado em produção. Continuam pendentes a confirmação do titular da conta setorial e funções planejadas como trânsito real/roteamento. O quiz usa preferências qualitativas e não garante custo, autonomia ou habilitação legal de um modelo. O frontend não substitui a validação no servidor.

A suíte automatizada e o JAR são verificados no GitHub Actions com H2 em memória, sem acesso ao Neon de produção.
