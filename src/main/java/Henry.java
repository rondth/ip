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
        Task[] tasks = new Task[MAX_TASKS];
        int taskCount = 0;

        while (scanner.hasNextLine()) {
            String command = scanner.nextLine();
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
            } else if (command.startsWith("mark ")) {
                int taskNumber = Integer.parseInt(command.substring(5).trim());
                int taskIndex = taskNumber - 1;
                tasks[taskIndex].markAsDone();
                System.out.println(" Nice! I've marked this task as done:");
                System.out.println("   " + tasks[taskIndex]);
            } else if (command.startsWith("unmark ")) {
                int taskNumber = Integer.parseInt(command.substring(7).trim());
                int taskIndex = taskNumber - 1;
                tasks[taskIndex].markAsNotDone();
                System.out.println(" OK, I've marked this task as not done yet:");
                System.out.println("   " + tasks[taskIndex]);
            } else if (command.startsWith("todo ")) {
                String description = command.substring(5);
                tasks[taskCount] = new Todo(description);
                taskCount = addTask(tasks, taskCount);
            } else if (command.startsWith("deadline ")) {
                String taskDetails = command.substring(9);
                int bySeparatorIndex = taskDetails.indexOf(" /by ");
                String description = taskDetails.substring(0, bySeparatorIndex);
                String by = taskDetails.substring(bySeparatorIndex + 5);
                tasks[taskCount] = new Deadline(description, by);
                taskCount = addTask(tasks, taskCount);
            } else {
                tasks[taskCount] = new Todo(command);
                taskCount = addTask(tasks, taskCount);
            }
            System.out.println(separator);
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
