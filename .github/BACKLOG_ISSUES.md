# Backlog Issues - EcoGiro

Este arquivo documenta todas as 50 issues do backlog do projeto EcoGiro.

## Sprint 0 - Planejamento e Arquitetura

### BL01 - Planejamento: Definir modelo de negócio, identidade visual e wireframes
- Prioridade: Alta
- Período: 05/08/2026 - 09/08/2026
- Dependências: Nenhuma

### BL02 - Planejamento: Estruturar repositório Git, branches e fluxo de PR
- Prioridade: Alta
- Período: 05/08/2026 - 09/08/2026
- Dependências: Nenhuma

### BL03 - Arquitetura: Definir modelo de dados e enumerações
- Prioridade: Alta
- Período: 05/08/2026 - 09/08/2026
- Dependências: Nenhuma

### BL04 - Arquitetura: Definir contrato da API REST
- Prioridade: Alta
- Período: 05/08/2026 - 09/08/2026
- Dependências: BL03

### BL05 - Recomendador: Formalizar regras determinísticas e casos de teste
- Prioridade: Alta
- Período: 05/08/2026 - 09/08/2026
- Dependências: BL03

## Sprint 1 - Front-end Inicial

### BL06 - Front-end: Desenvolver Landing Page responsiva
- Prioridade: Alta
- Status: Concluído
- Período: 10/08/2026 - 16/08/2026
- Dependências: BL01

### BL07 - Front-end: Desenvolver catálogo de modelos e filtros
- Prioridade: Alta
- Período: 10/08/2026 - 16/08/2026
- Dependências: BL01

## Sprint 2-3 - Front-end Intermediário

### BL08 - Front-end: Desenvolver tela de comparação de planos
- Prioridade: Alta
- Status: Concluído
- Período: 17/08/2026 - 23/08/2026
- Dependências: BL01

### BL09 - Front-end: Criar detalhes e disponibilidade dos modelos
- Prioridade: Alta
- Status: Concluído
- Período: 17/08/2026 - 23/08/2026
- Dependências: BL07

### BL10 - Front-end: Criar questionário com progresso e validação
- Prioridade: Alta
- Status: Concluído
- Período: 17/08/2026 - 23/08/2026
- Dependências: BL05

### BL11 - Front-end: Criar tela de recomendação e justificativa
- Prioridade: Alta
- Status: Concluído
- Período: 24/08/2026 - 30/08/2026
- Dependências: BL10

### BL12 - Front-end: Permitir alternativa manual compatível à recomendação
- Prioridade: Alta
- Status: Concluído
- Período: 24/08/2026 - 30/08/2026
- Dependências: BL11

## Sprint 4-5 - Front-end e Admin

### BL13 - Front-end: Criar formulário de solicitação
- Prioridade: Alta
- Período: 31/08/2026 - 06/09/2026
- Dependências: BL08, BL11

### BL14 - Front-end: Validações do formulário e confirmação
- Prioridade: Alta
- Período: 31/08/2026 - 06/09/2026
- Dependências: BL13

### BL15 - Front-end: Criar tela de consulta por protocolo e CPF
- Prioridade: Alta
- Período: 07/09/2026 - 13/09/2026
- Dependências: Nenhuma

### BL16 - Front-end Admin: Criar login/logout administrativo
- Prioridade: Alta
- Status: Concluído
- Período: 07/09/2026 - 13/09/2026
- Dependências: BL01

## Sprint 6 - Admin Interface

### BL17 - Front-end Admin: Criar painel com indicadores simples
- Prioridade: Média
- Período: 14/09/2026 - 20/09/2026
- Dependências: BL16

### BL18 - Front-end Admin: Lista, filtros e detalhe de solicitações
- Prioridade: Alta
- Período: 14/09/2026 - 20/09/2026
- Dependências: BL16

### BL19 - Front-end Admin: Interface de status e observação interna
- Prioridade: Alta
- Período: 14/09/2026 - 20/09/2026
- Dependências: BL18

### BL20 - Front-end Admin: Interfaces de CRUD de modelos e planos
- Prioridade: Alta
- Período: 14/09/2026 - 20/09/2026
- Dependências: BL16

## Sprint 7 - Back-end Base

### BL21 - Banco: Criar MySQL, migrations/scripts e dados iniciais
- Prioridade: Alta
- Período: 21/09/2026 - 27/09/2026
- Dependências: BL03

### BL22 - Back-end: Inicializar projeto Spring Boot e camadas
- Prioridade: Alta
- Período: 21/09/2026 - 27/09/2026
- Dependências: BL04

### BL23 - Back-end: Criar entidades, relacionamentos e repositories
- Prioridade: Alta
- Período: 21/09/2026 - 27/09/2026
- Dependências: BL21, BL22

## Sprint 8 - APIs e Integração

### BL24 - API: GET de modelos e filtros
- Prioridade: Alta
- Período: 28/09/2026 - 04/10/2026
- Dependências: BL23

### BL25 - API: GET de planos ativos
- Prioridade: Alta
- Período: 28/09/2026 - 04/10/2026
- Dependências: BL23

### BL26 - Recomendador: Implementar service de recomendação no servidor
- Prioridade: Alta
- Período: 28/09/2026 - 04/10/2026
- Dependências: BL05, BL23

### BL27 - API: POST /api/recomendacoes com justificativa
- Prioridade: Alta
- Período: 28/09/2026 - 04/10/2026
- Dependências: BL26

### BL28 - Integração: Integrar catálogo, planos e questionário às APIs
- Prioridade: Alta
- Período: 28/09/2026 - 04/10/2026
- Dependências: BL07, BL08, BL09, BL10, BL24, BL25, BL26, BL27

## Sprint 9 - APIs Avançadas

### BL29 - Back-end: Validar compatibilidade entre modelo e plano
- Prioridade: Alta
- Período: 05/10/2026 - 11/10/2026
- Dependências: BL23

### BL30 - Back-end: Gerar protocolo único no servidor
- Prioridade: Alta
- Período: 05/10/2026 - 11/10/2026
- Dependências: BL23

### BL31 - API: POST /api/solicitacoes e persistência
- Prioridade: Alta
- Período: 05/10/2026 - 11/10/2026
- Dependências: BL29, BL30

### BL32 - Integração: Integrar formulário de solicitação e confirmação
- Prioridade: Alta
- Período: 05/10/2026 - 11/10/2026
- Dependências: BL13, BL14, BL31

## Sprint 10 - Consultas Públicas

### BL33 - API: Consulta por protocolo e CPF
- Prioridade: Alta
- Período: 12/10/2026 - 18/10/2026
- Dependências: BL31

### BL34 - Integração: Integrar consulta pública sem observação interna
- Prioridade: Alta
- Período: 12/10/2026 - 18/10/2026
- Dependências: BL15, BL33

## Sprint 11 - Segurança

### BL35 - Segurança: Spring Security, sessão HTTP e cookie HttpOnly
- Prioridade: Alta
- Período: 19/10/2026 - 25/10/2026
- Dependências: BL22

### BL36 - Segurança: BCrypt, proteção /api/admin e segurança de logs
- Prioridade: Alta
- Período: 19/10/2026 - 25/10/2026
- Dependências: BL35

## Sprint 12 - Admin APIs

### BL37 - API Admin: Listar e filtrar solicitações
- Prioridade: Alta
- Período: 26/10/2026 - 01/11/2026
- Dependências: BL35

### BL38 - API Admin: Atualizar status e observação interna
- Prioridade: Alta
- Período: 26/10/2026 - 01/11/2026
- Dependências: BL37

## Sprint 13 - Admin CRUD e Integração

### BL39 - API Admin: CRUD de modelos de veículos
- Prioridade: Alta
- Período: 02/11/2026 - 08/11/2026
- Dependências: BL35

### BL40 - API Admin: CRUD de planos
- Prioridade: Alta
- Período: 02/11/2026 - 08/11/2026
- Dependências: BL35

### BL41 - Integração Admin: Integrar painel, solicitações, modelos e planos
- Prioridade: Alta
- Período: 02/11/2026 - 08/11/2026
- Dependências: BL17, BL18, BL19, BL20, BL37, BL38, BL39, BL40

### BL42 - Qualidade: Padronizar erros 400/401/403/404/409 e DTO público
- Prioridade: Alta
- Período: 02/11/2026 - 08/11/2026
- Dependências: Nenhuma

## Sprint 14-15 - Qualidade e Entrega

### BL43 - Qualidade: Executar testes funcionais, segurança e recomendador
- Prioridade: Alta
- Período: 09/11/2026 - 15/11/2026
- Dependências: Nenhuma

### BL44 - Qualidade: Correções, acessibilidade, responsividade e refatoração
- Prioridade: Alta
- Período: 16/11/2026 - 22/11/2026
- Dependências: BL43

### BL45 - Documentação: README, script/migrations e preparação da apresentação
- Prioridade: Alta
- Status: Em andamento
- Período: 16/11/2026 - 22/11/2026
- Dependências: Nenhuma

### BL46 - Entrega: Validação final e demonstração
- Prioridade: Alta
- Período: 23/11/2026 - 23/11/2026
- Dependências: BL44, BL45

## Backlog Opcional - Features Extra

### BL47 - Extra: Pontos de atendimento
- Prioridade: Baixa
- Status: Backlog
- Dependências: Nenhuma
- Nota: Somente após o MVP obrigatório

### BL48 - Extra: Dashboard por tipo e status
- Prioridade: Baixa
- Status: Backlog
- Dependências: Nenhuma
- Nota: Somente após o MVP obrigatório

### BL49 - Extra: Comparador de economia estimada com valores fixos
- Prioridade: Baixa
- Status: Backlog
- Dependências: Nenhuma
- Nota: Somente após o MVP obrigatório

### BL50 - Extra: Explicação apoiada por IA ou histórico de status
- Prioridade: Baixa
- Status: Backlog
- Dependências: Nenhuma
- Nota: Somente após o MVP obrigatório
