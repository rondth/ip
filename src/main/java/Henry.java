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
            String command = scanner.nextLine().trim();
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
                    int taskIndex = parseTaskIndex(command, "mark", taskCount);
                    tasks[taskIndex].markAsDone();
                    System.out.println(" Nice! I've marked this task as done:");
                    System.out.println("   " + tasks[taskIndex]);
                } else if (isCommand(command, "unmark")) {
                    int taskIndex = parseTaskIndex(command, "unmark", taskCount);
                    tasks[taskIndex].markAsNotDone();
                    System.out.println(" OK, I've marked this task as not done yet:");
                    System.out.println("   " + tasks[taskIndex]);
                } else if (isCommand(command, "todo")) {
                    String description = command.substring("todo".length()).trim();
                    if (description.isEmpty()) {
                        throw new HenryException(
                                "A todo needs a description. For example: todo borrow a book");
                    }
                    ensureTaskListHasSpace(taskCount);
                    tasks[taskCount] = new Todo(description);
                    taskCount = addTask(tasks, taskCount);
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
                    ensureTaskListHasSpace(taskCount);
                    tasks[taskCount] = new Deadline(description, by);
                    taskCount = addTask(tasks, taskCount);
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
                    ensureTaskListHasSpace(taskCount);
                    tasks[taskCount] = new Event(description, from, to);
                    taskCount = addTask(tasks, taskCount);
                } else {
                    throw new HenryException(
                            "I don't recognise that command. Try todo, deadline, event, list, mark, unmark, or bye.");
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
     * Ensures that another task can be stored in the fixed-size task list.
     *
     * @param taskCount current number of tasks
     * @throws HenryException if the task list is full
     */
    private static void ensureTaskListHasSpace(int taskCount) throws HenryException {
        if (taskCount >= MAX_TASKS) {
            throw new HenryException("The task list is full, so I can't add another task.");
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
