import java.time.LocalDateTime;

/** Represents a task that must be completed by a specific date or time. */
public class Deadline extends Task {
    protected LocalDateTime by;

    public Deadline(String description, LocalDateTime by) {
        super(description);
        this.by = by;
    }

    public LocalDateTime getBy() {
        return by;
    }

    @Override
    public String toString() {
        return "[D][" + getStatusIcon() + "] " + getDescription()
                + " (by: " + DateTimeUtil.format(by) + ")";
    }
}
