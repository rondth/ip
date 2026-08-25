import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Loads and saves Henry's tasks using a text file on the hard disk.
 */
public class Storage {
    private final Path filePath;

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
     *
     * @return tasks reconstructed from the data file
     * @throws IOException if the data file cannot be read
     */
    public ArrayList<Task> load() throws IOException {
        ArrayList<Task> tasks = new ArrayList<>();
        if (!Files.exists(filePath)) {
            return tasks;
        }

        for (String taskLine : Files.readAllLines(filePath, StandardCharsets.UTF_8)) {
            tasks.add(parseTask(taskLine));
        }
        return tasks;
    }

    /**
     * Rewrites the data file with the current task list, creating its directory if needed.
     *
     * @param tasks tasks to save
     * @throws IOException if the directory or file cannot be written
     */
    public void save(List<Task> tasks) throws IOException {
        Path parentDirectory = filePath.getParent();
        if (parentDirectory != null) {
            Files.createDirectories(parentDirectory);
        }

        ArrayList<String> taskLines = new ArrayList<>();
        for (Task task : tasks) {
            taskLines.add(task.toFileString());
        }
        Files.write(filePath, taskLines, StandardCharsets.UTF_8);
    }

    /**
     * Reconstructs one task from its pipe-separated storage representation.
     *
     * @param taskLine stored representation of one task
     * @return reconstructed task
     */
    private Task parseTask(String taskLine) {
        String[] fields = taskLine.split(" \\| ");
        String taskType = fields[0];
        boolean isDone = fields[1].equals("1");
        String description = fields[2];

        Task task;
        switch (taskType) {
        case "T":
            task = new Todo(description);
            break;
        case "D":
            task = new Deadline(description, fields[3]);
            break;
        case "E":
            task = new Event(description, fields[3], fields[4]);
            break;
        default:
            throw new IllegalArgumentException("Unknown task type: " + taskType);
        }

        if (isDone) {
            task.markAsDone();
        }
        return task;
    }
}
