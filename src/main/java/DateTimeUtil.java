import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Locale;

/** Parses user-entered dates and formats them for Todd's responses. */
public final class DateTimeUtil {
    private static final DateTimeFormatter INPUT_DATE = DateTimeFormatter
            .ofPattern("uuuu-MM-dd")
            .withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter INPUT_DATE_TIME = DateTimeFormatter
            .ofPattern("uuuu-MM-dd HHmm")
            .withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter DISPLAY_DATE = DateTimeFormatter
            .ofPattern("MMM dd uuuu", Locale.ENGLISH);
    private static final DateTimeFormatter DISPLAY_DATE_TIME = DateTimeFormatter
            .ofPattern("MMM dd uuuu, h:mma", Locale.ENGLISH);

    private DateTimeUtil() {
    }

    /**
     * Parses either a date or a date with a 24-hour time.
     * A date without a time is represented as midnight on that date.
     *
     * @param input date text in yyyy-MM-dd or yyyy-MM-dd HHmm format
     * @return the parsed date and time
     * @throws TodException if the input is not a real date in a supported format
     */
    public static LocalDateTime parseDateTime(String input) throws TodException {
        try {
            return LocalDate.parse(input, INPUT_DATE).atStartOfDay();
        } catch (DateTimeParseException dateException) {
            try {
                return LocalDateTime.parse(input, INPUT_DATE_TIME);
            } catch (DateTimeParseException dateTimeException) {
                throw new TodException("Please use a valid date in yyyy-MM-dd or yyyy-MM-dd HHmm format.");
            }
        }
    }

    /**
     * Parses the date used by commands that search for tasks on a specific day.
     *
     * @param input date text in yyyy-MM-dd format
     * @return the parsed date
     * @throws TodException if the input is not a real date in the required format
     */
    public static LocalDate parseDate(String input) throws TodException {
        try {
            return LocalDate.parse(input, INPUT_DATE);
        } catch (DateTimeParseException e) {
            throw new TodException("Please use a valid date in yyyy-MM-dd format.");
        }
    }

    /** Formats a stored date and omits the time when it is midnight. */
    public static String format(LocalDateTime dateTime) {
        if (dateTime.toLocalTime().equals(LocalTime.MIDNIGHT)) {
            return dateTime.format(DISPLAY_DATE);
        }
        return dateTime.format(DISPLAY_DATE_TIME);
    }

    /** Formats a date without a time for a user-facing response. */
    public static String format(LocalDate date) {
        return date.format(DISPLAY_DATE);
    }
}
