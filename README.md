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

## Anotações

- Fazer o cliente em Typescript, aproveitando o código do Overwolf

- Usar REST API da Confluent Cloud para operar o cluster Kafka criado

- Os Assinantes precisam capturar cada mensagem enviada para o tópico, sem exceções

- Ver como capturar bem o tempo de partida

- Ver o que fazer com os logs de uma partida depois que ela acaba
    - Colocar opção de armazenamento de logs de partida para replay?
    - Teria que se fazer um mecanismo de histórico para cada tópico?

## Dúvidas

- Preciso criar um tópico para cada jogador? Consigo fazer isso?

- Consigo persistir os tópicos dos jogadores? Fazer algum mecanismo de extensão de persistência?

- Fazer esquema de listar os jogadores/tópicos disponíveis para o Assinante se inscrever em novos jogadores/tópicos?

## Tarefas

- Entender código do Overwolf

- Entender REST API da Confluent Cloud

- Integrar REST API no código do Overwolf

- Modificar código do Overwolf para ser Publicador e Assinante, com várias telas e um funcionamento diferente (não iniciar por conta própria quando começa um jogo)

- Ver quais eventos capturar, de quanto em quanto tempo vamos enviar mensagens, e como vamos agrupar os eventos para formar uma mensagem com um determinado estado da partida
