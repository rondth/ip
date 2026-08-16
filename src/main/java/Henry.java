import java.util.Scanner;

/**
 * Starts the Henry chatbot application.
 */
public class Henry {
    private static final int MAX_TASKS = 100;

    /**
     * Greets the user, stores tasks, updates task completion, lists saved tasks, and exits
     * when the user enters bye.
     *
     * @param args command-line arguments; not used
     */
    public static void main(String[] args) throws HenryException {
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
        Task[] tasks = new Task[MAX_TASKS];
        int taskCount = 0;

        while (scanner.hasNextLine()) {
            String command = scanner.nextLine();
            try {
                if (command.equals("bye")) {
                    System.out.println("Bye. Hope to see you again soon!");
                    System.out.println(separator);
                    break;
                }

                if (command.equals("list")) {
                    System.out.println(" Here are the tasks in your list:");
                    for (int i = 0; i < taskCount; i++) {
                        System.out.println(" " + (i + 1) + "." + tasks[i]);
                    }
                } else if (isCommand(command, "mark")) {
                    requireArguments(command, "mark");
                    int taskNumber = Integer.parseInt(command.substring(5).trim());
                    int taskIndex = taskNumber - 1;
                    tasks[taskIndex].markAsDone();
                    System.out.println(" Nice! I've marked this task as done:");
                    System.out.println("   " + tasks[taskIndex]);
                } else if (isCommand(command, "unmark")) {
                    requireArguments(command, "unmark");
                    int taskNumber = Integer.parseInt(command.substring(7).trim());
                    int taskIndex = taskNumber - 1;
                    tasks[taskIndex].markAsNotDone();
                    System.out.println(" OK, I've marked this task as not done yet:");
                    System.out.println("   " + tasks[taskIndex]);
                } else if (isCommand(command, "todo")) {
                    requireArguments(command, "todo");
                    String description = command.substring(5);
                    tasks[taskCount] = new Todo(description);
                    taskCount = addTask(tasks, taskCount);
                } else if (isCommand(command, "deadline")) {
                    requireArguments(command, "deadline");
                    String taskDetails = command.substring(9);
                    int bySeparatorIndex = taskDetails.indexOf(" /by ");
                    String description = taskDetails.substring(0, bySeparatorIndex);
                    String by = taskDetails.substring(bySeparatorIndex + 5);
                    tasks[taskCount] = new Deadline(description, by);
                    taskCount = addTask(tasks, taskCount);
                } else if (isCommand(command, "event")) {
                    requireArguments(command, "event");
                    String taskDetails = command.substring(6);
                    int fromSeparatorIndex = taskDetails.indexOf(" /from ");
                    int toSeparatorIndex = taskDetails.indexOf(" /to ", fromSeparatorIndex + 7);
                    String description = taskDetails.substring(0, fromSeparatorIndex);
                    String from = taskDetails.substring(fromSeparatorIndex + 7, toSeparatorIndex);
                    String to = taskDetails.substring(toSeparatorIndex + 5);
                    tasks[taskCount] = new Event(description, from, to);
                    taskCount = addTask(tasks, taskCount);
                } else {
                    throw new HenryException(
                            "I don't recognise that command. Try todo, deadline, event, list, mark, unmark, or bye.");
                }
            } catch (HenryException e){
                System.out.println(e.getMessage());
            }
            System.out.println(separator);
        }
    }

    /**
     * Returns whether the input is the given command, with or without arguments.
     *
     * @param input complete user input
     * @param commandWord command word to match
     * @return true if the input contains the command word
     */
    private static boolean isCommand(String input, String commandWord) {
        return input.equals(commandWord) || input.startsWith(commandWord + " ");
    }

    /**
     * Ensures that a command which requires arguments is not empty.
     *
     * @param input complete user input
     * @param commandWord command word whose arguments are required
     * @throws HenryException if the command has no arguments
     */
    private static void requireArguments(String input, String commandWord) throws HenryException {
        if (input.equals(commandWord)) {
            throw new HenryException("The " + commandWord + " command needs more information.");
        }
    }

    /**
     * Prints confirmation for the task at the current insertion position.
     *
     * @param tasks task storage containing the newly added task
     * @param taskCount index of the newly added task
     * @return the updated number of tasks
     */
    private static int addTask(Task[] tasks, int taskCount) {
        int updatedTaskCount = taskCount + 1;
        System.out.println(" Got it. I've added this task:");
        System.out.println("   " + tasks[taskCount]);
        System.out.println(" Now you have " + updatedTaskCount + " tasks in the list.");
        return updatedTaskCount;
    }
}
