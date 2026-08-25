import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

/**
 * Loads and saves Henry's tasks using a text file on the hard disk.
 */
public class Storage {
    private static final String FIELD_SEPARATOR = " | ";
    private final Path filePath;

    /**
     * Contains the usable tasks and the number of malformed records found during loading.
     *
     * @param tasks valid tasks recovered from the data file
     * @param skippedLineCount number of malformed non-empty records
     */
    public record LoadResult(ArrayList<Task> tasks, int skippedLineCount) {
    }

    /**
     * Creates storage that writes to the given file.
     *
     * @param filePath location of the task data file
     */
    public Storage(Path filePath) {
        this.filePath = filePath;
    }

    /**
     * Loads tasks from the data file, or returns an empty list when the file does not exist.
     * <p>
     * Malformed non-empty lines are skipped so that one damaged record does not prevent valid
     * tasks from being recovered.
     *
     * @return loaded tasks and the number of records that could not be parsed
     * @throws IOException if the data file cannot be read
     */
    public LoadResult load() throws IOException {
        ArrayList<Task> tasks = new ArrayList<>();
        if (!Files.exists(filePath)) {
            return new LoadResult(tasks, 0);
        }

        int skippedLineCount = 0;
        for (String taskLine : Files.readAllLines(filePath, StandardCharsets.UTF_8)) {
            if (taskLine.isBlank()) {
                continue;
            }
            try {
                tasks.add(parseTask(taskLine));
            } catch (IllegalArgumentException e) {
                skippedLineCount++;
            }
        }
        return new LoadResult(tasks, skippedLineCount);
    }

    /**
     * Rewrites the data file with the current task list, creating its directory if needed.
     *
     * @param tasks tasks to save
     * @throws IOException if the directory or file cannot be written
     */
    public void save(List<Task> tasks) throws IOException {
        Path targetFile = filePath;
        Path parentDirectory = targetFile.getParent();
        Files.createDirectories(parentDirectory);

        ArrayList<String> taskLines = new ArrayList<>();
        for (Task task : tasks) {
            taskLines.add(task.toFileString());
        }

        Path temporaryFile = Files.createTempFile(parentDirectory, "henry-", ".tmp");
        try {
            Files.write(temporaryFile, taskLines, StandardCharsets.UTF_8);
            replaceDataFile(temporaryFile, targetFile);
        } finally {
            // This is normally already moved. If replacement failed, avoid leaving clutter behind.
            Files.deleteIfExists(temporaryFile);
        }
    }

    /**
     * Escapes characters that otherwise have structural meaning in the storage format.
     *
     * @param field user-entered text to store
     * @return field text safe for the pipe-separated file
     */
    static String escapeField(String field) {
        return field.replace("\\", "\\\\").replace("|", "\\|");
    }

    /**
     * Reconstructs one task from its pipe-separated storage representation.
     *
     * @param taskLine stored representation of one task
     * @return reconstructed task
     */
    private Task parseTask(String taskLine) {
        List<String> fields = splitFields(taskLine);
        if (fields.size() < 3) {
            throw new IllegalArgumentException("Missing task fields");
        }

        String taskType = fields.get(0);
        String status = fields.get(1);
        if (!status.equals("0") && !status.equals("1")) {
            throw new IllegalArgumentException("Invalid completion status: " + status);
        }
        boolean isDone = status.equals("1");
        String description = requireText(fields.get(2), "description");

        Task task = switch (taskType) {
        case "T" -> {
            requireFieldCount(fields, 3, taskType);
            yield new Todo(description);
        }
        case "D" -> {
            requireFieldCount(fields, 4, taskType);
            yield new Deadline(description, requireText(fields.get(3), "deadline"));
        }
        case "E" -> {
            requireFieldCount(fields, 5, taskType);
            yield new Event(description, requireText(fields.get(3), "start time"),
                    requireText(fields.get(4), "end time"));
        }
        default -> throw new IllegalArgumentException("Unknown task type: " + taskType);
        };

        if (isDone) {
            task.markAsDone();
        }
        return task;
    }

    /**
     * Checks that a stored task has the required number of fields.
     *
     * @param fields decoded task fields
     * @param expectedCount required number of fields
     * @param taskType stored one-letter task type used in the error message
     * @throws IllegalArgumentException if the field count is incorrect
     */
    private void requireFieldCount(List<String> fields, int expectedCount, String taskType) {
        if (fields.size() != expectedCount) {
            throw new IllegalArgumentException("Wrong number of fields for task type " + taskType);
        }
    }

    /**
     * Splits a stored record and decodes escaped pipes and backslashes in one pass.
     *
     * @param taskLine complete stored task record
     * @return decoded fields from the record
     */
    private List<String> splitFields(String taskLine) {
        ArrayList<String> fields = new ArrayList<>();
        StringBuilder currentField = new StringBuilder();

        for (int i = 0; i < taskLine.length();) {
            if (taskLine.startsWith(FIELD_SEPARATOR, i)) {
                fields.add(currentField.toString());
                currentField.setLength(0);
                i += FIELD_SEPARATOR.length();
            } else if (taskLine.charAt(i) == '\\' && i + 1 < taskLine.length()
                    && (taskLine.charAt(i + 1) == '\\' || taskLine.charAt(i + 1) == '|')) {
                currentField.append(taskLine.charAt(i + 1));
                i += 2;
            } else {
                currentField.append(taskLine.charAt(i));
                i++;
            }
        }
        fields.add(currentField.toString());
        return fields;
    }

    /**
     * Rejects empty values that cannot be created through Henry's command interface.
     *
     * @param field decoded field text
     * @param fieldName name used if validation fails
     * @return the validated field
     * @throws IllegalArgumentException if the field contains no non-whitespace text
     */
    private String requireText(String field, String fieldName) {
        if (field.isBlank()) {
            throw new IllegalArgumentException("Missing " + fieldName);
        }
        return field;
    }

    /**
     * Atomically replaces the old data where supported, with a portable fallback.
     *
     * @param temporaryFile completely written replacement file
     * @param targetFile configured data-file location
     * @throws IOException if neither replacement method succeeds
     */
    private void replaceDataFile(Path temporaryFile, Path targetFile) throws IOException {
        try {
            Files.move(temporaryFile, targetFile, StandardCopyOption.ATOMIC_MOVE,
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (AtomicMoveNotSupportedException e) {
            Files.move(temporaryFile, targetFile, StandardCopyOption.REPLACE_EXISTING);
        }
    }
}
