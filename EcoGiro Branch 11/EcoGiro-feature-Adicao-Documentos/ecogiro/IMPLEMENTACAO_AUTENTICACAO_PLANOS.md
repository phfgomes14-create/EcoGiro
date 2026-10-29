# EcoGiro — autenticação, planos e mapa

## O que foi acrescentado

- Autenticação com Spring Security e BCrypt.
- Cadastro/login de usuário comum.
- Cadastro/login de administrador com código administrativo autorizado.
- Papéis `USER` e `ADMIN` e proteção de rotas.
- Área do usuário (`/usuario.html`).
- Catálogo de planos e solicitação de contratação (`/planos-aluguel.html`).
- Fluxo de aprovação/rejeição pelo administrador.
- Criação automática de assinatura ativa quando uma solicitação é aprovada.
- Painel administrativo (`/admin.html`) para usuários, planos, solicitações e frota.
- Banco para veículos com latitude/longitude/status.
- Mapa Leaflet + OpenStreetMap (`/mapa.html`) alimentado pelos veículos do banco.
- Persistência do histórico do quiz na tabela `TB_RECOMENDACAO`.
- PostgreSQL driver e datasource por variáveis de ambiente, pronto para Neon/Render.
- Dockerfile para deploy.

## Banco de dados

Tabelas principais:

- `TB_USUARIO`
- `TB_PLANO_ALUGUEL`
- `TB_SOLICITACAO_PLANO`
- `TB_ASSINATURA_PLANO`
- `TB_VEICULO`
- `TB_RECOMENDACAO`

O projeto usa H2 persistente localmente. As tabelas são criadas/atualizadas por JPA (`ddl-auto=update`).

## Executar localmente

```bash
./gradlew bootRun
```

Acesse `http://localhost:8080/index.html`.

### Cadastro de administrador local

O campo **ID do administrador** valida a propriedade `ADMIN_REGISTRATION_CODE`.

Valor local padrão para desenvolvimento:

```text
ECOGIRO-ADMIN-2026
```

No Render, defina obrigatoriamente um valor diferente e secreto em `ADMIN_REGISTRATION_CODE`.

## Variáveis para PostgreSQL/Neon

O projeto aceita:

```text
SPRING_DATASOURCE_URL
SPRING_DATASOURCE_USERNAME
SPRING_DATASOURCE_PASSWORD
ADMIN_REGISTRATION_CODE
PORT
```

A URL de produção deve estar no formato JDBC, por exemplo:

```text
jdbc:postgresql://HOST/DB?sslmode=require
```

## Fluxo de plano

1. Usuário cria a conta e faz login.
2. Acessa **Planos**.
3. Envia uma solicitação para um plano.
4. A solicitação entra como `PENDING`.
5. Administrador abre o painel e aprova ou rejeita.
6. O usuário vê a resposta na área da conta.
7. Em caso de aprovação, é criada uma assinatura `ACTIVE` com início na data da aprovação e término conforme a duração do plano.

## Mapa

O mapa não usa uma lista fixa no frontend. Ele consulta:

```text
GET /api/map/vehicles
```

Os veículos são cadastrados/editados pelo painel administrativo, e cada registro possui latitude, longitude, local, tipo e status.

## Observação de segurança

O projeto desativa CSRF para manter compatibilidade imediata com os formulários HTML estáticos existentes. Antes de uma versão pública definitiva, é recomendável reativar CSRF e inserir tokens nos formulários/requests que modificam dados.
