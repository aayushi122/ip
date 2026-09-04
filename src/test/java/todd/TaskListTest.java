package todd;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

/** Tests the state-changing and date-search operations in {@link TaskList}. */
public class TaskListTest {
    @Test
    public void addAndDelete_tasks_updatesListAndReturnsDeletedTask() {
        TaskList taskList = new TaskList();
        Todo firstTask = new Todo("read book");
        Todo secondTask = new Todo("write report");

        taskList.add(firstTask);
        taskList.add(secondTask);
        Task deletedTask = taskList.delete(0);

        assertSame(firstTask, deletedTask);
        assertEquals(List.of(secondTask), taskList.asList());
    }

    @Test
    public void mark_unmarkedTask_marksAndReturnsTask() throws TodException {
        Todo task = new Todo("read book");
        TaskList taskList = new TaskList(List.of(task));

        Task markedTask = taskList.mark(0);

        assertSame(task, markedTask);
        assertTrue(task.isDone());
    }

    @Test
    public void mark_alreadyMarkedTask_throwsTodException() throws TodException {
        Todo task = new Todo("read book");
        TaskList taskList = new TaskList(List.of(task));
        taskList.mark(0);

        assertThrows(TodException.class, () -> taskList.mark(0));
    }

    @Test
    public void unmark_markedTask_unmarksAndReturnsTask() throws TodException {
        Todo task = new Todo("read book");
        task.markAsDone();
        TaskList taskList = new TaskList(List.of(task));

        Task unmarkedTask = taskList.unmark(0);

        assertSame(task, unmarkedTask);
        assertFalse(task.isDone());
    }

    @Test
    public void unmark_alreadyUnmarkedTask_throwsTodException() {
        TaskList taskList = new TaskList(List.of(new Todo("read book")));

        assertThrows(TodException.class, () -> taskList.unmark(0));
    }

    @Test
    public void findTaskNumbersOn_mixedTasks_returnsOriginalMatchingNumbers() {
        TaskList taskList = new TaskList(List.of(
                new Todo("read book"),
                new Deadline("submit report", LocalDateTime.of(2026, 9, 4, 23, 59)),
                new Deadline("pay bill", LocalDateTime.of(2026, 9, 5, 12, 0)),
                new Event("conference", LocalDateTime.of(2026, 9, 3, 9, 0),
                        LocalDateTime.of(2026, 9, 5, 17, 0))));

        List<Integer> result = taskList.findTaskNumbersOn(LocalDate.of(2026, 9, 4));

        assertEquals(List.of(2, 4), result);
    }

    @Test
    public void findTaskNumbersOn_eventBoundaryDates_includesBothBoundaries() {
        TaskList taskList = new TaskList(List.of(
                new Event("camp", LocalDateTime.of(2026, 9, 4, 9, 0),
                        LocalDateTime.of(2026, 9, 6, 17, 0))));

        assertEquals(List.of(1), taskList.findTaskNumbersOn(LocalDate.of(2026, 9, 4)));
        assertEquals(List.of(1), taskList.findTaskNumbersOn(LocalDate.of(2026, 9, 6)));
        assertEquals(List.of(), taskList.findTaskNumbersOn(LocalDate.of(2026, 9, 7)));
    }

    @Test
    public void find_keywordInMixedTaskDescriptions_returnsMatchesInOriginalOrder() {
        Todo firstMatch = new Todo("read book");
        Deadline nonMatch = new Deadline("submit report",
                LocalDateTime.of(2026, 9, 4, 23, 59));
        Event secondMatch = new Event("book club meeting",
                LocalDateTime.of(2026, 9, 5, 9, 0),
                LocalDateTime.of(2026, 9, 5, 10, 0));
        TaskList taskList = new TaskList(List.of(firstMatch, nonMatch, secondMatch));

        List<Task> result = taskList.find("book");

        assertEquals(List.of(firstMatch, secondMatch), result);
    }

    @Test
    public void find_keywordAbsent_returnsEmptyList() {
        TaskList taskList = new TaskList(List.of(
                new Todo("read book"),
                new Todo("write report")));

        assertEquals(List.of(), taskList.find("exercise"));
    }
}
