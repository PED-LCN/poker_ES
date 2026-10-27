# poker_ES API

API REST para cadastro de usuários do projeto Poker. O projeto usa Java 21, Spring Boot, Maven e PostgreSQL.

## Requisitos

- Java 21
- Docker Desktop com Docker Compose, para executar o PostgreSQL localmente

## Iniciar o banco de dados

No terminal, na pasta raiz do projeto (onde está `docker-compose.yml`), execute:

```powershell
docker compose up -d
```

O Compose inicia PostgreSQL 16 com estes dados locais:

| Configuração | Valor |
|---|---|
| Host/porta | `localhost:5432` |
| Banco | `poker-api` |
| Usuário | `postgres` |
| Senha | `postgres` |

A aplicação usa essa configuração em `src/main/resources/application.yaml`. Ao iniciar, o Flyway aplica as migrações SQL de `src/main/resources/db/migration` e cria/atualiza a tabela necessária. Não é preciso criar a tabela manualmente.

Para parar o banco sem apagar os dados:

```powershell
docker compose down
```

Os dados ficam em um volume Docker. Para apagar o banco local e todos os seus dados, use `docker compose down -v`.

## Executar a API

Na raiz do projeto, abra outro terminal e execute:

```powershell
./mvnw.cmd spring-boot:run
```

A primeira execução pode levar alguns minutos enquanto Maven baixa as dependências. Quando o servidor iniciar, a API ficará disponível em `http://localhost:8081`.

Para encerrar, pressione `Ctrl+C` no terminal da aplicação. O banco pode continuar ativo para a próxima execução.

## Fazer uma requisição

O endpoint implementado para testar o cadastro é:

```http
POST http://localhost:8081/poker/cadastro
Content-Type: application/json
```

Exemplo com `curl` (PowerShell ou outro terminal):

```powershell
curl.exe -X POST http://localhost:8081/poker/cadastro `
  -H "Content-Type: application/json" `
  -d '{"username":"jogador1","email":"jogador1@example.com","password":"senha1234"}'
```

Uma criação bem-sucedida retorna `201 Created` e os dados públicos do usuário criado (id, username, email e role). A senha não é devolvida; ela é armazenada codificada. Username deve ter de 3 a 50 caracteres, email deve ser válido e a senha deve ter no mínimo 8 caracteres. Username ou email já cadastrados retornam `409 Conflict`; dados inválidos retornam `400 Bad Request`.

Você também pode enviar a mesma requisição pelo Postman ou Insomnia, configurando o método `POST`, a URL acima, `Content-Type: application/json` e o JSON do exemplo no corpo (*Body*, modo *raw/JSON*).

## Escopo atual

Neste momento, o endpoint disponível é o cadastro de usuário (`POST /poker/cadastro`). Outros recursos de poker, como partidas, login ou operações de jogo, ainda não estão expostos como endpoints nesta versão.
