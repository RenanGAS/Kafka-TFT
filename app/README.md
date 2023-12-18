## Documentação

### Dependências

- Java 17+ 
- Maven
- Servidor Kafka
    - https://kafka.apache.org/documentation/#quickstart

### Para compilação

- `make compile`

### Exemplo de uso

- `make admin`

- `make player`
    - Nickname do jogador: fulano
    - Caminho do arquivo de logs: logs_example.txt
    - Transmissão em andamento...
    - CTRL-C

- `make viewer`
    - Use o comando `listar` para ver os jogadores em transmissão
    - `listar`
    - Jogadores em transmissão: SaKASOJA, Darkpulser, Faker, Pijack
    - `assistir`
    - Nome do jogador: SaKASOJA
    - Entrando na transmissão de SaKASOJA...
    - `status`
    - Jogador: SaKASOJA, Tempo de partida: 30 min, Vida: 90, Dinheiro: 40, Estágio: 2-1, Tabuleiro: Neeko (2), Jhin (3)
    - `sair`
    - Nickname do jogador: SaKASOJA
    - Saindo da transmissão de SaKASOJA...
    - CTRL-C

