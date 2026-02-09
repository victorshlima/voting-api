# Voting API

API para votacao de pautas. Fluxo basico: criar pauta -> abrir sessao da pauta -> registrar votos. O resultado e calculado automaticamente por scheduler e gravado no banco.

## Requisitos
Para executar o projeto localmente, é necessário ter os seguintes itens instalados:

**Docker**

Ferramenta para criação e execução de containers.
Documentação oficial (Linux):
https://docs.docker.com/engine/install/

## Stack e motivos
- Spring Boot 3.5 (Java 25): base REST, validacao, transacoes e observabilidade.
- OpenAPI/Swagger: contrato vivo e testes rapidos via UI.
- PostgreSQL + JPA/Hibernate: integridade forte, constraints (1 sessao por pauta, 1 voto por associado/sessao) e consultas consistentes.
- Flyway: versionamento de schema e historico de migracoes.
- ShedLock: garante que o scheduler rode uma unica vez entre instancias (evita concorrencia e double write).
- Versionamento por header `X-API-Version: 1`: compatibilidade com gateway e evolucao sem quebrar clientes.
- Perfil unico de properties e execução direta do conteiner - simplicidade para teste/avaliaç
## Regras de negocio
- Cada pauta pode ter no maximo 1 sessao de votacao.
- Sessao tem inicio imediato (UTC) e fim em `durationMinutes` (default 1, max 60).
- Voto so e aceito enquanto a sessao esta aberta.
- Cada associado vota no maximo 1 vez por sessao, mas pode votar em sessoes diferentes.
- O voto aceita `SIM/NAO`.
- Resultado:
  - empate ou zero votos -> EMPATE
  - acima do threshold -> APROVADA
  - abaixo do threshold -> REPROVADA
- Resultado e gravado em `agenda.result` ao fim da sessao (scheduler).

## Configuracao principal
- `voting-session.default-duration-minutes` - Tempo minimo padrao para sessao de votacao - default 1
- `voting-session.max-duration-minutes` - Tempo maximo para sessao de votacao - default 60
- `voting-result.approval-threshold-percent` - Valor percentual para aprovacao - default 50 
- `voting-result.scheduler-interval` - Tempo da execucao do scheduler - default 10 a cada minutos

## Endpoints (todos exigem `X-API-Version: 1`)
- `POST /agendas` -> cria pauta
- `GET /agendas` -> lista pautas com todos os campos (teste)
- `POST /voting-sessions/{agendaId}` -> cria sessao para a pauta
- `GET /voting-sessions/open` -> lista sessoes abertas no body (para facilitar testes/avaliação)
- `POST /voting-sessions/{votingSessionId}/votes` -> registra voto
- `POST /voting-sessions/{votingSessionId}/votes/test` -> registra voto com UUID gerado automaticamente (teste/avaliação))


## Como rodar local

1) Subir a API:
```
./gradlew bootRun
```

## Swagger / OpenAPI
- UI: `http://localhost:8080/swagger-ui/index.html`
- Spec: `http://localhost:8080/v3/api-docs`
- No Swagger UI, o header `X-API-Version` preencher a versão como 1`.


## Fluxo de votacao cURL ou SWAGGER

1) Criar agenda ("pauta"), `agendaId` da agenda é retornado no body):
```
curl -i -X POST 'http://localhost:8080/agendas' \
  -H 'accept: application/json' \
  -H 'X-API-Version: 1' \
  -H 'Content-Type: application/json' \
  -d '{
  "title": "Reforma do estatuto"
}'
```

Resposta (body):
```
{
  "agendaId": 33,
  "title": "Reforma do estatuto"
}
```

2) Use o `agendaId` retornado para inciar uma voting-sessions `sessao` de votos:
```
curl -i -X POST 'http://localhost:8080/voting-sessions/1' \
  -H 'accept: application/json' \
  -H 'X-API-Version: 1' \
  -H 'Content-Type: application/json' \
  -d '{
  "durationMinutes": 10
}'
```

Resposta (body):
```
{
  "sessionId": 1,
  "startsAt": "2025-01-01T00:01:00Z",
  "endsAt": "2025-01-01T00:10:00Z"
}
```
3.1) Voto com gerção se associado automático
  Apos abrir a sessão e possivel Votar com o `sessionId` da sessao aberta:
- Usuário é um UUID, para fins de simulação u usuario é gerado automaticamente
  postman request POST 'http://localhost:8080/voting-sessions/5/votes/test' \
  --header 'accept: application/json' \
  --header 'X-API-Version: 1' \
  --header 'Content-Type: application/json' \
  --body '{
  "vote": "SIM"
  }'

3.2) Voto com geração se associado manual
 O id do associado é um UUID, ele pode ser gerado pelo site, possibilitando simular diversos associados
https://www.uuidgenerator.net/version4
```
curl -i -X POST 'http://localhost:8080/voting-sessions/1/votes' \
  -H 'accept: application/json' \
  -H 'X-API-Version: 1' \
  -H 'Content-Type: application/json' \
  -d '{
  "associateId": "1a2b3c4d-5e6f-7a8b-9c0d-1e2f3a4b5c6d",
  "vote": "SIM"
}'
```
Resposta (body):
```
{
  "agendaId": 33,
  "votingSessionId": 5
}
```

(Opcional) Listar pautas - apenas para facilitar pesquisa do banco / validacao:
```
curl -i -X GET 'http://localhost:8080/agendas' \
  -H 'X-API-Version: 1'
```

Resposta (body):
```
[
  {
    "id": 33,
    "title": "Reforma do estatuto",
    "result": null,
    "resultCreatedAt": null,
    "createdAt": "2025-01-01T00:00:00Z"
  }
]
```

(Opcional) Listar sessoes abertas - apenas para facilitar pesquisa do banco / validacao:
```
curl -i -X GET 'http://localhost:8080/voting-sessions/open' \
  -H 'X-API-Version: 1'
```

Resposta (body):
```
[
  {
    "sessionId": 5,
    "agendaTitle": "Pauta voto"
  }
]
```

## Resultado automatico
O scheduler roda em `voting-result.scheduler-interval` e grava o resultado em `agenda.result`.
Para verificar:
```
docker exec -it voting-postgres psql -U secret -d votingdb \
  -c "select id, des_title, result, dat_create_result from agenda;"
```

## Notas rapidas
- Criacao de agenda retorna JSON com `agendaId` e `title`.
- Criacao de sessao retorna JSON com `sessionId`, `startsAt` e `endsAt`.
- Criacao de voto retorna JSON com `agendaId` e `votingSessionId` (nao retorna `voteId`).
- `associateId` e um UUID (nao usamos documento/CPF como identificador por compliance).
- Para testes rapidos, use `POST /voting-sessions/{votingSessionId}/votes/test` (UUID gerado automaticamente).
- Validacoes retornam erro padronizado em `ApiErrorResponse` com `apiVersion`.

## Testes
- Existem testes de integracao para validar os cenarios principais.
- Para executar: `./gradlew test`

## Metricas

http://localhost:8080/actuator/metrics

## Evolucao
- Habilitar HTTP2
- Cache para a sessao da votacao
- Add o chekcStyle e sonar no projeto
- Metricas com grafana
