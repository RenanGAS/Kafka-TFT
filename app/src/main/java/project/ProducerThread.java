package project;

import org.apache.kafka.clients.producer.*;

import java.io.*;
import java.math.BigDecimal;
import java.time.Duration;
import java.nio.file.*;
import java.util.*;

import org.json.JSONObject;
import java.util.concurrent.ExecutionException;
import java.lang.InterruptedException;

public class ProducerThread extends Thread {
    String nickname;
    Path logFilePath;
    String configFile;

    public ProducerThread(String nickname, Path logFilePath, String configFile) {
        this.nickname = nickname;
        this.logFilePath = logFilePath;
        this.configFile = configFile;
    }

    @Override
    public void run() {
        // O nome do tópico de um jogador corresponde ao seu nickname
        final String topic = this.nickname;

        Properties props = null;

        try {
            // Carrega configurações do produtor
            props = AdminTasks.loadConfig(this.configFile);

            // Cria tópico
            AdminTasks.createTopic(topic);
        } catch (IOException ioe) {
            System.out.println("\nERRO: " + ioe.getMessage() + "\n");
        } catch (ExecutionException | InterruptedException ex) {
            System.out.println("\nERRO: " + ex.getMessage() + "\n");
        }

        System.out.println("\nTransmissão em andamento...\n");

        try (final Producer<String, String> producer = new KafkaProducer<>(props)) {
            // Mensagem para o tópico "listPlayers", indicando que o jogador está em transmissão
            producer.send(new ProducerRecord<>("listPlayers", topic, "online"));

            try (BufferedReader br = new BufferedReader(new FileReader(new File(this.logFilePath.toString())))) {
                String line;
                while ((line = br.readLine()) != null) {
                    JSONObject jsonObj = new JSONObject(line);
                    Iterator<String> it = jsonObj.keySet().iterator();

                    switch (it.next()) {
                        case "me":
                            // Para pegar health e gold
                            JSONObject me = jsonObj.getJSONObject("me");
                            Iterator<String> it2 = me.keySet().iterator();

                            switch (it2.next()) {
                                case "health":
                                    // Send sem esperar ack do servidor
                                    producer.send(new ProducerRecord<>(topic, "health", me.getString("health")));
                                    break;
                                case "gold":
                                    producer.send(new ProducerRecord<>(topic, "gold", me.getString("gold")));
                                    break;
                                default:
                                    break;
                            }

                            break;
                        case "match_info":
                            // Para pegar o estágio 
                            JSONObject stageJson = new JSONObject(jsonObj.getJSONObject("match_info").getString("round_type"));
                            producer.send(new ProducerRecord<>(topic, "stage", stageJson.getString("stage")));
                            break;
                        case "live_client_data":
                            // Para pegar o tempo de partida 
                            JSONObject timeJson = new JSONObject(jsonObj.getJSONObject("live_client_data").getString("game_data"));
                            BigDecimal seconds = timeJson.getBigDecimal("gameTime");

                            Duration duration = Duration.ofNanos(seconds.multiply(BigDecimal.valueOf(1_000_000_000)).longValue());
                            String formattedDuration = String.format("%d:%d", duration.toMinutes() % 60, duration.toSeconds() % 60);
                            System.out.format("\nkey: game_time, value: %s\n", formattedDuration);
                            producer.send(new ProducerRecord<>(topic, "game_time", formattedDuration));
                            break;
                        case "board":
                            // Para pegar as peças do tabuleiro
                            JSONObject piecesJson = new JSONObject(jsonObj.getJSONObject("board").getString("board_pieces"));

                            String listChamps = "";
                            for (String piece : piecesJson.keySet()) {
                                listChamps += "* " + piecesJson.getJSONObject(piece).getString("name");                        
                                listChamps += " (" + piecesJson.getJSONObject(piece).getString("level") + ")\n";                        
                            }

                            producer.send(new ProducerRecord<>(topic, "board", listChamps));
                            break;
                        default:
                            System.out.println("\nERRO: Evento não esperado.\n");
                            break;
                    }

                    // Envia um evento a cada 5 segundos
                    Thread.sleep(5000);
                }
            } catch (InterruptedException | IOException ioe) {
                System.out.println("\nERRO: " + ioe.getMessage() + "\n");
            }

            producer.send(new ProducerRecord<>("listPlayers", topic, "offline"));
        }
    }
}

