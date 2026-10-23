# Operação territorial — branch 11

## Funcionalidades adicionadas em 13/10/2026

- Tela protegida `operacao.html`, acessível pelos painéis e mapa administrativos.
- Cadastro de setores e lojas pelo ADMIN_GERAL. A loja informa setor, coordenadas e capacidade.
- Veículo ligado opcionalmente a uma loja. Os veículos existentes são preservados sem atribuição.
- ADMIN_SETORIAL vê e opera apenas lojas ativas dos setores ativos atribuídos à sua conta. O mapa também aplica esse filtro no servidor.
- ADMIN_GERAL e ADMIN legado mantêm acesso global à frota; somente ADMIN_GERAL cadastra território e aprova redistribuição. Não existe promoção automática de contas.
- Transferências validam acesso na origem e destino, estado de aluguel, loja/setor ativos, bloqueio e capacidade. A loja de destino é bloqueada na transação para serializar transferências e alterações de capacidade concorrentes.
- A edição do veículo no painel global também bloqueia a linha em transação para não sobrescrever uma transferência concorrente.
- Transferência atualiza a loja, coordenadas e nome de localização do veículo.
- Capacidade não pode ser reduzida abaixo da quantidade atual de veículos. Veículos alugados não podem ser alterados ou transferidos por esta operação.
- Tela de propostas: simulação, criação manual ou a partir da sugestão, fila de pendentes, aprovação e rejeição. A aprovação continua sendo explícita e restrita ao administrador geral.
- Novas escritas exigem token CSRF. A tela busca o token na sessão e o envia no cabeçalho indicado pelo servidor.
- Proxy do Cloudflare protege também `/operacao` e `/operacao.html` e encaminha operações ao Render.
- Pipeline GitHub Actions com Java 21, testes Java, geração do JAR, testes Node e relatório de testes. O banco de testes é H2 em memória, sem usar o Neon de produção.

## Uso e dados existentes

1. Uma conta já provisionada como ADMIN_GERAL abre Operação territorial.
2. Cadastra setores e lojas. Os códigos são únicos; os formulários têm limites e validação no servidor.
3. Cadastra-se um administrador setorial pelo fluxo já existente. O cadastro público não concede ADMIN_GERAL.
4. O administrador geral cria/aprova a proposta de atribuição do setor ao ID do administrador setorial.
5. O administrador geral ou legado transfere os veículos sem loja para as lojas cadastradas. Somente então esses veículos ficam visíveis ao administrador setorial responsável.
6. O administrador setorial opera a frota entre suas próprias lojas. Um usuário comum não acessa essa tela ou suas APIs.

O código não altera senhas, papéis ou atribuições automaticamente. O primeiro ADMIN_GERAL foi provisionado manualmente pelo responsável no Neon, conforme registro de produção abaixo. Outras contas ADMIN legado não recebem essa promoção automaticamente.

## Alterações no banco

JPA adiciona `store_id` opcional em `TB_VEICULO`, `capacity` e `blocked` opcionais em `tb_loja_ecogiro`. Não há exclusão ou recriação de tabelas. Valores antigos nulos de capacidade/bloqueio são lidos como 20/false. O projeto já usa `spring.jpa.hibernate.ddl-auto=update`; backup e migrações versionadas continuam recomendados para evolução de produção.

## Verificação e limites

- 16 testes Node passaram localmente: perfis/headers/mapas, falhas e expiração, proxy, cookie, redirecionamento, logout e nova rota protegida.
- Testes Java adicionados para capacidade, bloqueio, alugados, origem não autorizada, atualização de localização, escopo setorial, logout/invalidação de sessão, bloqueios por papel e CSRF.
- Execução local do Gradle bloqueada por rede; execução com Java 21 no GitHub Actions aprovada. `test bootJar` passou, com 16 testes Java e 16 testes Node. Validação do código final: [execução 38329148732](https://github.com/arthurkratos20-max/EcoGiro/actions/runs/38329148732), commit `8e1cb57617c6abd8e094b1d47779e181383eb679`. A execução anterior também passou.
- Testes de logout do proxy usam respostas simuladas; o teste Java verifica a sessão no Spring. Logout no site publicado foi confirmado posteriormente pelo responsável, conforme registro atualizado abaixo.
- Bloqueio manual não é trânsito real. Integração com trânsito, tempo de viagem, rotas e otimização por distância/carga necessita fornecedor e dados externos.
- Simulação de setores ainda usa quantidade de setores, não quantidade de lojas/veículos, distância ou tráfego.
- A transferência registra a posição administrativa na loja; não representa GPS ao vivo ou execução física do transporte.
- CRUD completo de lojas/setores, auditoria das transferências, atribuição automática de rotas e validação física de localização continuam fora desta entrega.
- Novos commits da equipe `bb1f5fe` e `f53ae8e` foram comparados à base `ad378bb`: não apresentaram diferença líquida de arquivos. Não houve substituição da interface da equipe.

## Deploy

Publicar a mesma branch 11 no Render e Cloudflare. A página nova vem do backend para exigir sessão; assets continuam no Pages. O Neon não possui branch de código da aplicação. Revalidar login, Operação territorial, mapa por perfil e logout após os dois deploys.

## Verificação em produção e correções — 12/10/2026 (Brasília)

As capturas enviadas confirmaram deploy bem-sucedido de `93e497c` no Render e no Cloudflare Pages de produção. A tela de operação carregou os veículos do Neon. A restrição antiga `tb_usuario_role_check` rejeitou ADMIN_GERAL com SQLSTATE 23514: `ddl-auto=update` não atualizou essa regra antiga. A migração manual [20261013_admin_roles.sql](docs/migrations/20261013_admin_roles.sql) permite os quatro papéis válidos mantendo o CHECK. Não concede privilégios automaticamente. O usuário executou a migração e alterou somente sua própria conta para ADMIN_GERAL; a tela de cadastro geral passou a aparecer após novo login.

O log de 01:09:31 UTC confirmou duplicidade de `DF-CENTRO` em `uk_setor_codigo`, SQLSTATE 23505. O setor já existia. A correção retorna HTTP 409 com mensagem de código duplicado para setores e lojas, sem mostrar SQL ou detalhes internos. A tela exibe a mensagem com texto seguro, impede reenvio simultâneo dos formulários e distingue cadastro salvo de falha na atualização da tela. Teste de integração reproduz o cadastro repetido.

As verificações desta etapa foram complementadas pelos testes manuais descritos no estado atualizado abaixo; a verificação com a própria conta do administrador setorial continua pendente.

Validação da correção: 17 testes Java e compilação `test bootJar` aprovados no [Actions 38331010701](https://github.com/arthurkratos20-max/EcoGiro/actions/runs/38331010701); 18 testes Node aprovados localmente, incluindo os dois novos testes de formulário. O código foi validado antes da atualização da branch de deploy.


## Estado atualizado da homologação — 13/10/2026, 00h13 (America/Sao_Paulo)

Este registro atualiza as pendências históricas acima. Evidências: capturas dos painéis e da aplicação enviadas pelo responsável e confirmações textuais nesta sessão. Não houve acesso direto do assistente ao Neon ou às contas dos outros administradores.

### Fluxos confirmados manualmente

| Fluxo | Resultado e limite da evidência |
| --- | --- |
| Deploy inicial da operação | Render e Cloudflare Pages de produção mostraram sucesso no commit `93e497c`. |
| Perfil geral | Conta principal provisionada manualmente como ADMIN_GERAL; formulários exclusivos apareceram após novo login. |
| Setor | `DF-CENTRO`, Brasília — Centro, ID 1, apareceu na lista da aplicação. |
| Loja | `DF-DEMO-01`, EcoGiro Brasília — Demonstração, vinculada ao setor Centro, apareceu na aplicação. Loja fictícia para o trabalho acadêmico; coordenadas orientadas: -15.7942, -47.8822; capacidade 20. Não foi validado um endereço comercial real. |
| Duplicidade de setor | Log confirmou SQLSTATE 23505 / `uk_setor_codigo` para DF-CENTRO já existente. Tratamento corrigido no código. |
| Duplicidade de loja | Tela no Render mostrou mensagem clara de loja já cadastrada, com a loja existente preservada. |
| Transferência | ECO-M001 passou de sem loja atribuída para a loja de demonstração. Ocupação passou de 0/20 para 1/20. A coluna Loja atual também foi observada depois no Cloudflare. |
| Lotação | Com ocupação 1 e capacidade alterada para 1, a loja sumiu dos destinos da ECO-E001; voltou ao restaurar 20. Confirma o filtro da interface em produção; recusa de requisição direta a destino lotado permanece coberta pelos testes automatizados, não por este teste manual. |
| Bloqueio manual | Ao marcar e salvar Destino bloqueado, a loja sumiu dos destinos mesmo com vagas; voltou ao desmarcar e salvar. Confirmado pelo responsável. |
| Leitura pelo Cloudflare | `https://ecogiro.pages.dev/operacao.html` exibiu a mesma loja com 1/20, bloqueio desmarcado e dados atualizados. |
| Logout pelo Cloudflare | Após Sair, nova navegação explícita para operacao.html exigiu login. Confirmado pelo responsável. |
| Conversão dos administradores antigos | Responsável executou no Neon atualização de role ADMIN para ADMIN_SETORIAL. Duas contas, IDs 3 e 5, retornaram atualizadas e ativas. A conta ADMIN_GERAL não foi incluída. Senhas e estado ativo foram preservados pelo SQL. |
| Simulação | Com um setor e dois administradores setoriais ativos, sugeriu Brasília — Centro para administrador #3. Simulação não altera atribuições. |
| Proposta | Proposta #1 para setor Centro / administrador #3 apareceu em Propostas pendentes. |
| Aprovação | Após o responsável clicar Aprovar, a proposta saiu da fila; nova simulação não sugeriu alterações nem mostrou setor sem responsável. Evidência compatível com aprovação; ainda falta confirmação pela conta setorial ou consulta do estado APPROVED no banco. |

### Incidentes e correções registrados

- A regra antiga `tb_usuario_role_check` rejeitou ADMIN_GERAL (SQLSTATE 23514). O responsável executou a migração transacional que mantém a validação e permite USER, ADMIN, ADMIN_GERAL e ADMIN_SETORIAL; depois atualizou sua própria conta. Script genérico, sem promoção automática: [20261013_admin_roles.sql](docs/migrations/20261013_admin_roles.sql).
- Erro 500 ao repetir DF-CENTRO foi causado por código já existente, não por perda do setor. Correção publicada nos commits `db642cd` e `6517ea6`: HTTP 409 e mensagem segura para código de setor/loja duplicado, proteção contra envio simultâneo de formulários e distinção entre cadastro salvo e falha ao atualizar a tela.
- A tentativa de criar loja apresentou “Failed to fetch”. A loja foi posteriormente observada persistida, e um novo cadastro foi recusado como duplicado. A causa da falha de comunicação não foi confirmada; não equivale a cadastro perdido.
- Um login pelo Cloudflare mostrou uma tela do Render e voltou ao formulário. Após abrir o Render e aguardar o site carregar, o responsável confirmou que o login funcionou. Inicialização do serviço é hipótese, não diagnóstico confirmado; não foi aplicada correção de código específica para esse incidente.
- Testes da correção: 17 testes Java e `test bootJar` aprovados no [Actions 38331010701](https://github.com/arthurkratos20-max/EcoGiro/actions/runs/38331010701). 18 testes Node aprovados localmente, incluindo envio duplo e falha de atualização após salvar. Não foram executados testes automatizados contra o Neon de produção.

### Estado de demonstração e próximos passos

Estado observado: setor Centro cadastrado; loja fictícia com capacidade restaurada para 20, bloqueio desmarcado e um veículo vinculado. Não criar novamente os mesmos códigos.

**Pendente de confirmação pelo administrador #3:** sair e entrar com a própria conta pelo login administrativo; conferir painel setorial, setor Brasília — Centro, loja de demonstração e ECO-M001; verificar ausência dos formulários de cadastro geral e aprovação. O responsável informou que esse teste precisa ser feito pelo titular da conta. Nenhuma senha dessa conta foi solicitada ou usada.

Também permanecem sem homologação manual: acesso negado a outro território/API global, rejeição de proposta, perfil USER após esta entrega, demais fluxos de planos e operações de frota. Não declarar toda a aplicação homologada com base nestes testes.

Com apenas um setor para dois administradores, um pode continuar sem setor; a simulação equilibra a quantidade de setores, não divide um mesmo setor entre contas. Contas cujo papel foi alterado devem sair e entrar novamente. Não houve envio automático de mensagens ao grupo.

Limites funcionais preservados: bloqueios são manuais, sem trânsito real, GPS ao vivo ou otimização de trajetos; CRUD completo de setores/lojas e auditoria de transferências ainda não foram entregues. A documentação registra o que existe, o que foi observado e o que continua pendente, sem afirmar essas funcionalidades como concluídas.
