import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

/** Runs the Todd task-management chatbot. */
public class Todd {
    public static void main(String[] args) {
        Ui ui = new Ui();
        Storage storage = new Storage(Path.of("data", "todd.txt"));
        ArrayList<Task> list;

        ui.showWelcome();

        try {
            list = storage.load();
        } catch (TodException e) {
            ui.showLoadingError(e.getMessage());
            list = new ArrayList<>();
        }

        String txt = ui.readCommand();

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
                        ui.showGoodbye();
                        return;

                    case LIST:
                        ui.showTaskList(list);
                        break;

                    case MARK: {
                        int index = parseIndex(txt, "mark", list.size());
                        if (list.get(index).isDone()) {
                            throw new TodException("That task is already marked.");
                        }
                        list.get(index).markAsDone();
                        storage.save(list);
                        ui.showMarked(list.get(index));
                        break;
                    }

                    case UNMARK: {
                        int index = parseIndex(txt, "unmark", list.size());
                        if (!list.get(index).isDone()) {
                            throw new TodException("That task is already unmarked.");
                        }
                        list.get(index).markAsUndone();
                        storage.save(list);
                        ui.showUnmarked(list.get(index));
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
                        ui.showAdded(newTask, list.size());
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
                        ui.showAdded(newTask, list.size());
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
                        ui.showAdded(newTask, list.size());
                        break;
                    }

                    case DELETE: {
                        int index = parseIndex(txt, "delete", list.size());
                        Task removed = list.remove(index);
                        storage.save(list);
                        ui.showDeleted(removed, list.size());
                        break;
                    }

                    case ON: {
                        String dateText = txt.length() > 2 ? txt.substring(2).trim() : "";
                        LocalDate date = DateTimeUtil.parseDate(dateText);
                        ArrayList<Task> matchingTasks = new ArrayList<>();
                        ArrayList<Integer> taskNumbers = new ArrayList<>();
                        for (int i = 0; i < list.size(); i++) {
                            Task task = list.get(i);
                            if (occursOn(task, date)) {
                                matchingTasks.add(task);
                                taskNumbers.add(i + 1);
                            }
                        }
                        ui.showTasksOnDate(date, matchingTasks, taskNumbers);
                        break;
                    }

                    case UNKNOWN:
                    default:
                        throw new TodException("What does that mean dawg");
                }

            } catch (TodException e) {
                ui.showError(e.getMessage());
            }

            txt = ui.readCommand();
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
