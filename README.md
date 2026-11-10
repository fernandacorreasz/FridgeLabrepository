# Fridge Lab — revisão de segurança

Aplicação didática para configurar geladeiras simuladas com Spring Boot, PostgreSQL, Flyway, React e um firmware em contêiner separado. Use somente com dados fictícios e portas vinculadas a `127.0.0.1`.

## Execução local

```bash
docker compose up --build
```

Interface: http://localhost:3002 · API: http://localhost:8082 · Swagger UI: http://localhost:8082/swagger-ui/index.html

Também é possível abrir o Swagger pelo frontend em http://localhost:3002/swagger-ui/index.html. As rotas `/` e `/api` exibem as informações da API; os endpoints de dados continuam exigindo Basic Auth. Se o banco já foi iniciado antes de uma alteração de migration, recrie-o com `docker compose down -v` antes de subir novamente.

A API exige Basic Auth. Usuários didáticos: `teacher`/`laboratorio-teacher` (dispositivos 1–2) e `student`/`laboratorio-student` (dispositivo 3). Essas credenciais são apenas para o ambiente didático local; troque-as fora do laboratório. Para restaurar os dados: `docker compose down -v` e suba novamente.
