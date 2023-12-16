# Kafka-TFT

Aplicação para o acesso ao vivo a informações de partidas de Teamfight Tactics

## Overwolf

Overwolf é uma plataforma que oferece suporte para o desenvolvimento de aplicações 
para jogos online. Um de seus serviços é a disponibilização de dados em tempo real
de partidas, como a pontuação dos times, os status dos jogadores e eventos (conquistas
alcançadas por uma equipe, eliminação de jogadores).

Link: <https://overwolf.github.io/api/live-game-data/supported-games/teamfight-tactics>

## Arquitetura

![kafka_tft_architecture drawio](https://github.com/RenanGAS/Kafka-TFT/assets/68087317/5c22c9a2-f24d-4158-86b4-b459db0aa19c)

## Interfaces de Serviço

### Publicador

- Cria tópico do jogador se não existe

- Publica informações da partida do jogador

- Colocar opção de começar/parar o serviço. Quando iniciado, começa-se a escutar o início de uma partida

- Tratar os logs para que seja enviada uma mensagem com todas as informações gerais da partida

- Ver o que fazer quanto à permissão de publicação pro cluster Kafka na Confluent Cloud

### Assinantes

- Procura tópico do jogador

- Pega informações da partida do jogador

- Tela com tópicos incritos para ativação da captura de informações

- Tela com lista de tópicos existentes para inscrição em tópicos novos

- Tela com a atualização das informações dos tópicos ativados
 
- Ver o que fazer quanto à permissão de inscrição pro cluster Kafka na Confluent Cloud

## Uso 

- 1: Jogador quer streamar suas partidas 

- 2: Jogador roda o Aplicativo e seleciona opção para começar a streamá-las

- 3: Aplicativo começa a esperar pelo início de uma partida

- 4: Jogador começa uma partida

- 5: Aplicativo começa a enviar informações da partida para o tópico do Jogador

- 6: Espectador quer assistir partidas do Jogador

- 7: Espectador roda o Aplicativo e seleciona opção para assistir partidas do Jogador

- 8: Aplicativo começa a pegar e exibir infomações da partida, que se encontram no tópico do Jogador

- 9: Enquanto o Jogador não parar o serviço de streaming do Aplicativo, continua-se a escutar por partidas e a enviar informações delas quando começarem

- 10: Enquanto o Jogador está streamando o Espectador consegue ver informações de partidas dele

- 11: Quando o Jogador parar o serviço de streaming, o Aplicativo reseta o tópico mas não o deleta

- 12: Quando um Jogador parar de streamar, o Espectador não vê mais informações de partidas na tela em que as informações das partidas apareciam

- OBS: Como acontece na Twitch, mas sem stream de vídeo e áudio

## Anotações

- Fazer o cliente em Typescript, aproveitando o código do Overwolf

- Usar REST API da Confluent Cloud para operar o cluster Kafka criado

- Os Assinantes precisam capturar cada mensagem enviada para o tópico, sem exceções

- Ver como capturar bem o tempo de partida

- Ver o que fazer com os logs de uma partida depois que ela acaba
    - Colocar opção de armazenamento de logs de partida para replay?
    - Teria que se fazer um mecanismo de histórico para cada tópico?

- Para preservar ordem, as mensagens tem que estar num mesmo tópico
    - O que precisa de ordem:
        - Vida
        - Board
        - Tempo

- Qual a dificuldade?
    - R: Como vou mandar as mensagens? De 2 em 2 minutos? Queria atualizar as infos de um jogador independentemente

## Dúvidas

- Preciso criar um tópico para cada jogador? Consigo fazer isso?

- Consigo persistir os tópicos dos jogadores? Fazer algum mecanismo de extensão de persistência?

- Fazer esquema de listar os jogadores/tópicos disponíveis para o Assinante se inscrever em novos jogadores/tópicos?

- Os logs de um jogador precisam ser homogêneos? Consigo enviar logs de coisas diferentes para um mesmo tópico?

## Tarefas

- Entender código do Overwolf

- Entender REST API da Confluent Cloud

- Fazer um parser para os logs de game_info
    - Pra isso:
        - Fazer pra não fechar a janela quando acaba o jogo
        - Fazer um parser e formatar os dados numa mensagem só
        - Ver como mandar os logs para um cluster Kafka

- Integrar REST API no código do Overwolf

- Modificar código do Overwolf para ser Publicador e Assinante, com várias telas e um funcionamento diferente (não iniciar por conta própria quando começa um jogo)

- Ver quais eventos capturar, de quanto em quanto tempo vamos enviar mensagens, e como vamos agrupar os eventos para formar uma mensagem com um determinado estado da partida

## Configurações

### Broker:

- auto.leader.rebalance.enable
- leader.imbalance.check.interval.seconds
- leader.imbalance.per.broker.percentage
- log.dir
- min.insync.replicas
- default.replication.factor
- num.partitions

### Producer:

- batch.size
- security.protocol
- partitioner.adaptive.partitioning.enable

### Consumer:

- allow.auto.create.topics
- auto.offset.reset
- enable.auto.commit
- security.protocol
