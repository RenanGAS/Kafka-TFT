/**
 *
 * Autor: Renan Guensuke Aoki Sakashita
 *
 */

package project;

import org.apache.kafka.clients.consumer.*;
import java.time.Duration;
import java.io.*;
import java.util.*;

/**
 * Executa operações de listagem de usuários online, inscrição no tópico de um jogador,
 * requisição de informações da partida, e cancelamento de inscrição.
 *
 */
public class ConsumerThread extends Thread {
    final Consumer<String, String> consumerListPlayers;
    final Consumer<String, String> consumerMatches;
    List<String> subscribedTopics;
    List<String> onlinePlayers;

    public ConsumerThread(String configFile) {

        // Configuração do consumidor
        Properties props = null;
        try {
            // Carrega configurações em "config.properties"
            props = AdminTasks.loadConfig(configFile);
        } catch (IOException ioe) {
            System.out.println("\nERRO: " + ioe.getMessage() + "\n");
        }

        // Grupo do consumidor
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "consumer-group-0");

        // Em caso de falha, pegar a mensagem de offset mais recente
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        // Inicialização dos consumidores para os tópios "Jogadores Online" e "Jogador"
        this.consumerListPlayers = new KafkaConsumer<>(props);
        this.consumerMatches = new KafkaConsumer<>(props);

        // Lista de tópicos inscritos
        this.subscribedTopics = new ArrayList<>();

        // Inscrição no tópico "Jogadores Online"
        this.consumerListPlayers.subscribe(Arrays.asList("listPlayers"));
        this.subscribedTopics.add("listPlayers");
    }

    void handleViewerCommands(String command, Scanner scanner) {
        switch (command) {
            case "listar":

                System.out.println("\nJogadores em transmissão:\n");

                // Consumo do tópico "Jogadores Online"
                // São feitas 3 tentativas
                for (int i = 0; i < 3; i++) {
                    System.out.format("\nDEBUG -> Tentativa %d\n", i);

                    // Se não tiver mensagens ou der algum erro, espera por um timeout de um segundo
                    ConsumerRecords<String, String> recordsListPlayers = this.consumerListPlayers.poll(Duration.ofMillis(1000));
                    for (ConsumerRecord<String, String> record : recordsListPlayers) {
                        String key = record.key();
                        String value = record.value();

                        System.out.format("\nDEBUG -> message: key=%s, value=%s\n", key, value);

                        // Se o jogador está online, adiciona na lista de jogadores Online
                        // Se não, procura na lista e o remove
                        if (value == "online") {
                            this.onlinePlayers.add(key);
                        } else if (value == "offline") {
                            this.onlinePlayers.remove(key);
                        }

                        // Exibe a lista de jogadores online
                        if (this.onlinePlayers != null) {
                            for (String player : this.onlinePlayers) {
                                System.out.format("* %s\n", player);
                            }
                        }
                    }
                }
                break;
            case "assistir":

                // Nickname do jogador
                System.out.print("\nNickname do jogador: ");
                String nicknameToWatch = scanner.nextLine(); 

                // Inscrição no tópico do jogador
                if (!this.subscribedTopics.contains(nicknameToWatch)) {
                    this.consumerMatches.subscribe(Arrays.asList(nicknameToWatch));
                    this.subscribedTopics.add(nicknameToWatch);
                }

                System.out.format("\nEntrando na transmissão de %s...\n", nicknameToWatch);
                break;
            case "status":

                // Consumo do tópico do jogador
                // São feitas 5 tentativas
                for (int i = 0; i < 5; i++) {
                    System.out.format("\nDEBUG -> Tentativa %d\n", i);

                    ConsumerRecords<String, String> recordsMatch = this.consumerMatches.poll(Duration.ofMillis(1000));
                    for (ConsumerRecord<String, String> record : recordsMatch) {
                        String key = record.key();
                        String value = record.value();

                        System.out.format("\nDEBUG -> message: key=%s, value=%s\n", key, value);

                        System.out.format("* %s: %s\n", key, value);
                    }
                }
                break;
            case "sair":
                System.out.print("\nNome do jogador: ");
                String nicknameToQuit = scanner.nextLine(); 

                // Método unsubscribe() cancela a inscrição de todos tópicos do consumidor em questão
                this.consumerMatches.unsubscribe();
                this.subscribedTopics.remove(nicknameToQuit);

                System.out.format("\nSaindo da transmissão de %s...\n", nicknameToQuit);
                break;
            default:
                System.out.println("\nERRO: Este comando não existe.\n"); 
                break;
        }
    }

    @Override
    public void run() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("\nUse o comando 'listar' para ver os jogadores em transmissão.\n");

        while (true) {
            String command = scanner.nextLine();

            handleViewerCommands(command, scanner);
        }
    }
}

