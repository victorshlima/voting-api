
# Spring Boot + Flyway + Docker (PostgreSQL)

## 1) Run localy

 - docker compose up -d
 -  http://localhost:8080/swagger-ui/index.html#/Agendas/create


# Steps
 
# Create -Agenda

    curl -i -X POST 'http://localhost:8080/agendas' \
    -H 'Content-Type: application/json' \
    -H 'X-API-Version: 1' \
    -d '{"title":"Pauta de exemplo"}'