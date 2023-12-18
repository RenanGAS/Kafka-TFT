package project;

import org.apache.kafka.clients.admin.*;
import org.apache.kafka.common.KafkaFuture;
import org.apache.kafka.common.config.TopicConfig;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.ExecutionException;

/**
 * Gerencia a aplicação Kafka
 *
 */
public class AdminTasks {

    /**
     * Carrega configuração dos produtores, consumidores e brokers.
     * 
     * @param configFile Nome do arquivo de configuração
     * @return Objeto Properties
     */
    public static Properties loadConfig(final String configFile) throws IOException {
        if (!Files.exists(Paths.get(configFile))) {
            throw new IOException("\nERRO: Arquivo " + configFile + " não encontrado.\n");
        }

        final Properties config = new Properties();

        // Leitura do arquivo de configuração e carregamento no objeto Properties
        try (InputStream inputStream = new FileInputStream(configFile)) {
            config.load(inputStream);
        }

        return config;
    }

    /**
     * Cria um tópico com a configuração 'cleanup.policy' compact
     *
     * @param name Nome do tópico
     * @return Tópico com número de partições = 3 e fator de replicação = 3
     */
    public static void createTopic(String name) throws ExecutionException, InterruptedException {
        Properties props = new Properties();
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");

        try (Admin admin = Admin.create(props)) {
            String topicName = name;
            int partitions = 3;
            short replicationFactor = 3;

            // Cria um tópico compacto
            CreateTopicsResult result = admin.createTopics(Collections.singleton(
                        new NewTopic(topicName, partitions, replicationFactor)
                        .configs(Collections.singletonMap(TopicConfig.CLEANUP_POLICY_CONFIG, TopicConfig.CLEANUP_POLICY_COMPACT))));

            // Bloqueia até que haja uma resposta da criação do tópico
            KafkaFuture<Void> future = result.values().get(topicName);

            future.get();
        }
    }

    public static void main(String[] args) {
        try {
            createTopic("listPlayers");
        } catch (ExecutionException | InterruptedException e) {
            System.out.println("\nERRO: " + e.getMessage() + "\n");
        }
    }
}

