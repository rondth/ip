import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Scanner;

/*
Starts the Henry chatbot application.
 */
public class Henry {
    /**
     * Greets the user, stores tasks, updates or deletes tasks, lists saved tasks, and exits
     * when the user enters bye.
     *
     * @param args command-line arguments; not used
     */
    @SuppressWarnings("unused")
    public static void main(String[] args) {
        String separator = "____________________________________________________________";
        String banner = """
                 _   _                     \s
                | | | | ___ _ __  _ __ _   _
                | |_| |/ _ \\ '_ \\| '__| | | |
                |  _  |  __/ | | | |  | |_| |
                |_| |_|\\___|_| |_|_|   \\__, |
                                       |___/\s
                """;

        System.out.println(separator);
        System.out.print(banner);
        System.out.println("Hello! I'm Henry.");
        System.out.println("What can I do for you?");
        System.out.println(separator);

        Scanner scanner = new Scanner(System.in);
        Storage storage = new Storage(Path.of("data", "henry.txt"));
        ArrayList<Task> tasks = loadTasks(storage, separator);

        while (scanner.hasNextLine()) {
            String command = scanner.nextLine().trim();
            CommandType commandType = CommandType.from(command);
            try {
                switch (commandType) {
                case BYE:
                    System.out.println("Bye. Hope to see you again soon!");
                    System.out.println(separator);
                    return;
                case LIST:
                    System.out.println(" Here are the tasks in your list:");
                    for (int i = 0; i < tasks.size(); i++) {
                        System.out.println(" " + (i + 1) + "." + tasks.get(i));
                    }
                    break;
                case MARK:
                    int taskIndex = parseTaskIndex(command, commandType, tasks.size());
                    updateTaskStatus(tasks, taskIndex, true, storage);
                    System.out.println(" Nice! I've marked this task as done:");
                    System.out.println("   " + tasks.get(taskIndex));
                    break;
                case UNMARK:
                    int unmarkedTaskIndex = parseTaskIndex(command, commandType, tasks.size());
                    updateTaskStatus(tasks, unmarkedTaskIndex, false, storage);
                    System.out.println(" OK, I've marked this task as not done yet:");
                    System.out.println("   " + tasks.get(unmarkedTaskIndex));
                    break;
                case DELETE:
                    int deletedTaskIndex = parseTaskIndex(command, commandType, tasks.size());
                    Task removedTask = tasks.remove(deletedTaskIndex);
                    try {
                        storage.save(tasks);
                    } catch (IOException e) {
                        tasks.add(deletedTaskIndex, removedTask);
                        throw e;
                    }
                    System.out.println(" Noted. I've removed this task:");
                    System.out.println("   " + removedTask);
                    System.out.println(" Now you have " + tasks.size() + " tasks in the list.");
                    break;
                case TODO:
                    String description = extractArguments(command, commandType);
                    if (description.isEmpty()) {
                        throw new HenryException(
                                "A todo needs a description. For example: todo borrow a book");
                    }
                    addTask(tasks, new Todo(description), storage);
                    break;
                case DEADLINE:
                    String taskDetails = extractArguments(command, commandType);
                    int bySeparatorIndex = taskDetails.indexOf("/by");
                    if (bySeparatorIndex < 0) {
                        throw new HenryException(
                                "A deadline needs '/by'. For example: deadline submit report /by Friday");
                    }
                    String deadlineDescription = taskDetails.substring(0, bySeparatorIndex).trim();
                    String by = taskDetails.substring(bySeparatorIndex + 3).trim();
                    if (deadlineDescription.isEmpty()) {
                        throw new HenryException("A deadline needs a description before '/by'.");
                    }
                    if (by.isEmpty()) {
                        throw new HenryException("A deadline needs a date or time after '/by'.");
                    }
                    addTask(tasks, new Deadline(deadlineDescription, by), storage);
                    break;
                case EVENT:
                    String eventDetails = extractArguments(command, commandType);
                    int fromSeparatorIndex = eventDetails.indexOf("/from");
                    if (fromSeparatorIndex < 0) {
                        throw new HenryException(
                                "An event needs '/from' and '/to'. "
                                        + "For example: event meeting /from 2pm /to 3pm");
                    }
                    int toSeparatorIndex = eventDetails.indexOf("/to", fromSeparatorIndex + 5);
                    if (toSeparatorIndex < 0) {
                        throw new HenryException("An event needs an ending time introduced by '/to'.");
                    }
                    String eventDescription = eventDetails.substring(0, fromSeparatorIndex).trim();
                    String from = eventDetails.substring(fromSeparatorIndex + 5, toSeparatorIndex).trim();
                    String to = eventDetails.substring(toSeparatorIndex + 3).trim();
                    if (eventDescription.isEmpty()) {
                        throw new HenryException("An event needs a description before '/from'.");
                    }
                    if (from.isEmpty()) {
                        throw new HenryException("An event needs a starting time after '/from'.");
                    }
                    if (to.isEmpty()) {
                        throw new HenryException("An event needs an ending time after '/to'.");
                    }
                    addTask(tasks, new Event(eventDescription, from, to), storage);
                    break;
                case UNKNOWN:
                    throw new HenryException(
                            "I don't recognise that command. Try todo, deadline, event, list, mark, unmark, delete, or bye.");
                }
            } catch (HenryException e) {
                System.out.println(e.getMessage());
            } catch (IOException e) {
                System.out.println("I couldn't save your tasks. Your last change was not applied.");
            }
            System.out.println(separator);
        }
    }

    /**
     * Extracts and validates the task number supplied to a command.
     *
     * @param input complete user input
     * @param commandType command whose task number should be read
     * @param taskCount current number of tasks
     * @return zero-based index of the selected task
     * @throws HenryException if the task number is absent, invalid, or out of range
     */
    private static int parseTaskIndex(String input, CommandType commandType, int taskCount)
            throws HenryException {
        String commandWord = commandType.getCommandWord();
        String argument = extractArguments(input, commandType);
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
     * Returns the text following a command word.
     *
     * @param input complete user input
     * @param commandType recognised command type
     * @return trimmed command arguments
     */
    private static String extractArguments(String input, CommandType commandType) {
        return input.substring(commandType.getCommandWord().length()).trim();
    }

    /**
     * Adds a task to the list and prints its confirmation.
     *
     * @param tasks task list to update
     * @param task task to add
     * @param storage storage used to save the updated list
     * @throws IOException if the updated task list cannot be saved
     */
    private static void addTask(ArrayList<Task> tasks, Task task, Storage storage)
            throws IOException {
        tasks.add(task);
        try {
            storage.save(tasks);
        } catch (IOException e) {
            tasks.removeLast();
            throw e;
        }
        System.out.println(" Got it. I've added this task:");
        System.out.println("   " + task);
        System.out.println(" Now you have " + tasks.size() + " tasks in the list.");
    }

    /**
     * Loads tasks without allowing a missing, unreadable, or partially malformed file to crash
     * the chatbot.
     */
    private static ArrayList<Task> loadTasks(Storage storage, String separator) {
        try {
            Storage.LoadResult result = storage.load();
            if (result.skippedLineCount() > 0) {
                int skippedLineCount = result.skippedLineCount();
                String recordLabel = skippedLineCount == 1 ? "record was" : "records were";
                System.out.println("Warning: " + skippedLineCount + " malformed task "
                        + recordLabel + " skipped while loading data/henry.txt.");
                System.out.println(separator);
            }
            return result.tasks();
        } catch (IOException e) {
            System.out.println("I couldn't load tasks from data/henry.txt. "
                    + "Starting with an empty task list.");
            System.out.println(separator);
            return new ArrayList<>();
        }
    }

    /**
     * Changes a task's status and restores it if the updated list cannot be saved.
     */
    private static void updateTaskStatus(ArrayList<Task> tasks, int taskIndex, boolean isDone,
            Storage storage) throws IOException {
        Task task = tasks.get(taskIndex);
        boolean wasDone = task.isDone;
        if (isDone) {
            task.markAsDone();
        } else {
            task.markAsNotDone();
        }

        try {
            storage.save(tasks);
        } catch (IOException e) {
            if (wasDone) {
                task.markAsDone();
            } else {
                task.markAsNotDone();
            }
            throw e;
        }
    }
}
