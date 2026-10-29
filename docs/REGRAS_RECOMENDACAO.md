# BL05 — Regras determinísticas e cenários de recomendação

A implementação atual é um ranking por preferências, não uma otimização financeira ou garantia de autonomia/legalidade. Cada resposta soma um ponto ao perfil indicado abaixo. Distância e orçamento são faixas qualitativas escolhidas pelo usuário; não há cálculo de custo real por quilômetro. A extração para `assets/js/recommendation-rules.js` preserva o comportamento da branch 11.

## Matriz de entrada

| Pergunta | Resposta | Perfil que recebe +1 |
| --- | --- | --- |
| FINALIDADE | Trabalho e entregas | moto |
| FINALIDADE | Lazer e passeios | bicicleta |
| FINALIDADE | Deslocamento urbano curto | patinete |
| FINALIDADE | Commute diário confortável | ebike |
| DISTÂNCIA | Curta — até 3 km | patinete |
| DISTÂNCIA | Média — 3 a 10 km | ebike |
| DISTÂNCIA | Longa — 10 a 30 km | bicicleta |
| DISTÂNCIA | Muito longa — acima de 30 km | moto |
| HABILITAÇÃO | Sim, categoria A | moto |
| HABILITAÇÃO | Não tenho e não pretendo tirar | bicicleta |
| HABILITAÇÃO | Não preciso, mas ajuda ter | ebike |
| HABILITAÇÃO | Não se aplica | patinete |
| ESFORÇO | Nenhum — prefiro motorizado | moto |
| ESFORÇO | Pouco — gosto de ajuda elétrica | ebike |
| ESFORÇO | Moderado — tudo bem pedalar | bicicleta |
| ESFORÇO | Nenhum — prefiro ir em pé | patinete |
| ORÇAMENTO | Econômico | bicicleta |
| ORÇAMENTO | Médio | patinete |
| ORÇAMENTO | Alto | ebike |
| ORÇAMENTO | Premium | moto |

## Ranking e desempate

Entrada: exatamente cinco respostas válidas, uma por pergunta. Ordenação decrescente por pontuação. Empates seguem a ordem fixa moto → bicicleta → ebike → patinete. Resultado principal é a posição 1; alternativa é a posição 2. Não há aleatoriedade nem alteração da entrada. O servidor guarda o histórico enviado pelo quiz; esse histórico não é autorização de aluguel e os textos enviados pelo cliente não são usados como validação de compatibilidade de plano.

## Casos com saídas esperadas

A sequência de perfis segue FINALIDADE, DISTÂNCIA, HABILITAÇÃO, ESFORÇO, ORÇAMENTO. Cada perfil corresponde à opção de mesmo perfil na matriz.

| Caso | Perfis de entrada | Pontos (moto/bicicleta/ebike/patinete) | Principal | Alternativa |
| --- | --- | --- | --- | --- |
| R01 | moto, moto, moto, moto, moto | 5/0/0/0 | Motocicleta | Bicicleta |
| R02 | bicicleta, bicicleta, bicicleta, bicicleta, bicicleta | 0/5/0/0 | Bicicleta | Motocicleta |
| R03 | ebike, ebike, ebike, ebike, ebike | 0/0/5/0 | E-bike | Motocicleta |
| R04 | patinete, patinete, patinete, patinete, patinete | 0/0/0/5 | Patinete elétrico | Motocicleta |
| R05 | moto, bicicleta, ebike, moto, bicicleta | 2/2/1/0 | Motocicleta | Bicicleta |
| R06 | ebike, patinete, ebike, patinete, bicicleta | 0/1/2/2 | E-bike | Patinete elétrico |
| R07 | lista vazia/incompleta | — | Erro: cinco perguntas | Sem resultado |
| R08 | perfil desconhecido | — | Erro: perfil inválido | Sem resultado |

Teste adicional percorre as 1024 combinações de cinco respostas com quatro perfis e verifica paridade com a contagem original, determinismo e preservação da entrada.

## Justificativas padrão

| Perfil | Justificativas mostradas pela interface |
| --- | --- |
| Motocicleta | Ideal para longas distâncias; alta produtividade para entregas; maior autonomia da frota |
| Bicicleta | Sem custo de combustível ou recarga; exercício e trajetos curtos; facilidade de estacionamento |
| E-bike | Pedalada assistida; trajetos médios com conforto; texto atual informa dispensa de habilitação |
| Patinete elétrico | Leve e fácil de guardar; trajetos curtos; texto atual informa dispensa de habilitação |

São descrições de apresentação, não comprovação técnica/regulatória de cada modelo. Limite conhecido: habilitação é uma preferência pontuada no quiz atual, não um filtro obrigatório; uma alternativa pode ter zero pontos. Preços e justificativas estáticos não substituem critérios de contratação. Qualquer mudança dessas regras exige nova decisão do time e atualização conjunta desta matriz/testes; esta auditoria não inventa pesos ou filtros diferentes dos existentes.
