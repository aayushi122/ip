import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/** Runs the Todd task-management chatbot. */
public class Todd {
    public static void main(String[] args) {
        Ui ui = new Ui();
        Storage storage = new Storage(Path.of("data", "todd.txt"));
        TaskList tasks;

        ui.showWelcome();

        try {
            tasks = new TaskList(storage.load());
        } catch (TodException e) {
            ui.showLoadingError(e.getMessage());
            tasks = new TaskList();
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
                        ui.showTaskList(tasks.asList());
                        break;

                    case MARK: {
                        int index = parseIndex(txt, "mark", tasks.size());
                        Task task = tasks.mark(index);
                        storage.save(tasks.asList());
                        ui.showMarked(task);
                        break;
                    }

                    case UNMARK: {
                        int index = parseIndex(txt, "unmark", tasks.size());
                        Task task = tasks.unmark(index);
                        storage.save(tasks.asList());
                        ui.showUnmarked(task);
                        break;
                    }

                    case TODO: {
                        String description = txt.length() > 4 ? txt.substring(5).trim() : "";
                        if (description.isEmpty()) {
                            throw new TodException("Wait you didn't even tell me what is the to-do");
                        }
                        Task newTask = new Todo(description);
                        tasks.add(newTask);
                        storage.save(tasks.asList());
                        ui.showAdded(newTask, tasks.size());
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
                        tasks.add(newTask);
                        storage.save(tasks.asList());
                        ui.showAdded(newTask, tasks.size());
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
                        tasks.add(newTask);
                        storage.save(tasks.asList());
                        ui.showAdded(newTask, tasks.size());
                        break;
                    }

                    case DELETE: {
                        int index = parseIndex(txt, "delete", tasks.size());
                        Task removed = tasks.delete(index);
                        storage.save(tasks.asList());
                        ui.showDeleted(removed, tasks.size());
                        break;
                    }

                    case ON: {
                        String dateText = txt.length() > 2 ? txt.substring(2).trim() : "";
                        LocalDate date = DateTimeUtil.parseDate(dateText);
                        List<Integer> taskNumbers = tasks.findTaskNumbersOn(date);
                        ui.showTasksOnDate(date, tasks.asList(), taskNumbers);
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

}
