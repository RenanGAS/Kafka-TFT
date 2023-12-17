# Kafka-TFT

Aplicação para o acesso ao vivo a informações de partidas de Teamfight Tactics

## Arquitetura

![kafka_tft_architecture drawio](https://github.com/RenanGAS/Kafka-TFT/assets/68087317/5c22c9a2-f24d-4158-86b4-b459db0aa19c)

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

### Player:

- batch.size
- security.protocol
- partitioner.adaptive.partitioning.enable

### Viewer:

- allow.auto.create.topics
- auto.offset.reset
- enable.auto.commit
- security.protocol

