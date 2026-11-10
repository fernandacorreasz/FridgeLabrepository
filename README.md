# Fridge Lab — revisão de segurança

Aplicação didática para configurar geladeiras simuladas com Spring Boot, PostgreSQL, Flyway, React e um firmware em contêiner separado. Use somente com dados fictícios e portas vinculadas a `127.0.0.1`.

## Execução local

```bash
docker compose up --build
```

Interface: http://localhost:3002 · API: http://localhost:8082 · Swagger UI: http://localhost:8082/swagger-ui/index.html

A API exige Basic Auth. Usuários didáticos: `teacher`/`laboratorio-teacher` (dispositivos 1–2) e `student`/`laboratorio-student` (dispositivo 3). Essas credenciais são apenas para o ambiente didático local; troque-as fora do laboratório. Para restaurar os dados: `docker compose down -v` e suba novamente.
