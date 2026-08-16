import java.util.ArrayList;
import java.util.Scanner;

/**
 * Starts the Henry chatbot application.
 */
public class Henry {
    /**
     * Greets the user, stores tasks, updates or deletes tasks, lists saved tasks, and exits
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
        ArrayList<Task> tasks = new ArrayList<>();

        while (scanner.hasNextLine()) {
            String command = scanner.nextLine().trim();
            try {
                if (command.equals("bye")) {
                    System.out.println("Bye. Hope to see you again soon!");
                    System.out.println(separator);
                    break;
                }

                if (command.equals("list")) {
                    System.out.println(" Here are the tasks in your list:");
                    for (int i = 0; i < tasks.size(); i++) {
                        System.out.println(" " + (i + 1) + "." + tasks.get(i));
                    }
                } else if (isCommand(command, "mark")) {
                    int taskIndex = parseTaskIndex(command, "mark", tasks.size());
                    tasks.get(taskIndex).markAsDone();
                    System.out.println(" Nice! I've marked this task as done:");
                    System.out.println("   " + tasks.get(taskIndex));
                } else if (isCommand(command, "unmark")) {
                    int taskIndex = parseTaskIndex(command, "unmark", tasks.size());
                    tasks.get(taskIndex).markAsNotDone();
                    System.out.println(" OK, I've marked this task as not done yet:");
                    System.out.println("   " + tasks.get(taskIndex));
                } else if (isCommand(command, "delete")) {
                    int taskIndex = parseTaskIndex(command, "delete", tasks.size());
                    Task removedTask = tasks.remove(taskIndex);
                    System.out.println(" Noted. I've removed this task:");
                    System.out.println("   " + removedTask);
                    System.out.println(" Now you have " + tasks.size() + " tasks in the list.");
                } else if (isCommand(command, "todo")) {
                    String description = command.substring("todo".length()).trim();
                    if (description.isEmpty()) {
                        throw new HenryException(
                                "A todo needs a description. For example: todo borrow a book");
                    }
                    addTask(tasks, new Todo(description));
                } else if (isCommand(command, "deadline")) {
                    String taskDetails = command.substring("deadline".length()).trim();
                    int bySeparatorIndex = taskDetails.indexOf("/by");
                    if (bySeparatorIndex < 0) {
                        throw new HenryException(
                                "A deadline needs '/by'. For example: deadline submit report /by Friday");
                    }
                    String description = taskDetails.substring(0, bySeparatorIndex).trim();
                    String by = taskDetails.substring(bySeparatorIndex + 3).trim();
                    if (description.isEmpty()) {
                        throw new HenryException("A deadline needs a description before '/by'.");
                    }
                    if (by.isEmpty()) {
                        throw new HenryException("A deadline needs a date or time after '/by'.");
                    }
                    addTask(tasks, new Deadline(description, by));
                } else if (isCommand(command, "event")) {
                    String taskDetails = command.substring("event".length()).trim();
                    int fromSeparatorIndex = taskDetails.indexOf("/from");
                    if (fromSeparatorIndex < 0) {
                        throw new HenryException(
                                "An event needs '/from' and '/to'. "
                                        + "For example: event meeting /from 2pm /to 3pm");
                    }
                    int toSeparatorIndex = taskDetails.indexOf("/to", fromSeparatorIndex + 5);
                    if (toSeparatorIndex < 0) {
                        throw new HenryException("An event needs an ending time introduced by '/to'.");
                    }
                    String description = taskDetails.substring(0, fromSeparatorIndex).trim();
                    String from = taskDetails.substring(fromSeparatorIndex + 5, toSeparatorIndex).trim();
                    String to = taskDetails.substring(toSeparatorIndex + 3).trim();
                    if (description.isEmpty()) {
                        throw new HenryException("An event needs a description before '/from'.");
                    }
                    if (from.isEmpty()) {
                        throw new HenryException("An event needs a starting time after '/from'.");
                    }
                    if (to.isEmpty()) {
                        throw new HenryException("An event needs an ending time after '/to'.");
                    }
                    addTask(tasks, new Event(description, from, to));
                } else {
                    throw new HenryException(
                            "I don't recognise that command. Try todo, deadline, event, list, mark, unmark, delete, or bye.");
                }
            } catch (HenryException e) {
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
     * Extracts and validates the task number supplied to a command.
     *
     * @param input complete user input
     * @param commandWord command whose task number should be read
     * @param taskCount current number of tasks
     * @return zero-based index of the selected task
     * @throws HenryException if the task number is absent, invalid, or out of range
     */
    private static int parseTaskIndex(String input, String commandWord, int taskCount)
            throws HenryException {
        String argument = input.substring(commandWord.length()).trim();
        if (argument.isEmpty()) {
            throw new HenryException(
                    "Please specify a task number. For example: " + commandWord + " 1");
        }

        final int taskNumber;
        try {
            taskNumber = Integer.parseInt(argument);
        } catch (NumberFormatException e) {
            throw new HenryException("'" + argument + "' is not a valid task number.");
        }

        if (taskCount == 0) {
            throw new HenryException("There are no tasks to " + commandWord + " yet.");
        }
        if (taskNumber < 1 || taskNumber > taskCount) {
            throw new HenryException(
                    "Task " + taskNumber + " does not exist. Choose a number from 1 to "
                            + taskCount + ".");
        }
        return taskNumber - 1;
    }

    /**
     * Adds a task to the list and prints its confirmation.
     *
     * @param tasks task list to update
     * @param task task to add
     */
    private static void addTask(ArrayList<Task> tasks, Task task) {
        tasks.add(task);
        System.out.println(" Got it. I've added this task:");
        System.out.println("   " + task);
        System.out.println(" Now you have " + tasks.size() + " tasks in the list.");
    }
}
