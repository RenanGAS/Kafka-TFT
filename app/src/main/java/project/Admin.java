package project;

import org.apache.kafka.clients.producer.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * Gerencia a aplicação Kafka
 *
 */
public class Admin {

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
}

