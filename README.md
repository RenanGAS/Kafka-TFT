# Kafka-TFT

## Sobre o projeto

Esta aplicação tem como objetivo possibilitar o acesso ao vivo a informações de partidas de Teamfight Tactics (TFT). O TFT é um
jogo de multiplayer, em que cada jogador forma um time com peças do jogo, e o objetivo é sobreviver aos confrontos entre os jogadores
durante os vários estágios da partida, até que o último jogador adversário seja eliminado. De uma partida é possível extrair dados como
o tempo decorrido e o estágio corrente, e eventos relacionados a um jogador, como sua vida, dinheiro e peças.

Para poder realizar a transmissão dessas informações ao vivo, foi idealizado um sistema baseado na plataforma Apache Kafka, em que
os jogadores seriam responsáveis por enviar as informações de sua partida para tópicos exclusivos deles, e quem quisesse assistir
a partida de um jogador, teria que se inscrever no tópico do mesmo, para receber as informações.

O projeto foi feito em Java, e por enquanto não está integrado com a API que fornece os eventos do jogo. Para testes, está sendo
utilizado um arquivo `.txt` no diretório `./app/Logs`, com exemplos reais de eventos emitidos, no formato `JSON`.

## Arquitetura

![v2_arquitecture](https://github.com/RenanGAS/Kafka-TFT/assets/68087317/f91cbe45-4cfb-43c9-8879-0fdc67029959)

![cluster_detailed](https://github.com/RenanGAS/Kafka-TFT/assets/68087317/6e296846-5cde-4c94-916f-f0a90aa06879)

## Implementação de tolerância a falhas

- Replicação de partições

## Interface de serviço

## Configuração

### Broker:

- auto.leader.rebalance.enable
- leader.imbalance.check.interval.seconds
- leader.imbalance.per.broker.percentage
- log.dir
- min.insync.replicas
- default.replication.factor
- num.partitions

### Topic:

- cleanup.policy: compact

### Player:

- batch.size
- security.protocol
- partitioner.adaptive.partitioning.enable

### Viewer:

- allow.auto.create.topics
- auto.offset.reset
- enable.auto.commit
- security.protocol

