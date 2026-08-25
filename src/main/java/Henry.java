import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

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
        Ui ui = new Ui();
        ui.showWelcome();
        Storage storage = new Storage(Path.of("data", "henry.txt"));
        ArrayList<Task> tasks = loadTasks(storage, ui);

        while (ui.hasNextCommand()) {
            String command = ui.readCommand();
            CommandType commandType = CommandType.from(command);
            try {
                switch (commandType) {
                case BYE:
                    ui.showGoodbye();
                    return;
                case LIST:
                    ui.showTaskList(tasks);
                    break;
                case MARK:
                    int taskIndex = parseTaskIndex(command, commandType, tasks.size());
                    updateTaskStatus(tasks, taskIndex, true, storage);
                    ui.showTaskMarked(tasks.get(taskIndex));
                    break;
                case UNMARK:
                    int unmarkedTaskIndex = parseTaskIndex(command, commandType, tasks.size());
                    updateTaskStatus(tasks, unmarkedTaskIndex, false, storage);
                    ui.showTaskUnmarked(tasks.get(unmarkedTaskIndex));
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
                    ui.showTaskDeleted(removedTask, tasks.size());
                    break;
                case TODO:
                    String description = extractArguments(command, commandType);
                    if (description.isEmpty()) {
                        throw new HenryException(
                                "A todo needs a description. For example: todo borrow a book");
                    }
                    addTask(tasks, new Todo(description), storage, ui);
                    break;
                case DEADLINE:
                    String taskDetails = extractArguments(command, commandType);
                    int bySeparatorIndex = taskDetails.indexOf("/by");
                    if (bySeparatorIndex < 0) {
                        throw new HenryException(
                                "A deadline needs '/by'. For example: deadline submit report /by 2019-12-02");
                    }
                    String deadlineDescription = taskDetails.substring(0, bySeparatorIndex).trim();
                    String by = taskDetails.substring(bySeparatorIndex + 3).trim();
                    if (deadlineDescription.isEmpty()) {
                        throw new HenryException("A deadline needs a description before '/by'.");
                    }
                    if (by.isEmpty()) {
                        throw new HenryException("A deadline needs a date or time after '/by'.");
                    }
                    LocalDateTime deadlineDateTime;
                    try {
                        deadlineDateTime = Deadline.parseBy(by);
                    } catch (DateTimeParseException e) {
                        throw new HenryException(
                                "Please use a deadline date like 2/12/2019 1800 or 2019-12-02.");
                    }
                    addTask(tasks, new Deadline(deadlineDescription, deadlineDateTime), storage, ui);
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
                    addTask(tasks, new Event(eventDescription, from, to), storage, ui);
                    break;
                case UNKNOWN:
                    throw new HenryException(
                            "I don't recognise that command. Try todo, deadline, event, list, mark, unmark, delete, or bye.");
                }
            } catch (HenryException e) {
                ui.showMessage(e.getMessage());
                continue;
            } catch (IOException e) {
                ui.showMessage("I couldn't save your tasks. Your last change was not applied.");
                continue;
            }
            ui.showSeparator();
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
     * @param ui console UI used to display the confirmation
     * @throws IOException if the updated task list cannot be saved
     */
    private static void addTask(ArrayList<Task> tasks, Task task, Storage storage, Ui ui)
            throws IOException {
        tasks.add(task);
        try {
            storage.save(tasks);
        } catch (IOException e) {
            tasks.removeLast();
            throw e;
        }
        ui.showTaskAdded(task, tasks.size());
    }

    /**
     * Loads tasks without allowing a missing, unreadable, or partially malformed file to crash
     * the chatbot.
     *
     * @param storage storage used to load tasks
     * @param ui console UI used to display loading warnings
     * @return loaded tasks, or an empty list when loading fails
     */
    private static ArrayList<Task> loadTasks(Storage storage, Ui ui) {
        try {
            Storage.LoadResult result = storage.load();
            if (result.skippedLineCount() > 0) {
                int skippedLineCount = result.skippedLineCount();
                String recordLabel = skippedLineCount == 1 ? "record was" : "records were";
                ui.showMessage("Warning: " + skippedLineCount + " malformed task "
                        + recordLabel + " skipped while loading data/henry.txt.");
            }
            return result.tasks();
        } catch (IOException e) {
            ui.showMessage("I couldn't load tasks from data/henry.txt. "
                    + "Starting with an empty task list.");
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
