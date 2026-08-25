import java.io.IOException;
import java.nio.file.Path;

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
        TaskList tasks = loadTasks(storage, ui);

        while (ui.hasNextCommand()) {
            String command = ui.readCommand();
            CommandType commandType = Parser.parseCommandType(command);
            try {
                switch (commandType) {
                case BYE:
                    ui.showGoodbye();
                    return;
                case LIST:
                    ui.showTaskList(tasks.asList());
                    break;
                case MARK:
                    int taskIndex = Parser.parseTaskIndex(command, commandType, tasks.size());
                    updateTaskStatus(tasks, taskIndex, true, storage);
                    ui.showTaskMarked(tasks.get(taskIndex));
                    break;
                case UNMARK:
                    int unmarkedTaskIndex = Parser.parseTaskIndex(
                            command, commandType, tasks.size());
                    updateTaskStatus(tasks, unmarkedTaskIndex, false, storage);
                    ui.showTaskUnmarked(tasks.get(unmarkedTaskIndex));
                    break;
                case DELETE:
                    int deletedTaskIndex = Parser.parseTaskIndex(
                            command, commandType, tasks.size());
                    Task removedTask = tasks.delete(deletedTaskIndex);
                    try {
                        storage.save(tasks.asList());
                    } catch (IOException e) {
                        tasks.add(deletedTaskIndex, removedTask);
                        throw e;
                    }
                    ui.showTaskDeleted(removedTask, tasks.size());
                    break;
                case TODO:
                case DEADLINE:
                case EVENT:
                    addTask(tasks, Parser.parseTask(command, commandType), storage, ui);
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
     * Adds a task to the list and prints its confirmation.
     *
     * @param tasks task list to update
     * @param task task to add
     * @param storage storage used to save the updated list
     * @param ui console UI used to display the confirmation
     * @throws IOException if the updated task list cannot be saved
     */
    private static void addTask(TaskList tasks, Task task, Storage storage, Ui ui)
            throws IOException {
        tasks.add(task);
        try {
            storage.save(tasks.asList());
        } catch (IOException e) {
            tasks.delete(tasks.size() - 1);
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
    private static TaskList loadTasks(Storage storage, Ui ui) {
        try {
            Storage.LoadResult result = storage.load();
            if (result.skippedLineCount() > 0) {
                int skippedLineCount = result.skippedLineCount();
                String recordLabel = skippedLineCount == 1 ? "record was" : "records were";
                ui.showMessage("Warning: " + skippedLineCount + " malformed task "
                        + recordLabel + " skipped while loading data/henry.txt.");
            }
            return new TaskList(result.tasks());
        } catch (IOException e) {
            ui.showMessage("I couldn't load tasks from data/henry.txt. "
                    + "Starting with an empty task list.");
            return new TaskList();
        }
    }

    /**
     * Changes a task's status and restores it if the updated list cannot be saved.
     */
    private static void updateTaskStatus(TaskList tasks, int taskIndex, boolean isDone,
            Storage storage) throws IOException {
        Task task = tasks.get(taskIndex);
        boolean wasDone = task.isDone;
        if (isDone) {
            tasks.mark(taskIndex);
        } else {
            tasks.unmark(taskIndex);
        }

        try {
            storage.save(tasks.asList());
        } catch (IOException e) {
            if (wasDone) {
                tasks.mark(taskIndex);
            } else {
                tasks.unmark(taskIndex);
            }
            throw e;
        }
    }
}
