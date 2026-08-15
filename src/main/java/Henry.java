import java.util.Scanner;

/**
 * Starts the Henry chatbot application.
 */
public class Henry {
    /**
     * Greets the user, echoes each command, and exits when the user enters bye.
     *
     * @param args command-line arguments; not used
     */
    public static void main(String[] args) {
        String separator = "____________________________________________________________";
        String banner = " _   _                      \n"
                + "| | | | ___ _ __  _ __ _   _\n"
                + "| |_| |/ _ \\ '_ \\| '__| | | |\n"
                + "|  _  |  __/ | | | |  | |_| |\n"
                + "|_| |_|\\___|_| |_|_|   \\__, |\n"
                + "                       |___/ \n";

        System.out.println(separator);
        System.out.print(banner);
        System.out.println("Hello! I'm Henry.");
        System.out.println("What can I do for you?");
        System.out.println(separator);

        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String command = scanner.nextLine();
            if (command.equals("bye")) {
                System.out.println("Bye. Hope to see you again soon!");
                System.out.println(separator);
                break;
            }

            System.out.println(command);
            System.out.println(separator);
        }
    }
}
