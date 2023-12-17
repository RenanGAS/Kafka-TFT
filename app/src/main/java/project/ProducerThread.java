package project;

import org.apache.kafka.clients.producer.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class ProducerThread extends Thread {
    String nickname;
    String logFileName;

    public ProducerThread(String nickname, String logFileName) {
        this.nickname = nickname;
        this.logFileName = logFileName;
    }

    @Override
    public void run() {
        Properties props = null;

        try {
            // Carrega configurações do produtor
            props = Admin.loadConfig(this.logFileName);
        } catch (IOException ioe) {
            System.out.println("\nERRO: " + ioe.getMessage() + "\n");
        }

        // O nome do tópico de um jogador corresponde ao seu nickname
        final String topic = this.nickname;

        System.out.println("\nTransmissão em andamento...\n");

        try (final Producer<String, String> producer = new KafkaProducer<>(props)) {
            // Percorrer arquivo de logs
            // Ver sobre connect pra deixar produtor escutando novos logs no arquivo
            // e detectar cancelamento, fechando o produtor
            producer.send(
                    new ProducerRecord<>(topic, "key", "value"),
                    (event, exception) -> {
                        if (exception != null) {
                            exception.printStackTrace();
                        }
                    });
        }
    }
}

