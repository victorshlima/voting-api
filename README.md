# Voting API

API para votacao de pautas. Fluxo basico: criar pauta -> abrir sessao da pauta -> registrar votos. O resultado e calculado automaticamente por scheduler e gravado no banco.

## Stack e motivos
- Spring Boot 3.5 (Java 25): base REST, validacao, transacoes e observabilidade.
- OpenAPI/Swagger: contrato vivo e testes rapidos via UI.
- PostgreSQL + JPA/Hibernate: integridade forte, constraints (1 sessao por pauta, 1 voto por associado/sessao) e consultas consistentes.
- Flyway: versionamento de schema e historico de migracoes.
- ShedLock: garante que o scheduler rode uma unica vez entre instancias (evita concorrencia e double write).
- Versionamento por header `X-API-Version: 1`: compatibilidade com gateway e evolucao sem quebrar clientes.

## Regras de negocio (alto nivel)
- Cada pauta pode ter no maximo 1 sessao de votacao.
- Sessao tem inicio imediato (UTC) e fim em `durationMinutes` (default 1, max 60).
- Voto so e aceito enquanto a sessao esta aberta.
- Cada associado vota no maximo 1 vez por sessao, mas pode votar em sessoes diferentes.
- O voto aceita `SIM/NAO/YES/NO`.
- Resultado:
  - empate ou zero votos -> EMPATE
  - acima do threshold -> APROVADA
  - abaixo do threshold -> REPROVADA
- Resultado e gravado em `agenda.result` ao fim da sessao (scheduler).

## Configuracao principal
- `voting-session.default-duration-minutes` e `voting-session.max-duration-minutes`
- `voting-result.approval-threshold-percent` e `voting-result.scheduler-interval`
- `spring.datasource.*` (Postgres local)
- `server.port=8080`

Arquivo: `src/main/resources/application.yaml`

## Endpoints (todos exigem `X-API-Version: 1`)
- `POST /agendas` -> cria pauta
- `PUT /agendas/{agendaId}` -> atualiza pauta (somente se nao iniciou)
- `DELETE /agendas/{agendaId}` -> remove pauta (somente se nao iniciou)
- `POST /voting-sessions/{agendaId}` -> cria sessao para a pauta
- `POST /voting-sessions/{votingSessionId}/votes` -> registra voto
- `POST /voting-sessions/{votingSessionId}/votes/test` -> registra voto com UUID gerado automaticamente (teste)
- `GET /test/uuid` -> gera UUID para testes

## Swagger / OpenAPI
- UI: `http://localhost:8080/swagger-ui/index.html`
- Spec: `http://localhost:8080/v3/api-docs`

## Como rodar local
1) Subir o Postgres:
```
docker compose up -d postgres
```
2) Subir a API:
```
./gradlew bootRun
```

## Fluxo de votacao (cURL)
1) Criar pauta:
```
curl -i -X POST http://localhost:8080/agendas \
  -H 'Content-Type: application/json' \
  -H 'X-API-Version: 1' \
  -d '{"title":"Reforma do estatuto"}'
```

2) Criar sessao (use o `agendaId` retornado):
```
curl -i -X POST http://localhost:8080/voting-sessions/{agendaId} \
  -H 'Content-Type: application/json' \
  -H 'X-API-Version: 1' \
  -d '{"durationMinutes": 2}'
```

3) Votar com UUID automatico (teste, sem gerar UUID manual):
```
curl -i -X POST http://localhost:8080/voting-sessions/{sessionId}/votes/test \
  -H 'Content-Type: application/json' \
  -H 'X-API-Version: 1' \
  -d '{"vote":"SIM"}'
```

4) Opcional: gerar UUID e votar com identificador externo:
```
curl -s http://localhost:8080/test/uuid -H 'X-API-Version: 1'
```

```
curl -i -X POST http://localhost:8080/voting-sessions/{sessionId}/votes \
  -H 'Content-Type: application/json' \
  -H 'X-API-Version: 1' \
  -d '{"associateId":"<uuid>","vote":"SIM"}'
```

## Resultado automatico
O scheduler roda em `voting-result.scheduler-interval` e grava o resultado em `agenda.result`.
Para verificar:
```
docker exec -it voting-postgres psql -U secret -d votingdb \
  -c "select id, des_title, result, dat_create_result from agenda;"
```

## Notas rapidas
- `associateId` e um UUID fornecido por cliente externo (nao usamos documento como identificador).
- Para testes rapidos, use `POST /voting-sessions/{votingSessionId}/votes/test` (UUID gerado automaticamente).
- Validacoes retornam erro padronizado em `ApiErrorResponse` com `apiVersion`.
