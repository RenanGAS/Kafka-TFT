# Kafka-TFT

## Sobre o Projeto

Esta aplicação tem como objetivo possibilitar o acesso ao vivo a informações de partidas de Teamfight Tactics (TFT). O TFT é um
jogo multiplayer, em que cada jogador forma um time e o objetivo é sobreviver a confrontos com jogadores adversários
durante estágios da partida, até que o último inimigo seja eliminado. De uma partida é possível extrair dados como
o tempo decorrido e o estágio corrente, e eventos relacionados a um jogador, como sua vida, dinheiro e campeões.

Para poder realizar a transmissão dessas informações ao vivo, foi idealizado um sistema baseado na plataforma Apache Kafka, em que
os jogadores seriam responsáveis por enviar as informações de suas partidas para tópicos exclusivos deles, e quem quisesse assistir
a partida de um jogador, teria que se inscrever no tópico do mesmo, para receber as informações.

O projeto foi feito em Java, e por enquanto não está integrado com a API que fornece os eventos do jogo ([Overwolf](https://overwolf.github.io/api/live-game-data/supported-games/teamfight-tactics)). Para testes, está sendo
utilizado um arquivo `.txt` no diretório `./app/Logs`, com exemplos reais de eventos do jogo, no formato `JSON`.

## Arquitetura

Na imagem abaixo é apresentada a arquitetura da aplicação, com indicações do fluxo principal de comunicação:

- `1.`: Jogador 1 envia mensagem para o tópico `Jogadores Online`, para informar que estará transmitindo sua partida.
- `2.`: Jogador 1 envia mensagens para seu tópico `Jogador 1`, com eventos da partida.
- `3.`: Amigo dos jogadores 1 e 2 se inscreve no tópico `Jogadores Online` para ver quais jogadores estão em transmissão.
- `4.`: Amigo dos jogadores vê que o Jogador 1 está transmitindo e se inscreve em seu tópico para assistir a partida.

![v2_arquitecture_v2 drawio](https://github.com/RenanGAS/Kafka-TFT/assets/68087317/c96f833c-9e32-47af-ba95-8e18e6e20bc4)

## Implementação de Tolerância a Falhas

Para a implementação de uma característica de Sistemas Distribuídos no projeto, configurou-se cada tópico com um `fator de replicação` igual
a três. Desta forma, cada partição possui duas cópias, sendo elas chamadas de `seguidoras`, e a principal de `líder`. Com esta configuração,
se faz necessário a criação de mais dois `Brokers` para conter as partições seguidoras.

Esta mudança busca prover um mecanismo de tolerância a falhas para o `cluster`. Seguindo a ilustração do cenário na imagem abaixo,
temos que a dinâmica ocorre da seguinte forma:

- Dada uma partição 0, operações de escrita e leitura serão feitas sobre a partição 0 `líder`.
- Quando uma escrita é feita, as partições 0 `seguidoras` são notificadas, e então fazem requisições para partição `líder` para se atualizarem.
- Se uma partição `líder` falha, as partições `seguidoras` começam um processo de eleição de uma nova partição `líder`. 
- Com um `fator de replicação` igual a três, tolera-se duas falhas.

![detailed_cluster drawio](https://github.com/RenanGAS/Kafka-TFT/assets/68087317/19c347e7-4ba3-4d30-8d87-6b7743df393c)

## Interface de Serviço

### Player

No momento o sistema pergunta ao jogador seu `nickname` e o arquivo em que está os logs de sua partida. Por enquanto não há outras interações com o jogador.

### Viewer

- **<ins>listar</ins>**:
    - Retorno: Lista jogadores online.

- **<ins>assistir</ins>**:
    - Parâmetro: `nickname` do jogador.
    - Retorno: Inscrição no tópico do jogador.

- **<ins>status</ins>**:
    - Retorno: Informações da partida do jogador. 

- **<ins>sair</ins>**:
    - Retorno: Cancelamento da inscrição no tópico do jogador. 

## Configurações

Cada entidade da plataforma Kafka foi configurada da seguinte forma.

### Brokers 0, 1 e 2:

- `broker.id=0` | `broker.id=1` | `broker.id=2`
    - ID único para cada `broker`.
- `listeners=PLAINTEXT://localhost:9092` | `listeners=PLAINTEXT://localhost:9093` | `listeners=PLAINTEXT://localhost:9094`
    - Endereço de cada `broker`.
- `num.partitions=3`
    - Número de partições para cada tópico criado.
- `offsets.topic.replication.factor=3`
    - Fator de replicação para as partições dos tópicos.
- `log.cleaner.enable=true`
    - Configuração necessária para utilzação de `cleanup.policy=compact` nos tópicos. 

### Tópicos:

- `cleanup.policy=compact`
    - Tópicos mantém apenas as mensagens mais recentes de uma determinada chave. Desta forma, tanto da partida como da lista de jogadores online, preserva-se as últimas informações.
    - Dada a sequência de mensagens de uma partida: vida:90 | vida:70 | estágio:2-1 | vida:60 | estágio:2-2 | dinheiro:30 | dinheiro:25, mantém-se no tópico: vida:60 | dinheiro:25 | estágio:2-2.
    - Dada a sequência de mensagens na lista de jogadores online: jogador1:online | jogador2:online | jogador1:offline | jogador3:online | jogador2:offline, mantém-se: jogador1:offline | jogador2:offline | jogador3:online.

### Produtores (Jogadores) e Consumidores (Espectadores):

- `bootstrap.servers=localhost:9092,localhost:9093,localhost:9094`
    - Endereços dos `brokers`.

- `key.serializer=org.apache.kafka.common.serialization.StringSerializer`
- `value.serializer=org.apache.kafka.common.serialization.StringSerializer`
- `key.deserializer=org.apache.kafka.common.serialization.StringDeserializer`
- `value.deserializer=org.apache.kafka.common.serialization.StringDeserializer`
    - Tratamento das mensagens (key, value) como Strings.

