package todd;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

/** Reads console input and formats responses shared by Todd's interfaces. */
public class Ui {
    private static final String LINE = "____________________________________________________________";
    private static final String BANNER = "   ______          __     __\n"
            + "  /_  __/___  ____/ /____/ /\n"
            + "   / / / __ \\/ __  / __  / \n"
            + "  / / / /_/ / /_/ / /_/ /  \n"
            + " /_/  \\____/\\__,_/\\__,_/   \n";

    private final Scanner scanner;

    /** Creates a console UI that reads commands from standard input. */
    public Ui() {
        scanner = new Scanner(System.in);
    }

    /** Returns the next command entered by the user. */
    public String readCommand() {
        return scanner.nextLine();
    }

    /** Displays an already formatted message on the console. */
    public void show(String message) {
        System.out.println(message);
    }

    /** Returns Todd's welcome message. */
    public String formatWelcome() {
        return BANNER + "Hello There! I'm Todd, a NPC Chatbot :P\nWhat can I do for you today?";
    }

    /** Returns Todd's goodbye message. */
    public String formatGoodbye() {
        return "Noo don't go, come back. Ok fine bye. See you soon.";
    }

    /** Returns a concise guide to the commands Todd recognizes. */
    public String formatHelp() {
        return surround(
                "Here are the commands I understand:",
                "todo DESCRIPTION",
                "deadline DESCRIPTION /by DATE [HHmm]",
                "event DESCRIPTION /from DATE [HHmm] /to DATE [HHmm]",
                "list | mark NUMBER | unmark NUMBER | delete NUMBER",
                "find KEYWORD | on DATE | reminders | help | bye",
                "DATE can be yyyy-MM-dd, today, tomorrow, or a weekday such as Mon.");
    }

    /** Returns an error that prevented saved tasks from being loaded. */
    public String formatLoadingError(String message) {
        return surround(message, "Todd will start with an empty task list.");
    }

    /** Returns a user-facing command error. */
    public String formatError(String message) {
        return surround(message);
    }

    /** Returns all tasks, or an empty-list message when there are none. */
    public String formatTaskList(List<Task> tasks) {
        if (tasks.isEmpty()) {
            return surround("Your list is empty! Add some tasks first.");
        }

        StringBuilder content = new StringBuilder("Here are the tasks in your list:");
        appendNumberedTasks(content, tasks);
        return surround(content.toString());
    }

    /** Returns confirmation that the specified task was marked. */
    public String formatMarked(Task task) {
        return surround("Hooray! You are on fire! Task crossed off.", "   " + task);
    }

    /** Returns confirmation that the specified task was unmarked. */
    public String formatUnmarked(Task task) {
        return surround("Oh no! Okay unmarked.", "   " + task);
    }

    /** Returns the added task and the updated total number of tasks. */
    public String formatAdded(Task task, int totalTasks) {
        return surround(
                "Got it. I've added this task:",
                "   " + task,
                "Now you have " + totalTasks + " tasks in the list.");
    }

    /** Returns the deleted task and the updated total number of tasks. */
    public String formatDeleted(Task task, int totalTasks) {
        return surround(
                "Noted. I've removed this task:",
                "   " + task,
                "Now you have " + totalTasks + " tasks in the list.");
    }

    /** Returns tasks selected by a date search together with their original list numbers. */
    public String formatTasksOnDate(LocalDate date, List<Task> tasks, List<Integer> taskNumbers) {
        StringBuilder content = new StringBuilder("Deadlines and events on ")
                .append(DateTimeUtil.format(date)).append(":");
        if (taskNumbers.isEmpty()) {
            content.append("\nYou have no deadlines or events on that date.");
        } else {
            for (int taskNumber : taskNumbers) {
                content.append("\n ").append(taskNumber).append(".").append(tasks.get(taskNumber - 1));
            }
        }
        return surround(content.toString());
    }

    /** Returns tasks whose descriptions match a find command. */
    public String formatMatchingTasks(List<Task> matchingTasks) {
        if (matchingTasks.isEmpty()) {
            return surround("There are no matching tasks in your list.");
        }

        StringBuilder content = new StringBuilder("Here are the matching tasks in your list:");
        appendNumberedTasks(content, matchingTasks);
        return surround(content.toString());
    }

    /** Returns incomplete deadlines and events occurring in the reminder window. */
    public String formatReminders(LocalDate startDate, int numberOfDays,
                                  List<Task> tasks, List<Integer> taskNumbers) {
        LocalDate endDate = startDate.plusDays(numberOfDays - 1L);
        StringBuilder content = new StringBuilder("Upcoming incomplete deadlines and events from ")
                .append(DateTimeUtil.format(startDate))
                .append(" to ")
                .append(DateTimeUtil.format(endDate))
                .append(":");
        if (taskNumbers.isEmpty()) {
            content.append("\nYou have no upcoming deadlines or events.");
        } else {
            for (int taskNumber : taskNumbers) {
                content.append("\n ").append(taskNumber).append(".").append(tasks.get(taskNumber - 1));
            }
        }
        return surround(content.toString());
    }

    private void appendNumberedTasks(StringBuilder content, List<Task> tasks) {
        for (int i = 0; i < tasks.size(); i++) {
            content.append("\n ").append(i + 1).append(".").append(tasks.get(i));
        }
    }

    /** Wraps any number of response lines between visual separators. */
    private String surround(String... lines) {
        return LINE + "\n" + String.join("\n", lines) + "\n" + LINE;
    }
}
