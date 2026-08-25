package henry.task;

/**
 * Represents a task and whether it has been completed.
 */
public class Task {
    protected String description;
    protected boolean isDone;

    /**
     * Creates a task that is initially not done.
     *
     * @param description description of the task
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /**
     * Returns the character used to display the task's completion status.
     *
     * @return {@code "X"} when done, or a space otherwise
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /**
     * Returns whether this task has been completed.
     *
     * @return true if this task is done
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Marks this task as completed.
     */
    public void markAsDone() {
        isDone = true;
    }

    /**
     * Marks this task as not completed.
     */
    public void markAsNotDone() {
        isDone = false;
    }

    /**
     * Returns the task fields shared by every task type in the storage format.
     *
     * @return completion status and description separated by {@code " | "}
     */
    public String toFileString() {
        return (isDone ? "1" : "0") + " | " + escapeField(description);
    }

    /**
     * Escapes characters that otherwise have structural meaning in the storage format.
     *
     * @param field task text to store
     * @return field text safe for the pipe-separated file
     */
    protected static String escapeField(String field) {
        return field.replace("\\", "\\\\").replace("|", "\\|");
    }

    /**
     * Returns the task in its display format.
     *
     * @return the status icon followed by the task description
     */
    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }
}
