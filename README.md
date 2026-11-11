# Fridge Lab — revisão de segurança

Essa aplicação é um sistema  para configurar geladeiras, desenvolvido de forma didática para praticas de segurança.
Teste educacional

Tecnologias usadas: Spring Boot, PostgreSQL, Flyway, React e um firmware em contêiner separado. 

## Execução local

```bash
docker compose up --build
```

Interface: http://localhost:3002 · API: http://localhost:8082 · Swagger UI: http://localhost:8082/swagger-ui/index.html

Também é possível abrir o Swagger pelo frontend em http://localhost:3002/swagger-ui/index.html. As rotas `/` e `/api` exibem as informações da API; os endpoints de dados continuam exigindo Basic Auth. Se o banco já foi iniciado antes de uma alteração de migration, recrie-o com `docker compose down -v` antes de subir novamente.
A API exige Basic Auth. Usuários didáticos: `teacher`/`laboratorio-teacher` (dispositivos 1–2) e `student`/`laboratorio-student` (dispositivo 3), 
Para restaurar os dados: `docker compose down -v` e suba novamente.

## Acesso pelo DBeaver

O Compose publica o PostgreSQL somente em `127.0.0.1`, para uso local pelo DBeaver. Crie uma conexão PostgreSQL com os valores abaixo:

| Campo | Valor |
|---|---|
| Host | `localhost` ou `127.0.0.1` |
| Port | `5432` |
| Database | `geladeira_lab` |
| Username | `postgres` |
| Password | `laboratorio_postgres` |
| SSL mode | `disable` |

Após o primeiro `docker compose up --build`, também existe a conta operacional `fridge_app` com senha `training-only`. Ela possui apenas permissões de leitura/atualização da tabela de dispositivos; para inspecionar migrations e schema use `postgres`. Se a porta 5432 já estiver ocupada por outro PostgreSQL, encerre o serviço local antes de subir o laboratório.

## Rotas principais

Todas as rotas abaixo usam Basic Auth, exceto `/`, `/api`, `/swagger-ui/**` e `/v3/api-docs/**`.

| Método | Rota | Uso |
|---|---|---|
| GET | `/api/devices?name=Hub` | Listar e buscar geladeiras do usuário |
| GET | `/api/devices/{id}` | Consultar uma geladeira |
| POST | `/api/devices` ou `/api/devices/register` | Cadastrar uma geladeira |
| PUT | `/api/devices/{id}` | Editar dados, localização e firmware |
| DELETE | `/api/devices/{id}` | Excluir uma geladeira do usuário |
| PUT | `/api/devices/{id}/configuration` | Enviar temperatura, modo e nota ao firmware |
| PUT | `/api/devices/{id}/firmware` | Salvar código C++ do firmware didático |
| GET | `/api/firmware/state` | Consultar estado aplicado no simulador |

Exemplo para cadastrar:

```json
{
  "name": "Câmara de treinamento",
  "temperatureC": 4,
  "mode": "ECO",
  "note": "Amostras fictícias",
  "country": "Brasil",
  "city": "São Paulo",
  "latitude": -23.55052,
  "longitude": -46.633308,
  "firmwareCode": "void setup() { pinMode(LED_BUILTIN, OUTPUT); }\nvoid loop() { readTemperature(); maintainTarget(); delay(1000); }"
}
```

Exemplo para aplicar uma configuração no firmware:

```json
{
  "temperatureC": 2,
  "mode": "ECO",
  "note": "Configuração de laboratório"
}
```

O banco é populado pela migration `V4__seed_twenty_fridges.sql` com 20 itens fictícios, cidades, países, latitude/longitude e um sketch C++ de exemplo. O frontend tem telas independentes para visão geral/mapa, lista e CRUD, cadastro e laboratório de firmware.
