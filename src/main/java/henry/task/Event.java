package henry.task;

/**
 * Represents a task that takes place between specified start and end times.
 */
public class Event extends Task {
    private final String startTime;
    private final String endTime;

    /**
     * Creates an event task that is initially not done.
     *
     * @param description description of the task.
     * @param startTime date or time at which the event starts.
     * @param endTime date or time at which the event ends.
     */
    public Event(String description, String startTime, String endTime) {
        super(description);
        this.startTime = startTime;
        this.endTime = endTime;
    }

    @Override
    public String toFileString() {
        return "E | " + super.toFileString() + " | " + escapeField(startTime)
                + " | " + escapeField(endTime);
    }

    @Override
    public String toString() {
        return "[E]" + super.toString()
                + " (from: " + startTime + " to: " + endTime + ")";
    }
}
