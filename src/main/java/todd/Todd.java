package todd;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

/** Runs the Todd task-management chatbot and processes user commands. */
public class Todd {
    private static final Path DEFAULT_DATA_PATH = Path.of("data", "todd.txt");

    private final Ui ui;
    private final Storage storage;
    private final TaskList tasks;
    private final String loadingError;

    /** Creates Todd using the default data-file location. */
    public Todd() {
        this(DEFAULT_DATA_PATH);
    }

    /** Creates Todd using a specified data file, primarily for testing. */
    public Todd(Path dataPath) {
        ui = new Ui();
        storage = new Storage(dataPath);

        TaskList loadedTasks;
        String error = null;
        try {
            loadedTasks = new TaskList(storage.load());
        } catch (TodException e) {
            loadedTasks = new TaskList();
            error = ui.formatLoadingError(e.getMessage());
        }
        tasks = loadedTasks;
        loadingError = error;
    }

    /** Starts Todd's text-based interface. */
    public static void main(String[] args) {
        new Todd().run();
    }

    /** Returns the startup message shown by both interfaces. */
    public String getWelcomeMessage() {
        if (loadingError == null) {
            return ui.formatWelcome();
        }
        return ui.formatWelcome() + System.lineSeparator() + loadingError;
    }

    /**
     * Processes one command and returns the response for either user interface.
     *
     * @param input command entered by the user
     * @return response to display
     */
    public String getResponse(String input) {
        try {
            Command command = Parser.parse(input);
            return execute(command, input);
        } catch (TodException e) {
            return ui.formatError(e.getMessage());
        }
    }

    private void run() {
        ui.show(getWelcomeMessage());

        while (true) {
            String input = ui.readCommand();
            Command command = Parser.parse(input);
            ui.show(getResponse(input));
            if (command == Command.BYE) {
                return;
            }
        }
    }

    private String execute(Command command, String input) throws TodException {
        return switch (command) {
            case BYE -> ui.formatGoodbye();
            case LIST -> ui.formatTaskList(tasks.asList());
            case MARK -> markTask(input);
            case UNMARK -> unmarkTask(input);
            case TODO -> addTask(Parser.parseTodo(input));
            case DEADLINE -> addTask(Parser.parseDeadline(input));
            case EVENT -> addTask(Parser.parseEvent(input));
            case DELETE -> deleteTask(input);
            case ON -> findTasksOnDate(input);
            case FIND -> findTasksByKeyword(input);
            case UNKNOWN -> throw new TodException("What does that mean dawg");
        };
    }

    /** Marks the task selected by a mark command and saves the updated list. */
    private String markTask(String input) throws TodException {
        int index = Parser.parseIndex(input, "mark", tasks.size());
        Task task = tasks.mark(index);
        saveTasks();
        return ui.formatMarked(task);
    }

    /** Unmarks the task selected by an unmark command and saves the updated list. */
    private String unmarkTask(String input) throws TodException {
        int index = Parser.parseIndex(input, "unmark", tasks.size());
        Task task = tasks.unmark(index);
        saveTasks();
        return ui.formatUnmarked(task);
    }

    /** Adds a parsed task, saves it, and formats a confirmation. */
    private String addTask(Task task) throws TodException {
        tasks.add(task);
        saveTasks();
        return ui.formatAdded(task, tasks.size());
    }

    /** Deletes the task selected by a delete command and saves the updated list. */
    private String deleteTask(String input) throws TodException {
        int index = Parser.parseIndex(input, "delete", tasks.size());
        Task removed = tasks.delete(index);
        saveTasks();
        return ui.formatDeleted(removed, tasks.size());
    }

    /** Formats tasks that occur on the date supplied by an on command. */
    private String findTasksOnDate(String input) throws TodException {
        LocalDate date = Parser.parseDate(input);
        List<Integer> taskNumbers = tasks.findTaskNumbersOn(date);
        return ui.formatTasksOnDate(date, tasks.asList(), taskNumbers);
    }

    /** Formats tasks matching the keyword supplied by a find command. */
    private String findTasksByKeyword(String input) throws TodException {
        String keyword = Parser.parseKeyword(input);
        return ui.formatMatchingTasks(tasks.find(keyword));
    }

    private void saveTasks() throws TodException {
        storage.save(tasks.asList());
    }
}
