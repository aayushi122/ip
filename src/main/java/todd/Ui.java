package todd;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

/** Handles all console input and output for Todd. */
public class Ui {
    private static final String LINE = "\t____________________________________________________________";
    private static final String BANNER = "   ______          __     __\n"
            + "  /_  __/___  ____/ /____/ /\n"
            + "   / / / __ \\/ __  / __  / \n"
            + "  / / / /_/ / /_/ / /_/ /  \n"
            + " /_/  \\____/\\__,_/\\__,_/   \n";

    private final Scanner scanner;

    public Ui() {
        this.scanner = new Scanner(System.in);
    }

    public String readCommand() {
        return scanner.nextLine();
    }

    public void showWelcome() {
        System.out.println(BANNER);
        System.out.println("Hello There! I'm Todd, a NPC Chatbot :P\nWhat can I do for you today?");
    }

    public void showGoodbye() {
        System.out.println("Noo don't go, come back. Ok fine bye. See you soon.");
    }

    /** Shows an error that prevented saved tasks from being loaded at startup. */
    public void showLoadingError(String message) {
        showLine();
        System.out.println("\t " + message);
        System.out.println("\t Todd will start with an empty task list.");
        showLine();
    }

    public void showError(String message) {
        showLine();
        System.out.println("\t " + message);
        showLine();
    }

    public void showTaskList(List<Task> tasks) {
        showLine();
        if (tasks.isEmpty()) {
            System.out.println("\t Your list is empty! Add some tasks first.");
        } else {
            System.out.println("\t Here are the tasks in your list:");
            for (int i = 0; i < tasks.size(); i++) {
                System.out.println("\t  " + (i + 1) + "." + tasks.get(i));
            }
        }
        showLine();
    }

    public void showMarked(Task task) {
        showLine();
        System.out.println("\t Hooray! You are on fire! Task crossed off.");
        System.out.println("\t    " + task);
        showLine();
    }

    public void showUnmarked(Task task) {
        showLine();
        System.out.println("\t Oh no! Okay unmarked.");
        System.out.println("\t    " + task);
        showLine();
    }

    public void showAdded(Task task, int totalTasks) {
        showLine();
        System.out.println("\t Got it. I've added this task:");
        System.out.println("\t   " + task);
        System.out.println("\t Now you have " + totalTasks + " tasks in the list.");
        showLine();
    }

    public void showDeleted(Task task, int totalTasks) {
        showLine();
        System.out.println("\t Noted. I've removed this task:");
        System.out.println("\t   " + task);
        System.out.println("\t Now you have " + totalTasks + " tasks in the list.");
        showLine();
    }

    /**
     * Shows tasks selected by a date search together with their original list numbers.
     *
     * @param date date requested by the user
     * @param tasks complete task list
     * @param taskNumbers one-based positions of matching tasks in the full task list
     */
    public void showTasksOnDate(LocalDate date, List<Task> tasks, List<Integer> taskNumbers) {
        showLine();
        System.out.println("\t Deadlines and events on " + DateTimeUtil.format(date) + ":");
        if (taskNumbers.isEmpty()) {
            System.out.println("\t You have no deadlines or events on that date.");
        } else {
            for (int taskNumber : taskNumbers) {
                System.out.println("\t  " + taskNumber + "." + tasks.get(taskNumber - 1));
            }
        }
        showLine();
    }

    /** Displays tasks whose descriptions match a find command. */
    public void showMatchingTasks(List<Task> matchingTasks) {
        showLine();
        if (matchingTasks.isEmpty()) {
            System.out.println("\t There are no matching tasks in your list.");
        } else {
            System.out.println("\t Here are the matching tasks in your list:");
            for (int i = 0; i < matchingTasks.size(); i++) {
                System.out.println("\t  " + (i + 1) + "." + matchingTasks.get(i));
            }
        }
        showLine();
    }

    private void showLine() {
        System.out.println(LINE);
    }
}
