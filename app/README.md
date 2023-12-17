## Documentação

### Dependências

- Java 17+ 
    - Guia:
- Maven
    - Guia:
- Servidor Kafka
    - Guia: 

### Para compilação

- `make compile`

### Exemplo de uso

- make player
    - Nome do jogador: fulano
    - Caminho do arquivo de logs: arquivo_logs_tft.txt
    - Use o comando `transmitir` para começar a transmissão
    - Transmissão em andamento...
    - Use o comando `terminar` para parar a transmissão
    - terminar
    - Fim da transmissão...

- make viewer
    - Use o comando `listar` para ver os jogadores em transmissão
    - listar
    - Jogadores em transmissão: SaKASOJA, Darkpulser, Faker, Pijack
    - Use o comando `assistir(nome_jogador)` para ver a partida do jogador
    - assistir(SaKASOJA)
    - Começando coleta de dados...
    - Use o comando `status` para ver informações da partida
    - Jogador: SaKASOJA, Tempo de partida: 30 min, Vida: 90, Dinheiro: 40, Estágio: 2-1, Tabuleiro: Neeko (2), Jhin (3)
    - Use o comando `sair` para parar de assistir a partida
    - sair
    - Saindo da partida...
