/**
 *
 * Autor: Renan Guensuke Aoki Sakashita
 *
 */

package project;

/**
 * Instancia thread para consumo de mensagens dos tópicos.
 *
 */
public class Viewer {
    public static void main(final String[] args) throws Exception {
        if (args.length != 1) {
            System.out.println("Please provide the configuration file path as a command line argument");
            System.exit(1);
        }

        ConsumerThread consumer = new ConsumerThread(args[0]);
        consumer.start();
        consumer.join();
    }
}

