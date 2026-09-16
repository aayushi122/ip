package todd;

import java.time.LocalDate;
import java.time.LocalDateTime;

/** Converts raw user input into commands and validated command arguments. */
public final class Parser {
    private Parser() {
    }

    /** Identifies the command word at the start of the user's input. */
    public static Command parse(String input) {
        String commandWord = input.trim().split("\\s+", 2)[0];
        switch (commandWord) {
            case "bye":
                return Command.BYE;
            case "list":
                return Command.LIST;
            case "mark":
                return Command.MARK;
            case "unmark":
                return Command.UNMARK;
            case "todo":
                return Command.TODO;
            case "deadline":
                return Command.DEADLINE;
            case "event":
                return Command.EVENT;
            case "delete":
                return Command.DELETE;
            case "on":
                return Command.ON;
            case "find":
                return Command.FIND;
            case "help":
                return Command.HELP;
            case "reminders":
                return Command.REMINDERS;
            default:
                return Command.UNKNOWN;
        }
    }

    /** Parses and validates a one-based task number, returning its zero-based index. */
    public static int parseIndex(String input, String command, int listSize) throws TodException {
        String numberPart = getArguments(input, command);
        boolean isValidNumber = !numberPart.isEmpty();
        for (int i = 0; i < numberPart.length(); i++) {
            if (!Character.isDigit(numberPart.charAt(i))) {
                isValidNumber = false;
            }
        }
        if (!isValidNumber) {
            throw invalidTaskNumber(command);
        }

        int index;
        try {
            index = Integer.parseInt(numberPart) - 1;
        } catch (NumberFormatException e) {
            throw invalidTaskNumber(command);
        }
        if (index < 0 || index >= listSize) {
            throw new TodException("Am I tripping cuz that task number literally doesn't exist!");
        }
        return index;
    }

    /** Parses a todo command and creates the described task. */
    public static Todo parseTodo(String input) throws TodException {
        String description = getArguments(input, "todo");
        if (description.isEmpty()) {
            throw new TodException("Wait you didn't even tell me what is the to-do");
        }
        validateDescription(description);
        return new Todo(description);
    }

    /** Parses a deadline command, including its date or date and time. */
    public static Deadline parseDeadline(String input) throws TodException {
        String arguments = getArguments(input, "deadline");
        if (arguments.isEmpty()) {
            throw new TodException("Wait you didn't even tell me anything about this deadline");
        }

        String[] parts = splitParameter(arguments, "/by");
        String description = parts[0].trim();
        if (description.isEmpty()) {
            throw new TodException("Try Again! The description is literally empty.");
        }
        if (parts.length < 2 || parts[1].trim().isEmpty()) {
            throw new TodException("Try Again! There is no /by date or time.");
        }

        validateDescription(description);
        LocalDateTime by = DateTimeUtil.parseDateTime(parts[1].trim());
        return new Deadline(description, by);
    }

    /** Parses an event command, including its start and end date-times. */
    public static Event parseEvent(String input) throws TodException {
        String arguments = getArguments(input, "event");
        if (arguments.isEmpty()) {
            throw new TodException("Wait you didn't even tell me anything about this event");
        }

        String[] allToParts = splitParameter(arguments, "/to");
        String[] fromSplit = splitParameter(arguments, "/from");
        if (allToParts.length == 2 && splitParameter(fromSplit[0], "/to").length == 2) {
            throw new TodException("Put /from before /to. Type help for the event format.");
        }
        String description = fromSplit[0].trim();
        if (description.isEmpty()) {
            throw new TodException("Try Again! The description is literally empty.");
        }
        if (fromSplit.length < 2 || fromSplit[1].trim().isEmpty()) {
            throw new TodException("Try Again! There is no /from time.");
        }

        String[] toSplit = splitParameter(fromSplit[1], "/to");
        String fromText = toSplit[0].trim();
        if (toSplit.length < 2 || toSplit[1].trim().isEmpty()) {
            throw new TodException("Try Again! There is no /to time.");
        }
        validateDescription(description);
        LocalDateTime from = DateTimeUtil.parseDateTime(fromText);
        LocalDateTime to = DateTimeUtil.parseDateTime(toSplit[1].trim());
        if (!to.isAfter(from)) {
            throw new TodException("The /to date and time must be after /from.");
        }
        return new Event(description, from, to);
    }

    /** Parses the date argument of an on command. */
    public static LocalDate parseDate(String input) throws TodException {
        return DateTimeUtil.parseDate(getArguments(input, "on"));
    }

    /** Parses and validates the keyword of a find command. */
    public static String parseKeyword(String input) throws TodException {
        String keyword = getArguments(input, "find");
        if (keyword.isEmpty()) {
            throw new TodException("Wait you didn't give me a keyword to find.");
        }
        return keyword;
    }

    /** Returns the trimmed part of an input line that follows its command word. */
    private static String getArguments(String input, String command) {
        input = input.trim();
        return input.length() > command.length() ? input.substring(command.length()).trim() : "";
    }

    /** Splits a required parameter, accepting whitespace and rejecting repeated occurrences. */
    private static String[] splitParameter(String arguments, String parameter) throws TodException {
        String[] parts = arguments.split("(?<!\\S)" + parameter + "(?=\\s|$)", -1);
        if (parts.length > 2) {
            throw new TodException("Use " + parameter + " only once. Type help for the command format.");
        }
        return parts;
    }

    /** Prevents descriptions from introducing extra fields or lines in the save file. */
    private static void validateDescription(String description) throws TodException {
        if (description.contains("|") || description.contains("\n") || description.contains("\r")) {
            throw new TodException("Please keep the description on one line and leave out | characters.");
        }
    }

    /** Rejects accidental arguments to commands that do not accept any. */
    public static void validateNoArguments(String input) throws TodException {
        String[] parts = input.trim().split("\\s+", 2);
        if (parts.length > 1) {
            throw new TodException("Use just " + parts[0] + " without extra words or numbers.");
        }
    }

    private static TodException invalidTaskNumber(String command) {
        return new TodException("Brother you didnt give me a valid task number to " + command + ".");
    }
}
