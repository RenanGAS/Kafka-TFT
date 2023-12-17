package project;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class Player {

    public static void main(final String[] args) throws IOException, InterruptedException {
        if (args.length != 1) {
            System.out.println("ERRO: Arquivo de configuração deve ser passado como argumento. Verifique a instrução no makefile.\n");
            System.exit(1);
        }

        Scanner scanner = new Scanner(System.in);

        System.out.print("Nome do jogador: ");
        String nickname = scanner.nextLine();

        String logFileName;

        while (true) {
            System.out.print("Arquivo de log: ");
            logFileName = scanner.nextLine();

            try {
                Path logFilePath = Paths.get("Logs/" + logFileName);
                System.out.println(logFilePath.toString());
            } catch (InvalidPathException ipe) {
                System.out.println("\nERRO: " + ipe.getMessage() + "\n");
                continue;
            }

            break;
        }

        ProducerThread producer = new ProducerThread(nickname, logFileName);
        producer.start();
        producer.join();
    }
}

