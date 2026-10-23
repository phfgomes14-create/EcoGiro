# Integração do estoque da equipe - 22/10/2026

## Origem e escopo

Integração seletiva do ZIP EcoGiro-branch11-UI-UX-atualizados.zip e do relatório de alterações confirmado pelo responsável. Base do fork: 9133c86. Não substitui o backend pelo ZIP, pois aquele pacote não inclui os perfis e a operação territorial já publicados no fork. Preserva as demais telas, protocolos, compatibilidade de planos e testes existentes. O fluxo de reservas encontrado no ZIP não pertence ao relatório confirmado e não foi incorporado nesta entrega.

## Pedidos atendidos

- Menu de admin.html direciona para estoque.html e remove o atalho de mapa. Não adiciona quiz ou recomendação ao painel.
- Estoque mantém o layout, CSS e HTML organizado da equipe, com filtros de localização, tipo e disponibilidade e contadores.
- Marcadores verdes com rótulos escapados agrupam veículos por locationName. Coordenadas são médias dos veículos com latitude/longitude válidas. Não representam GPS ao vivo nem centros oficiais de setores.
- Clique no marcador seleciona a localização e atualiza lista/contadores preservando filtros de tipo/status. Sem resultados, exibe mensagem clara.
- Localização é agrupamento visual, não atribuição de permissão. Dois locais com mesmo nome são agrupados intencionalmente conforme o relatório.

## Integração com permissões existentes

- Página estoque.html protegida para ADMIN_GERAL, ADMIN_SETORIAL e ADMIN legado.
- GET /api/admin/operations/inventory inclui veículos inativos, mas aplica TerritoryAccessService no servidor: o administrador setorial recebe apenas frota de lojas ativas de seus setores ativos atribuídos. Geral/legado têm visão global, inclusive veículos sem loja.
- A tela não consulta nem libera /api/admin/vehicles aos setoriais. Navegação de retorno usa perfil da sessão e header administrativo.
- Alterações de status e transferências usam POSTs existentes da operação territorial e token CSRF. Não permite definir RENTED manualmente ou editar veículos alugados.
- Destinos são lojas autorizadas, ativas, não bloqueadas e com vaga. Servidor revalida escopo, lotação, bloqueio e estado do veículo em transação.
- Status e transferência são duas operações independentes: se uma falhar, recarrega e orienta conferir os dados. Uma alteração de status bem-sucedida não é desfeita automaticamente por falha posterior na transferência.
- Guarda filtros apenas na tela, sem persistir localização de um administrador para outra sessão.
- Se Leaflet não carregar, a lista e os filtros continuam funcionando.

## Infraestrutura e publicação

- Render: publicar a branch branch11---UI/UX-atualizados após validação do CI. Se o serviço já acompanha essa branch do fork e o deploy automático está ligado, basta acompanhar a publicação. Caso contrário, selecionar essa branch e fazer deploy do commit validado. Manter Docker, Java 21 e as variáveis atuais. Não trocar credenciais.
- Neon: esta integração não modifica entidades, tabelas, colunas ou papéis. Não requer SQL, migração, alteração de senha ou recriação do banco. Não foram executadas operações no banco de produção.
- Cloudflare: nenhuma ação manual para uso pelo endereço principal https://ecogiro.onrender.com. Mantida a orientação de não alterar o Pages. O arquivo de proxy legado foi atualizado no repositório para proteger também /estoque e /estoque.html caso o Pages ainda publique automaticamente a mesma branch. Nenhum painel Cloudflare foi alterado.
- Não foi inspecionado o painel atual do Render nem executado deploy manual nesta sessão. Confirmar o commit em produção e realizar os testes abaixo após o deploy.

## Validação

- Testes Node executam o script da tela com DOM, API e Leaflet simulados: agrupamento/coordenadas, clique e filtros, escape HTML e destinos, escrita com CSRF e proxy de estoque.
- Testes Java verificam a filtragem do inventário (incluindo inativos e exclusão de outros setores) e a proteção HTTP da página e APIs.
- CI completo aprovado antes de atualizar a branch de trabalho: execução 39883424296, commit 381cdf9f8a1c62ca0d898c36b4e5dbb5ffe81bea, branch validacao/estoque-20261022. Testes Node e Java e compilação test bootJar concluídos com sucesso. Link: https://github.com/arthurkratos20-max/EcoGiro/actions/runs/39883424296 .
- Cinco testes novos de JavaScript aprovados também localmente. Quatro testes Java novos aprovados no CI, preservando a suíte anterior. O commit final acrescenta somente este registro de validação.

## Roteiro manual após deploy

1. Login geral: abrir Estoque pelo menu, validar agrupamento verde e clicar em uma localização.
2. Filtrar tipo e status, atualizar, comparar contadores e lista. Sem resultados, verificar mensagem.
3. Login setorial com sua própria conta: verificar somente os veículos dos setores atribuídos e retorno ao painel setorial. Sem atribuição, lista vazia.
4. Transferir um veículo disponível entre lojas autorizadas e verificar a nova posição/ocupação na operação territorial.
5. Conferir que lojas lotadas/bloqueadas não aparecem como destino e veículos alugados não podem ser alterados.
6. USER ou deslogado não deve acessar estoque nem sua API. Após logout, acesso explícito exige novo login.

Os testes de produção acima não foram realizados nesta entrega e não devem ser apresentados como concluídos.
