package henry.parser;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

import henry.exception.HenryException;
import henry.task.Deadline;
import henry.task.Event;
import henry.task.Task;
import henry.task.Todo;

/**
 * Interprets user input and converts command arguments into application values.
 */
public class Parser {
    /**
     * Identifies the command represented by the given input.
     *
     * @param input complete user input.
     * @return matching command type, or {@link CommandType#UNKNOWN} when none matches.
     */
    public static CommandType parseCommandType(String input) {
        return CommandType.from(input);
    }

    /**
     * Extracts and validates the task number supplied to a command.
     *
     * @param input complete user input.
     * @param commandType command whose task number should be read.
     * @param taskCount current number of tasks.
     * @return zero-based index of the selected task.
     * @throws HenryException if the task number is absent, invalid, or out of range.
     */
    public static int parseTaskIndex(String input, CommandType commandType, int taskCount)
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
     * Parses a task-creation command into its corresponding task.
     *
     * @param input complete user input.
     * @param commandType task command to parse.
     * @return parsed task.
     * @throws HenryException if required task details are missing or invalid.
     */
    public static Task parseTask(String input, CommandType commandType) throws HenryException {
        return switch (commandType) {
            case TODO -> parseTodo(input);
            case DEADLINE -> parseDeadline(input);
            case EVENT -> parseEvent(input);
            default -> throw new IllegalArgumentException("Not a task command: " + commandType);
        };
    }

    private static Todo parseTodo(String input) throws HenryException {
        String description = extractArguments(input, CommandType.TODO);
        if (description.isEmpty()) {
            throw new HenryException(
                    "A todo needs a description. For example: todo borrow a book");
        }
        return new Todo(description);
    }

    private static Deadline parseDeadline(String input) throws HenryException {
        String taskDetails = extractArguments(input, CommandType.DEADLINE);
        int bySeparatorIndex = taskDetails.indexOf("/by");
        if (bySeparatorIndex < 0) {
            throw new HenryException(
                    "A deadline needs '/by'. For example: deadline submit report /by 2019-12-02");
        }

        String description = taskDetails.substring(0, bySeparatorIndex).trim();
        String by = taskDetails.substring(bySeparatorIndex + 3).trim();
        if (description.isEmpty()) {
            throw new HenryException("A deadline needs a description before '/by'.");
        }
        if (by.isEmpty()) {
            throw new HenryException("A deadline needs a date or time after '/by'.");
        }

        try {
            LocalDateTime deadline = Deadline.parseBy(by);
            return new Deadline(description, deadline);
        } catch (DateTimeParseException e) {
            throw new HenryException(
                    "Please use a deadline date like 2/12/2019 1800 or 2019-12-02.");
        }
    }

    private static Event parseEvent(String input) throws HenryException {
        String taskDetails = extractArguments(input, CommandType.EVENT);
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
        return new Event(description, from, to);
    }

    private static String extractArguments(String input, CommandType commandType) {
        return input.substring(commandType.getCommandWord().length()).trim();
    }
}
