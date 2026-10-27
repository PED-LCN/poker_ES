# poker_ES API

API REST para cadastro de usuários e operações iniciais de partidas de pôquer. O projeto usa Java 21, Spring Boot, Maven e PostgreSQL.

## Requisitos

- Java 21 (`java -version` deve mostrar a versão 21)
- Windows ou macOS: Docker Desktop com Docker Compose; Linux: Docker Engine com o plugin Docker Compose
- Terminal na pasta raiz do repositório, onde estão `docker-compose.yml` e os scripts do Maven Wrapper

## Iniciar o banco de dados

Inicie o Docker Desktop (Windows/macOS) ou o serviço Docker (Linux). Na pasta raiz do projeto, execute o mesmo comando em qualquer sistema:

```sh
docker compose up -d
```

O Compose inicia PostgreSQL 16 com estes dados locais:

| Configuração | Valor |
|---|---|
| Host/porta | `localhost:5432` |
| Banco | `poker-api` |
| Usuário | `postgres` |
| Senha | `postgres` |

A aplicação usa essa configuração em `src/main/resources/application.yaml`. Ao iniciar, o Flyway aplica as migrações SQL de `src/main/resources/db/migration`. Não é preciso criar as tabelas manualmente.

Para parar o banco sem apagar os dados:

```sh
docker compose down
```

Os dados ficam em um volume Docker. Para apagar o banco local e todos os seus dados, use `docker compose down -v`.

## Executar a API

Na raiz do projeto, abra outro terminal e execute o comando do seu sistema:

**Windows (PowerShell):**

```powershell
.\mvnw.cmd spring-boot:run
```

**Linux/macOS:**

```sh
sh ./mvnw spring-boot:run
```

Os scripts usam o Maven Wrapper do repositório; não é necessário instalar Maven separadamente. Se você já tiver Maven instalado, `mvn spring-boot:run` também funciona em qualquer sistema. A primeira execução pode levar alguns minutos enquanto as dependências são baixadas. Quando o servidor iniciar, a API ficará disponível em `http://localhost:8081`.

Para encerrar, pressione `Ctrl+C` no terminal da aplicação. O banco pode continuar ativo para a próxima execução.

## Usuário e jogador

O cadastro cria uma conta (`User`) e devolve seu `id`. Ao entrar em uma partida, essa conta recebe um `Player` próprio daquela partida. A resposta da entrada devolve o `playerId`, usado para iniciar a partida quando o jogador é seu criador e para consultar suas cartas. O mesmo usuário pode ter jogadores distintos em partidas diferentes.

## Endpoints

### Cadastro — US01

Crie uma conta com:

```http
POST http://localhost:8081/poker/cadastro
Content-Type: application/json
```

**Windows (PowerShell):**

```powershell
$dados = @{ username = 'jogador1'; email = 'jogador1@example.com'; password = 'senha1234' }
Invoke-RestMethod -Method Post -Uri 'http://localhost:8081/poker/cadastro' `
  -ContentType 'application/json' -Body ($dados | ConvertTo-Json)
```

**Linux/macOS:**

```sh
curl -X POST http://localhost:8081/poker/cadastro \
  -H 'Content-Type: application/json' \
  -d '{"username":"jogador1","email":"jogador1@example.com","password":"senha1234"}'
```

Uma criação bem-sucedida retorna `201 Created`, por exemplo:

```json
{"id":1,"userName":"jogador1","email":"jogador1@example.com","role":"PLAYER"}
```

Esse `id` identifica o usuário; o jogador da partida é criado quando ele entra nela. A senha não é devolvida; ela é armazenada codificada. Username deve ter de 3 a 50 caracteres, email deve ser válido e a senha deve ter no mínimo 8 caracteres. Username ou email já cadastrados retornam `409 Conflict`; dados inválidos retornam `400 Bad Request`.

Você também pode enviar a mesma requisição pelo Postman ou Insomnia, configurando o método `POST`, a URL acima, `Content-Type: application/json` e o JSON do exemplo no corpo (*Body*, modo *raw/JSON*).

### Partidas disponíveis — US09

```http
GET http://localhost:8081/poker/partidasAbertas
```

Retorna `200 OK` com partidas em espera que ainda tenham vaga. Exemplo de item da lista:

```json
{"matchId":1,"state":"WAITING_PLAYERS","currentPlayers":1,"maxPlayers":9,"creatorId":1}
```

`creatorId` é o identificador do `Player` criador da partida. Uma lista vazia é retornada como `[]`. Mesas cheias e partidas iniciadas não aparecem.

### Entrar em uma partida — US10

Use o `id` do **usuário** cadastrado em `idUsuario`:

```http
POST http://localhost:8081/poker/1/entrar?idUsuario=2
```

Uma entrada bem-sucedida retorna `200 OK` e um `playerId` novo para essa partida:

```json
{"success":true,"message":"Player successfully joined the match.","matchId":1,"playerId":2}
```

Partida ou usuário inexistente retorna `404`; entrada duplicada ou mesa cheia retorna `409`; partida iniciada retorna `400`.

### Iniciar uma partida — US12

Use em `idJogador` o `playerId` do criador da partida:

```http
POST http://localhost:8081/poker/1/iniciar?idJogador=1
```

O sucesso retorna `200 OK` com `{"success":true,"message":"Match started and cards distributed successfully."}`. Criador incorreto retorna `403`; jogadores insuficientes, `422`; partida já iniciada, `409`; partida inexistente, `404`. O início cria a primeira rodada e distribui duas cartas a cada participante.

### Cartas da rodada — US13

A distribuição inicial ocorre automaticamente ao iniciar a partida. Para consultar as duas cartas de um jogador da rodada:

```http
GET http://localhost:8081/poker/rodadas/1/jogadores/2/cartas
```

Exemplo de resposta `200 OK`:

```json
{"roundId":1,"playerId":2,"cards":[{"rank":"AS","suit":"COPAS","dealOrder":2},{"rank":"REI","suit":"PAUS","dealOrder":4}]}
```

`POST /poker/rodadas/{idRodada}/distribuir` distribui cartas somente a uma rodada existente que ainda esteja aguardando distribuição; tentar distribuir novamente retorna `409`. O identificador da rodada não é devolvido pela resposta de início nesta versão.

## Escopo atual

Ainda não há endpoints para criar partidas/mesas ou novas rodadas, nem para autenticar usuários. Por isso, o fluxo completo não pode ser iniciado apenas pelas rotas públicas: é preciso ter uma partida e seu criador previamente cadastrados no banco. A consulta de cartas recebe o `playerId` na URL e ainda não verifica a identidade de quem faz a requisição.
