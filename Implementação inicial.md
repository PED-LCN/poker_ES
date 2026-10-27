A implementação deverá contemplar as seguintes histórias de usuário e seus respectivos endpoints.

### **US01 – Cadastro de jogadores**

**História de usuário:** Como visitante, gostaria de criar uma conta e informar meus dados pessoais para participar das partidas de pôquer.

**Endpoint:** `POST /poker/cadastro`

**Requisição:** A API deverá receber, no corpo da requisição, um JSON com os dados necessários para cadastrar o jogador, como nome, e-mail e senha.

**Resposta esperada:** Um JSON contendo o identificador (`id`) do jogador criado.

### **US09 – Consulta de partidas disponíveis**

**História de usuário:** Como jogador, gostaria de consultar as mesas de pôquer disponíveis para encontrar uma partida da qual possa participar.

**Endpoint:** `GET /poker/partidasAbertas`

**Resposta** **esperada:** Um JSON contendo a lista de partidas disponíveis para entrada, incluindo informações relevantes, como o identificador da partida, o número de jogadores presentes e o número máximo de participantes.

### **US10 – Entrada em uma partida**

**História de usuário:** Como jogador, gostaria de entrar em uma mesa disponível para participar de uma partida de pôquer.

**Endpoint:** `POST /poker/{idPartida}/entrar`

**Parâmetro de rota:** `idPartida` — identificador da partida desejada.

**Parâmetro de consulta:** `idUsuario` — identificador da conta cadastrada que deseja entrar na partida. A entrada cria um jogador vinculado a esse usuário e à partida.

**Resposta esperada:** Um JSON informando o resultado da operação e o identificador do jogador criado na partida (`playerId`). Caso a operação não seja possível, a API deverá retornar uma resposta de erro apropriada, por exemplo, se a mesa estiver cheia ou a partida já tiver sido iniciada.

### **US12 – Início de uma partida**

**História de usuário:** Como jogador responsável pela criação de uma mesa, gostaria de iniciar a partida quando houver jogadores suficientes, para começar a jogar pôquer.

**Endpoint:** `POST /poker/{idPartida}/iniciar`

**Parâmetro de rota:** `idPartida` — identificador da partida a ser iniciada.

**Parâmetro de consulta:** `idJogador` — identificador do jogador criador da partida.

**Resposta** **esperada:** um JSON indicando se a partida foi iniciada com sucesso. A API deverá verificar se a partida existe, se o jogador responsável possui autorização para iniciá-la e se há participantes suficientes para iniciar o jogo.

### **US13 – Distribuição de cartas**

**História de usuário:** Como sistema, gostaria de distribuir cartas aleatoriamente aos jogadores quando uma nova rodada for iniciada, garantindo que cada participante receba suas cartas conforme as regras do pôquer.
