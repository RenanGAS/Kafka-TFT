/**
 *
 * Autor: Renan Guensuke Aoki Sakashita
 *
 */

package project;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * Inicializa o produtor do jogador, recebendo o nome do tópico e o arquivo onde os logs da partida estão armazenados,
 * instanciando também sua respectiva thread.
 *
 */
public class Player {

    public static void main(final String[] args) throws IOException, InterruptedException {
        if (args.length != 1) {
            System.out.println("ERRO: Arquivo de configuração deve ser passado como argumento. Verifique a instrução no makefile.\n");
            System.exit(1);
        }

        Scanner scanner = new Scanner(System.in);

        System.out.print("Nome do jogador: ");
        String nickname = scanner.nextLine();

        Path logFilePath = null;

        while (true) {
            System.out.print("Arquivo de log: ");
            String logFileName = scanner.nextLine();

            try {
                logFilePath = Paths.get("Logs/" + logFileName);
                System.out.println(logFilePath.toString());
            } catch (InvalidPathException ipe) {
                System.out.println("\nERRO: " + ipe.getMessage() + "\n");
                continue;
            }

            break;
        }

        ProducerThread producer = new ProducerThread(nickname, logFilePath, args[0]);
        producer.start();
        producer.join();
    }
}

