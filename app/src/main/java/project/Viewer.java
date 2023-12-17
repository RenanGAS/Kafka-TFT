package project;

import org.apache.kafka.clients.consumer.*;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

public class Viewer {

    public static void main(final String[] args) throws Exception {
        if (args.length != 1) {
            System.out.println("Please provide the configuration file path as a command line argument");
            System.exit(1);
        }

        // Configuração do consumidor
        final Properties props = Admin.loadConfig(args[0]);

        // Configurações adicionais
        // Grupo do consumidor
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "kafka-java-getting-started");
        // Em caso de falha, volta a pegar o evento mais novo disponível
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        // Ver como consumir e receber comandos do terminal
        try (final Consumer<String, String> consumerPlayersInLive = new KafkaConsumer<>(props)) {
            consumerPlayersInLive.subscribe(Arrays.asList("listInLive"));
            while (true) {
                ConsumerRecords<String, String> records = consumerPlayersInLive.poll(Duration.ofMillis(100));
                for (ConsumerRecord<String, String> record : records) {
                    String key = record.key();
                    String value = record.value();
                    System.out.println(
                            String.format("Consumed event from topic %s: key = %-10s value = %s", topic, key, value));
                }
            }
        }
    }

}

