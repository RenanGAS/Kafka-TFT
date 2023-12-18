package project;

import org.apache.kafka.clients.consumer.*;
import java.time.Duration;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class ConsumerThread extends Thread {
    final Consumer<String, String> consumerListPlayers;
    final Consumer<String, String> consumerMatches;
    List<String> subscribedTopics;
    List<String> onlinePlayers;

    public ConsumerThread(String configFile) {
        // Configuração do consumidor
        Properties props = null;
        try {
            // Carrega configurações do produtor
            props = AdminTasks.loadConfig(configFile);
        } catch (IOException ioe) {
            System.out.println("\nERRO: " + ioe.getMessage() + "\n");
        }

        // Configurações adicionais
        // Grupo do consumidor
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "kafka-java-getting-started");
        // Em caso de falha, volta a pegar o evento mais novo disponível
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        this.consumerListPlayers = new KafkaConsumer<>(props);
        this.consumerListPlayers.subscribe(Arrays.asList("listPlayers"));

        this.subscribedTopics = new ArrayList<>();
        this.subscribedTopics.add("listPlayers");

        this.consumerMatches = new KafkaConsumer<>(props);
    }

    void handleViewerCommands(String command, Scanner scanner) {
        switch (command) {
            case "listar":
                System.out.println("\nJogadores em transmissão:\n");
                // Mostrar apenas jogadores "online"
                ConsumerRecords<String, String> recordsListPlayers = this.consumerListPlayers.poll(Duration.ofMillis(100));
                for (ConsumerRecord<String, String> record : recordsListPlayers) {
                    String key = record.key();
                    String value = record.value();
                    System.out.format("\ndebug -> key: %s, value: %s\n", key, value);

                    if (value == "online") {
                        this.onlinePlayers.add(key);
                    } else if (value == "offline") {
                        this.onlinePlayers.remove(key);
                    }

                    for (String player : this.onlinePlayers) {
                        System.out.format("* %s\n", player);
                    }
                }
                break;
            case "assistir":
                System.out.print("\nNome do jogador: ");
                String nicknameToWatch = scanner.nextLine(); 

                this.consumerMatches.subscribe(Arrays.asList(nicknameToWatch));
                this.subscribedTopics.add(nicknameToWatch);

                System.out.format("\nEntrando na transmissão de %s...\n", nicknameToWatch);
                break;
            case "status":
                //System.out.print("\nNome do jogador: ");
                //String nicknameViewStatus = scanner.nextLine(); 

                // Filtrar eventos por nome do jogador
                ConsumerRecords<String, String> recordsMatch = this.consumerMatches.poll(Duration.ofMillis(100));
                for (ConsumerRecord<String, String> record : recordsMatch) {
                    String key = record.key();
                    String value = record.value();
                    System.out.format("* %s: %s\n", key, value);
                }
                break;
            case "sair":
                System.out.print("\nNome do jogador: ");
                String nicknameToQuit = scanner.nextLine(); 

                this.consumerMatches.unsubscribe();
                this.subscribedTopics.remove(nicknameToQuit);
                this.consumerMatches.subscribe(this.subscribedTopics);

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

