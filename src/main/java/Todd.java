import java.nio.file.Path;
import java.time.LocalDate;
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
                Command command = Parser.parse(txt);

                switch (command) {
                    case BYE:
                        ui.showGoodbye();
                        return;

                    case LIST:
                        ui.showTaskList(tasks.asList());
                        break;

                    case MARK: {
                        int index = Parser.parseIndex(txt, "mark", tasks.size());
                        Task task = tasks.mark(index);
                        storage.save(tasks.asList());
                        ui.showMarked(task);
                        break;
                    }

                    case UNMARK: {
                        int index = Parser.parseIndex(txt, "unmark", tasks.size());
                        Task task = tasks.unmark(index);
                        storage.save(tasks.asList());
                        ui.showUnmarked(task);
                        break;
                    }

                    case TODO: {
                        Task newTask = Parser.parseTodo(txt);
                        tasks.add(newTask);
                        storage.save(tasks.asList());
                        ui.showAdded(newTask, tasks.size());
                        break;
                    }

                    case DEADLINE: {
                        Task newTask = Parser.parseDeadline(txt);
                        tasks.add(newTask);
                        storage.save(tasks.asList());
                        ui.showAdded(newTask, tasks.size());
                        break;
                    }

                    case EVENT: {
                        Task newTask = Parser.parseEvent(txt);
                        tasks.add(newTask);
                        storage.save(tasks.asList());
                        ui.showAdded(newTask, tasks.size());
                        break;
                    }

                    case DELETE: {
                        int index = Parser.parseIndex(txt, "delete", tasks.size());
                        Task removed = tasks.delete(index);
                        storage.save(tasks.asList());
                        ui.showDeleted(removed, tasks.size());
                        break;
                    }

                    case ON: {
                        LocalDate date = Parser.parseDate(txt);
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

}
