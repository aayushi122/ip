import java.time.LocalDateTime;

/** Represents a task that takes place between a start and end time. */
public class Event extends Task {
    protected LocalDateTime from;
    protected LocalDateTime to;

    public Event(String description, LocalDateTime from, LocalDateTime to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    public LocalDateTime getFrom() {
        return from;
    }

    public LocalDateTime getTo() {
        return to;
    }

    @Override
    public String toString() {
        return "[E][" + getStatusIcon() + "] " + getDescription()
                + " (from: " + DateTimeUtil.format(from) + " to: " + DateTimeUtil.format(to) + ")";
    }
}
