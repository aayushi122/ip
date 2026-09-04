import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Scanner;

/** Runs the Todd task-management chatbot. */
public class Todd {
    public static void main(String[] args) {
        String line = "\t____________________________________________________________";
        String banner = "   ______          __     __\n"
                + "  /_  __/___  ____/ /____/ /\n"
                + "   / / / __ \\/ __  / __  / \n"
                + "  / / / /_/ / /_/ / /_/ /  \n"
                + " /_/  \\____/\\__,_/\\__,_/   \n";
        Storage storage = new Storage(Path.of("data", "todd.txt"));
        ArrayList<Task> list;

        System.out.println(banner);
        System.out.println("Hello There! I'm Todd, a NPC Chatbot :P\nWhat can I do for you today?");

        try {
            list = storage.load();
        } catch (TodException e) {
            System.out.println(line);
            System.out.println("\t " + e.getMessage());
            System.out.println("\t Todd will start with an empty task list.");
            System.out.println(line);
            list = new ArrayList<>();
        }

        Scanner sc = new Scanner(System.in);
        String txt = sc.nextLine();

        while (true) {
            try {
                String commandWord = txt.split(" ")[0];
                Command command;
                switch (commandWord) {
                    case "bye": command = Command.BYE; break;
                    case "list": command = Command.LIST; break;
                    case "mark": command = Command.MARK; break;
                    case "unmark": command = Command.UNMARK; break;
                    case "todo": command = Command.TODO; break;
                    case "deadline": command = Command.DEADLINE; break;
                    case "event": command = Command.EVENT; break;
                    case "delete": command = Command.DELETE; break;
                    case "on": command = Command.ON; break;
                    default: command = Command.UNKNOWN;
                }

                switch (command) {
                    case BYE:
                        System.out.println("Noo don't go, come back. Ok fine bye. See you soon.");
                        return;

                    case LIST:
                        System.out.println(line);
                        if (list.isEmpty()) {
                            System.out.println("\t Your list is empty! Add some tasks first.");
                        } else {
                            System.out.println("\t Here are the tasks in your list:");
                            for (int i = 0; i < list.size(); i++) {
                                System.out.println("\t  " + (i + 1) + "." + list.get(i));
                            }
                        }
                        System.out.println(line);
                        break;

                    case MARK: {
                        int index = parseIndex(txt, "mark", list.size());
                        if (list.get(index).isDone()) {
                            throw new TodException("That task is already marked.");
                        }
                        list.get(index).markAsDone();
                        storage.save(list);
                        System.out.println(line);
                        System.out.println("\t Hooray! You are on fire! Task crossed off.");
                        System.out.println("\t    " + list.get(index));
                        System.out.println(line);
                        break;
                    }

                    case UNMARK: {
                        int index = parseIndex(txt, "unmark", list.size());
                        if (!list.get(index).isDone()) {
                            throw new TodException("That task is already unmarked.");
                        }
                        list.get(index).markAsUndone();
                        storage.save(list);
                        System.out.println(line);
                        System.out.println("\t Oh no! Okay unmarked.");
                        System.out.println("\t    " + list.get(index));
                        System.out.println(line);
                        break;
                    }

                    case TODO: {
                        String description = txt.length() > 4 ? txt.substring(5).trim() : "";
                        if (description.isEmpty()) {
                            throw new TodException("Wait you didn't even tell me what is the to-do");
                        }
                        Task newTask = new Todo(description);
                        list.add(newTask);
                        storage.save(list);
                        printAdded(line, newTask, list.size());
                        break;
                    }

                    case DEADLINE: {
                        String rest = txt.length() > 8 ? txt.substring(9).trim() : "";
                        if (rest.isEmpty()) {
                            throw new TodException("Wait you didn't even tell me anything about this deadline");
                        }
                        String[] parts = rest.split(" /by ", 2);
                        String description = parts[0].trim();
                        if (description.isEmpty()) {
                            throw new TodException("Try Again! The description is literally empty.");
                        }
                        if (parts.length < 2 || parts[1].trim().isEmpty()) {
                            throw new TodException("Try Again! There is no /by date or time.");
                        }
                        LocalDateTime by = DateTimeUtil.parseDateTime(parts[1].trim());
                        Task newTask = new Deadline(description, by);
                        list.add(newTask);
                        storage.save(list);
                        printAdded(line, newTask, list.size());
                        break;
                    }

                    case EVENT: {
                        String rest = txt.length() > 5 ? txt.substring(6).trim() : "";
                        if (rest.isEmpty()) {
                            throw new TodException("Wait you didn't even tell me anything about this event");
                        }
                        String[] fromSplit = rest.split(" /from ", 2);
                        String description = fromSplit[0].trim();
                        if (description.isEmpty()) {
                            throw new TodException("Try Again! The description is literally empty.");
                        }
                        if (fromSplit.length < 2 || fromSplit[1].trim().isEmpty()) {
                            throw new TodException("Try Again! There is no /from time.");
                        }
                        String[] toSplit = fromSplit[1].split(" /to ", 2);
                        String fromText = toSplit[0].trim();
                        if (toSplit.length < 2 || toSplit[1].trim().isEmpty()) {
                            throw new TodException("Try Again! There is no /to time.");
                        }
                        String toText = toSplit[1].trim();
                        LocalDateTime from = DateTimeUtil.parseDateTime(fromText);
                        LocalDateTime to = DateTimeUtil.parseDateTime(toText);
                        if (to.isBefore(from)) {
                            throw new TodException("The /to date and time cannot be before /from.");
                        }
                        Task newTask = new Event(description, from, to);
                        list.add(newTask);
                        storage.save(list);
                        printAdded(line, newTask, list.size());
                        break;
                    }

                    case DELETE: {
                        int index = parseIndex(txt, "delete", list.size());
                        Task removed = list.remove(index);
                        storage.save(list);
                        System.out.println(line);
                        System.out.println("\t Noted. I've removed this task:");
                        System.out.println("\t   " + removed);
                        System.out.println("\t Now you have " + list.size() + " tasks in the list.");
                        System.out.println(line);
                        break;
                    }

                    case ON: {
                        String dateText = txt.length() > 2 ? txt.substring(2).trim() : "";
                        LocalDate date = DateTimeUtil.parseDate(dateText);
                        printTasksOnDate(line, list, date);
                        break;
                    }

                    case UNKNOWN:
                    default:
                        throw new TodException("What does that mean dawg");
                }

            } catch (TodException e) {
                System.out.println(line);
                System.out.println("\t " + e.getMessage());
                System.out.println(line);
            }

            txt = sc.nextLine();
        }
    }

    private static int parseIndex(String txt, String command, int listSize) throws TodException {
        String numberPart = txt.length() > command.length() ? txt.substring(command.length()).trim() : "";
        boolean isValidNumber = !numberPart.isEmpty();
        for (int i = 0; i < numberPart.length(); i++) {
            if (!Character.isDigit(numberPart.charAt(i))) {
                isValidNumber = false;
            }
        }
        if (!isValidNumber) {
            throw new TodException("Brother you didnt give me a valid task number to " + command + ".");
        }
        int index = Integer.parseInt(numberPart) - 1;
        if (index < 0 || index >= listSize) {
            throw new TodException("Am I tripping cuz that task number literally doesn't exist!");
        }
        return index;
    }

    private static void printAdded(String line, Task newTask, int totalTasks) {
        System.out.println(line);
        System.out.println("\t Got it. I've added this task:");
        System.out.println("\t   " + newTask);
        System.out.println("\t Now you have " + totalTasks + " tasks in the list.");
        System.out.println(line);
    }

    /** Prints deadlines due and events taking place on the requested date. */
    private static void printTasksOnDate(String line, ArrayList<Task> tasks, LocalDate date) {
        System.out.println(line);
        System.out.println("\t Deadlines and events on " + DateTimeUtil.format(date) + ":");
        boolean foundTask = false;
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            if (occursOn(task, date)) {
                System.out.println("\t  " + (i + 1) + "." + task);
                foundTask = true;
            }
        }
        if (!foundTask) {
            System.out.println("\t You have no deadlines or events on that date.");
        }
        System.out.println(line);
    }

    /** Returns whether a deadline or event belongs in a date search result. */
    private static boolean occursOn(Task task, LocalDate date) {
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
