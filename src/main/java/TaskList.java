import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** Owns the collection of tasks and the operations that change or search it. */
public class TaskList {
    private final ArrayList<Task> tasks;

    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /** Creates a task list containing tasks loaded from storage. */
    public TaskList(List<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
    }

    public int size() {
        return tasks.size();
    }

    /** Returns a read-only snapshot for displaying or saving the current tasks. */
    public List<Task> asList() {
        return List.copyOf(tasks);
    }

    public void add(Task task) {
        tasks.add(task);
    }

    public Task delete(int index) {
        return tasks.remove(index);
    }

    /** Marks one task and prevents an already-completed task from being marked again. */
    public Task mark(int index) throws TodException {
        Task task = tasks.get(index);
        if (task.isDone()) {
            throw new TodException("That task is already marked.");
        }
        task.markAsDone();
        return task;
    }

    /** Unmarks one task and prevents an incomplete task from being unmarked again. */
    public Task unmark(int index) throws TodException {
        Task task = tasks.get(index);
        if (!task.isDone()) {
            throw new TodException("That task is already unmarked.");
        }
        task.markAsUndone();
        return task;
    }

    /**
     * Finds one-based task numbers for deadlines due and events taking place on a date.
     * Original numbers are retained so they can be used with mark, unmark, or delete.
     */
    public List<Integer> findTaskNumbersOn(LocalDate date) {
        ArrayList<Integer> taskNumbers = new ArrayList<>();
        for (int i = 0; i < tasks.size(); i++) {
            if (occursOn(tasks.get(i), date)) {
                taskNumbers.add(i + 1);
            }
        }
        return taskNumbers;
    }

    /** Returns whether a deadline or event belongs in a date search result. */
    private boolean occursOn(Task task, LocalDate date) {
        if (task instanceof Deadline) {
            Deadline deadline = (Deadline) task;
            return deadline.getBy().toLocalDate().equals(date);
        }
        if (task instanceof Event) {
            Event event = (Event) task;
            LocalDate from = event.getFrom().toLocalDate();
            LocalDate to = event.getTo().toLocalDate();
            return !date.isBefore(from) && !date.isAfter(to);
        }
        return false;
    }
}
