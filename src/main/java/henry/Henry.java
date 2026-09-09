package henry;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import henry.exception.HenryException;
import henry.parser.CommandType;
import henry.parser.Parser;
import henry.storage.Storage;
import henry.task.Task;
import henry.task.TaskList;
import henry.ui.Ui;

/**
 * Starts the Henry chatbot application.
 */
public class Henry {
    private static final Path DEFAULT_DATA_FILE_PATH = Path.of("data", "henry.txt");
    private static final String GOODBYE_MESSAGE = "Bye. Hope to see you again soon!";

    private final Storage storage;
    private final TaskList tasks;
    private final String startupMessage;

    private CommandType lastCommandType = CommandType.UNKNOWN;

    /**
     * Creates Henry using the default data file.
     */
    public Henry() {
        this(DEFAULT_DATA_FILE_PATH);
    }

    /**
     * Creates Henry using the specified data file.
     *
     * @param dataFilePath path of the file used to persist tasks.
     */
    public Henry(Path dataFilePath) {
        storage = new Storage(dataFilePath);

        TaskList loadedTasks;
        String loadingMessage = "";
        try {
            Storage.LoadResult result = storage.load();
            loadedTasks = new TaskList(result.tasks());
            if (result.skippedLineCount() > 0) {
                int skippedLineCount = result.skippedLineCount();
                String recordLabel = skippedLineCount == 1 ? "record was" : "records were";
                loadingMessage = "Warning: " + skippedLineCount + " malformed task "
                        + recordLabel + " skipped while loading " + dataFilePath + ".";
            }
        } catch (IOException e) {
            loadedTasks = new TaskList();
            loadingMessage = "I couldn't load tasks from " + dataFilePath
                    + ". Starting with an empty task list.";
        }
        tasks = loadedTasks;
        startupMessage = loadingMessage;
    }

    /**
     * Greets the user, stores tasks, updates or deletes tasks, lists saved tasks, and exits
     * when the user enters bye.
     *
     * @param args command-line arguments; not used.
     */
    public static void main(String[] args) {
        Ui ui = new Ui();
        Henry henry = new Henry();
        ui.showWelcome();
        if (!henry.getStartupMessage().isEmpty()) {
            ui.showMessage(henry.getStartupMessage());
        }

        while (ui.hasNextCommand()) {
            String command = ui.readCommand();
            ui.showMessage(henry.getResponse(command));
            if (henry.getLastCommandType() == CommandType.BYE) {
                return;
            }
        }
    }

    /**
     * Returns any warning produced while loading saved tasks.
     *
     * @return loading warning, or an empty string when loading succeeded normally.
     */
    public String getStartupMessage() {
        return startupMessage;
    }

    /**
     * Returns the type of the most recently processed command.
     *
     * @return most recent command type, or {@link CommandType#UNKNOWN} before processing begins.
     */
    public CommandType getLastCommandType() {
        return lastCommandType;
    }

    /**
     * Processes a user command and returns Henry's response.
     *
     * @param input user command to process.
     * @return response suitable for display in either the console or GUI.
     */
    public String getResponse(String input) {
        String command = input.trim();
        CommandType commandType = Parser.parseCommandType(command);
        lastCommandType = commandType;

        try {
            return switch (commandType) {
                case BYE -> GOODBYE_MESSAGE;
                case LIST -> formatTaskList(" Here are the tasks in your list:", tasks.asList());
                case FIND -> formatTaskList(" Here are the matching tasks in your list:",
                        tasks.find(Parser.parseKeyword(command)));
                case MARK, UNMARK -> updateTaskStatus(command, commandType);
                case DELETE -> deleteTask(command);
                case TODO, DEADLINE, EVENT -> addTask(
                        Parser.parseTask(command, commandType));
                case UNKNOWN -> throw new HenryException(
                        "I don't recognise that command. Try todo, deadline, event, list, find, "
                                + "mark, unmark, delete, or bye.");
            };
        } catch (HenryException e) {
            return e.getMessage();
        } catch (IOException e) {
            return "I couldn't save your tasks. Your last change was not applied.";
        }
    }

    private String addTask(Task task) throws IOException {
        tasks.add(task);
        saveOrRollback(() -> tasks.delete(tasks.size() - 1));
        return " Got it. I've added this task:\n   " + task
                + "\n Now you have " + tasks.size() + " tasks in the list.";
    }

    private String deleteTask(String command) throws HenryException, IOException {
        int taskIndex = Parser.parseTaskIndex(command, CommandType.DELETE, tasks.size());
        Task removedTask = tasks.delete(taskIndex);
        saveOrRollback(() -> tasks.add(taskIndex, removedTask));
        return " Noted. I've removed this task:\n   " + removedTask
                + "\n Now you have " + tasks.size() + " tasks in the list.";
    }

    private String updateTaskStatus(String command, CommandType commandType)
            throws HenryException, IOException {
        int taskIndex = Parser.parseTaskIndex(command, commandType, tasks.size());
        Task task = tasks.get(taskIndex);
        boolean wasDone = task.isDone();
        boolean isDone = commandType == CommandType.MARK;
        tasks.setDone(taskIndex, isDone);
        saveOrRollback(() -> tasks.setDone(taskIndex, wasDone));

        if (isDone) {
            return " Nice! I've marked this task as done:\n   " + task;
        }
        return " OK, I've marked this task as not done yet:\n   " + task;
    }

    private void saveOrRollback(Runnable rollbackAction) throws IOException {
        try {
            storage.save(tasks.asList());
        } catch (IOException e) {
            rollbackAction.run();
            throw e;
        }
    }

    private static String formatTaskList(String heading, List<Task> displayedTasks) {
        StringBuilder response = new StringBuilder(heading);
        for (int i = 0; i < displayedTasks.size(); i++) {
            response.append("\n ").append(i + 1).append(".").append(displayedTasks.get(i));
        }
        return response.toString();
    }
}
