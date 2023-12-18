/**
 *
 * Autor: Renan Guensuke Aoki Sakashita
 *
 */

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

/**
 * Faz o envio dos eventos da partida de um jogador para seu tópico, como também notifica o início/término da partida
 *
 */
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


        // Configuração do produtor
        Properties props = null;
        try {
            // Carrega configurações em "config.properties"
            props = AdminTasks.loadConfig(this.configFile);

            // Cria tópico do jogador
            AdminTasks.createTopic(topic);

        } catch (IOException ioe) {
            System.out.println("\nERRO: " + ioe.getMessage() + "\n");
        } catch (ExecutionException | InterruptedException ex) {
            System.out.println("\nERRO: " + ex.getMessage() + "\n");
        }

        System.out.println("\nTransmissão em andamento...\n");

        // Criação do produtor para transmissão dos eventos
        try (final Producer<String, String> producer = new KafkaProducer<>(props)) {

            // Mensagem para o tópico "Jogadores Online", indicando que o jogador está em transmissão
            producer.send(new ProducerRecord<>("listPlayers", topic, "online"));

            // Leitor para o arquivo "logs_example.txt", que contém exemplos de logs reais de uma partida de TFT
            try (BufferedReader br = new BufferedReader(new FileReader(new File(this.logFilePath.toString())))) {

                String line;
                while ((line = br.readLine()) != null) {

                    // Parse de um evento para JSON
                    JSONObject jsonObj = new JSONObject(line);

                    // Identificação de sua primeira chave
                    Iterator<String> itFirstKey = jsonObj.keySet().iterator();
                    switch (itFirstKey.next()) {
                        case "me":

                            // "me" é a chave utilizada para eventos de vida (health) e dinheiro (gold)
                            JSONObject me = jsonObj.getJSONObject("me");

                            // Identificação da segunda chave: "health" ou "gold"
                            Iterator<String> itSecondKey = me.keySet().iterator();
                            switch (itSecondKey.next()) {
                                case "health":

                                    // No código é utilizado envio de mensagens sem esperar ACKs do Cluster
                                    System.out.format("\nDEBUG -> message: key=health, value=%s\n", me.getString("health"));
                                    producer.send(new ProducerRecord<>(topic, "health", me.getString("health")));

                                    break;
                                case "gold":

                                    System.out.format("\nDEBUG -> message: key=gold, value=%s\n", me.getString("gold"));
                                    producer.send(new ProducerRecord<>(topic, "gold", me.getString("gold")));
                                    break;
                                default:
                                    // Se receber outro evento, faz nada
                                    break;
                            }

                            break;
                        case "match_info":

                            // "match_info" é a chave utilizada para eventos de informação do estágio corrente da partida 
                            JSONObject stageJson = new JSONObject(jsonObj.getJSONObject("match_info").getString("round_type"));

                            System.out.format("\nDEBUG -> message: key=stage, value=%s\n", stageJson.getString("stage"));
                            producer.send(new ProducerRecord<>(topic, "stage", stageJson.getString("stage")));
                            break;
                        case "live_client_data":

                            // "live_client_data" é a chave utilizada para eventos de informação do tempo da partida 
                            JSONObject timeJson = new JSONObject(jsonObj.getJSONObject("live_client_data").getString("game_data"));

                            // Processo de conversão do valor fornecido em BigDecimal para minutos e segundos
                            BigDecimal seconds = timeJson.getBigDecimal("gameTime");
                            Duration duration = Duration.ofNanos(seconds.multiply(BigDecimal.valueOf(1_000_000_000)).longValue());
                            String formattedDuration = String.format("%d:%d", duration.toMinutes() % 60, duration.toSeconds() % 60);

                            System.out.format("\nDEBUG -> message: key=game_time, value=%s\n", formattedDuration);
                            producer.send(new ProducerRecord<>(topic, "game_time", formattedDuration));
                            break;
                        case "board":

                            // "board" é a chave utilizada para eventos de informação das peças no tabuleiro do jogador 
                            JSONObject piecesJson = new JSONObject(jsonObj.getJSONObject("board").getString("board_pieces"));

                            // De cada peça, extrai-se seu nome e level
                            String listChamps = "";
                            for (String piece : piecesJson.keySet()) {
                                listChamps += "* " + piecesJson.getJSONObject(piece).getString("name");                        
                                listChamps += " (" + piecesJson.getJSONObject(piece).getString("level") + ")\n";                        
                            }

                            System.out.format("\nDEBUG -> message: key=board, value=%s\n", listChamps);
                            producer.send(new ProducerRecord<>(topic, "board", listChamps));
                            break;
                        default:
                            System.out.println("\nERRO: Evento não esperado.\n");
                            break;
                    }

                    // Envia-se um evento a cada 5 segundos
                    Thread.sleep(5000);
                }
            } catch (InterruptedException | IOException ioe) {
                System.out.println("\nERRO: " + ioe.getMessage() + "\n");
            }

            // Mensagem para o tópico "Jogadores Online", indicando que o jogador terminou sua transmissão
            producer.send(new ProducerRecord<>("listPlayers", topic, "offline"));
        }
    }
}

