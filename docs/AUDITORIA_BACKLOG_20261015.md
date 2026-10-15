# Auditoria das tarefas atribuídas — 15/10/2026

Solicitação: conferir as tarefas da captura GitHub Notifications e completar lacunas no fork `arthurkratos20-max/EcoGiro`, branch `branch11---UI/UX-atualizados`. Produção indicada pelo responsável: [Render](https://ecogiro.onrender.com/). Não foi acessado nem alterado o painel/configuração do Pages. Atualizações do Git podem acionar integrações automáticas já configuradas nos provedores; essa auditoria não as desativa.

## Rastreabilidade

| Tarefa original | Verificação e entrega |
| --- | --- |
| [#23 BL03](https://github.com/phfgomes14-create/EcoGiro/issues/23) | [DER, dicionário das dez entidades, FKs, índices, nulabilidade e enumerações](MODELO_DE_DADOS.md). |
| [#25 BL05](https://github.com/phfgomes14-create/EcoGiro/issues/25) | [Matriz e entradas/saídas esperadas](REGRAS_RECOMENDACAO.md); ranking extraído sem alterar pontuação; testes dos cenários e 1024 combinações. |
| [#42 BL22](https://github.com/phfgomes14-create/EcoGiro/issues/42), duplicata [#12](https://github.com/phfgomes14-create/EcoGiro/issues/12) já fechada | Projeto Spring Boot/Gradle e camadas já existentes; documentação da arquitetura e DTO validado para a nova entrada de solicitação. |
| [#43 BL23](https://github.com/phfgomes14-create/EcoGiro/issues/43), descrição relacionada [#16](https://github.com/phfgomes14-create/EcoGiro/issues/16) | Dez entidades/repositories existentes conferidos e documentados. Vínculo opcional solicitação→veículo adicionado para compatibilidade e legado. |
| [#49 BL29](https://github.com/phfgomes14-create/EcoGiro/issues/49) | Seleção de veículo na solicitação, listagem compatível, validação no servidor e revalidação na aprovação. |
| [#50 BL30](https://github.com/phfgomes14-create/EcoGiro/issues/50) | UUID criado pelo servidor em @PrePersist, restrição única, retorno nas APIs e exibição ao usuário/administrador. |

As issues #42/#43/#49/#50 estavam sem corpo e sem comentários na consulta; decisões abaixo são a implementação adotada a partir dos títulos e do código existente, não critérios de aceite adicionais fornecidos pelo professor. Nenhuma issue original foi fechada ou editada; entrega publicada somente no fork.

## BL29 — decisão e contrato

O sistema não tinha catálogo separado de modelos. Um registro de Vehicle contém modelo, tipo e código da unidade; por isso a solicitação escolhe vehicleId, e a compatibilidade usa o VehicleType persistido dessa unidade, nunca um texto fornecido pelo cliente.

- TODOS aceita MOTO, BIKE, E_BIKE, SCOOTER.
- MOTO_E_ELETRICOS aceita MOTO, E_BIKE e SCOOTER; exclui BIKE.
- MOTO/BIKE/E_BIKE/SCOOTER aceitam somente o tipo correspondente.
- Categoria desconhecida falha fechada; novos cadastros/edições de planos rejeitam categoria inválida.
- Plano precisa estar ativo e veículo AVAILABLE na criação e aprovação.
- GET /api/user/vehicles?planId=ID retorna opções compatíveis para usuário autenticado.
- POST /api/user/requests recebe planId, vehicleId e message opcional (até 500 caracteres). Não recebe protocolo.
- Solicitações anteriores sem vehicleId continuam consultáveis e aprováveis pelo fluxo anterior. Novas solicitações exigem seleção. Clientes antigos precisam recarregar a página/usar o novo contrato.
- Aprovação/cancelamento bloqueiam a solicitação na transação para evitar duas decisões concorrentes sobre a mesma solicitação.
- A contratação é de plano: escolher uma unidade serve à validação de modelo/tipo e não reserva nem altera o estado físico do veículo. Aluguel exclusivo/estoque por unidade não foi implementado nesta tarefa.

## BL30 — unicidade e legado

Protocolo novo é UUID v4 de 36 caracteres gerado exclusivamente no servidor, com índice único. Não depende do relógio nem do número de itens no banco. Permanece igual ao cancelar/aprovar/rejeitar. Registros antigos com coluna nula são exibidos como LEGACY-id, em namespace separado dos UUIDs. Sem apagamento, recadastro ou troca de identificadores anteriores.

JPA adiciona protocol (opcional para compatibilidade), índice único e veiculo_id opcional. A aplicação mantém ddl-auto=update já existente. Não foi executado SQL no Neon nesta entrega. Teste real em PostgreSQL/Neon e confirmação do deploy permanecem necessários; H2 não substitui essa validação.

## Verificação

Validação final aprovada no [GitHub Actions 38736787265](https://github.com/arthurkratos20-max/EcoGiro/actions/runs/38736787265), commit b77f91cbd336299c94be9cb6c36469e0092a0705: 26 testes Java, 29 testes Node e compilação `test bootJar`. A execução anterior [38736404996](https://github.com/arthurkratos20-max/EcoGiro/actions/runs/38736404996) também passou. A suíte Node cobre as 1024 combinações do recomendador e a seleção obrigatória de veículo na interface. Testes Java novos cobrem matriz completa de categorias/tipos, plano inativo, indisponibilidade, seleção ausente, incompatibilidade na criação/aprovação, decisão repetida e persistência/imutabilidade de protocolo. Não foram usadas credenciais nem banco de produção nos testes.

## Pendências preservadas

A homologação do administrador setorial pelo titular ainda depende da confirmação da equipe. Congestionamento real/roteamento, auditoria de transferências e CRUD completo de lojas/setores continuam fora desta auditoria, pois não constam como entrega nas tarefas da imagem. O recomendador mantém os limites descritos em REGRAS_RECOMENDACAO.md. Não há garantia de todo o projeto concluído apenas por estas tarefas terem sido revisadas.

O README foi atualizado para refletir Java 21/Gradle, Render, contratos atuais, localização do aplicativo e links de documentação; instruções antigas de Maven e endpoint /recomendacao não correspondiam a esta branch. O teste de persistência também verifica que o banco rejeita um protocolo duplicado. Publicação de código não equivale a confirmação de deploy ou homologação manual do novo fluxo em produção.
