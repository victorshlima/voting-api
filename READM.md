
# Spring Boot + Flyway + Docker (PostgreSQL)





## 1) Run localy

 - docker compose up -d
 -  http://localhost:8080/swagger-ui/index.html#/Agendas/create


# Steps
 
# Create -Agenda

 - Create a agenda

    curl -i -X POST 'http://localhost:8080/agendas' \
    -H 'Content-Type: application/json' \
    -H 'X-API-Version: 1' \
    -d '{"title":"Pauta de exemplo"}'

# Initialize session -


# Vote 



# Get the results



Swagger

http://localhost:8080/swagger-ui/index.html

Actuator

http://localhost:8080/actuator/metrics/hikaricp.connections.max
http://localhost:8080/actuator/metrics/hikaricp.connections.active
http://localhost:8080/actuator/metrics/hikaricp.connections.idle
