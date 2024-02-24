package pen.calendars.formatter;

import pen.calendars.*;

import java.util.ArrayList;
import java.util.List;

import static pen.calendars.formatter.DateField.*;

/**
 * DateFormatters format and parse dates according to a format string.
 * The syntax is similar but not identical to the
 * {@link java.time.format.DateTimeFormatter} syntax.
 *
 * <h2>Numeric Conversions</h2>
 *
 * <table>
 *     <caption></caption>
 *     <tr><th>Conversion</th> <th>Meaning</th></tr>
 *     <tr><td>{@code d}</td> <td>Day of month</td></tr>
 *     <tr><td>{@code D}</td> <td>Day of year</td></tr>
 *     <tr><td>{@code m}</td> <td>Month of year</td></tr>
 *     <tr><td>{@code y}</td> <td>Year of era</td></tr>
 * </table>
 *
 * <p>For numeric components, the number of characters determines with
 * minimum width of the field, e.g., "yyyy" indicates a four-digit year. Values
 * with three or fewer digits will be formatted with leading zeros.  Values with
 * more digits will be formatted at their full width.</p>
 *
 * <p>The "year of era" is formatted as a positive number.  If both positive and
 * negative years will be in use, be sure to include the era ("E") conversion as
 * well.</p>
 *
 * <h2>Name Conversions</h2>
 *
 * <table>
 *     <tr><th>Conversion</th> <th>Meaning</th></tr>
 *     <tr><td>{@code E}</td> <td>The era or prior era name</td></tr>
 *     <tr><td>{@code M}</td> <td>The month name</td></tr>
 *     <tr><td>{@code W}</td> <td>The weekday name</td></tr>
 * </table>
 *
 * <p>For name conversions, the number of conversion characters indicates the
 * form of the name to use.</p>
 *
 * <table>
 *     <tr><th>{@link Form}</th> <th>Number of Characters</th></tr>
 *     <tr><td>{@code FULL}</td> <td>4 or more</td></tr>
 *     <tr><td>{@code SHORT}</td> <td>3</td></tr>
 *     <tr><td>{@code UNAMBIGUOUS}</td> <td>2</td></tr>
 *     <tr><td>{@code TINY}</td> <td>1</td></tr>
 * </table>
 *
 * <h2>String Literals</h2>
 *
 * <p>The format string can contain literal strings encloses in single quotes,
 * e.g., "'Year:' yyyy 'Day of year:' DDD"</p>.  Space characters, hyphens, and
 * slashes (" ", "-", and "/") are retained as is.
 */
public class DateFormatter {
    //-------------------------------------------------------------------------
    // Instance Variables

    // The compiled list of components
    private final List<DateField> fields = new ArrayList<>();

    // Whether months and/or weeks are required by this format string.
    private final boolean needsWeeks;
    private final boolean needsMonths;

    //-------------------------------------------------------------------------
    // Constructor

    private static final char DAY_OF_MONTH = 'd';
    private static final char DAY_OF_YEAR = 'D';
    private static final char ERA = 'E';
    private static final char MONTH_NAME = 'M';
    private static final char MONTH = 'm';
    private static final char WEEKDAY = 'W';
    private static final char YEAR = 'y';
    private static final char QUOTE = '\'';
    private static final char SPACE = ' ';
    private static final char HYPHEN = '-';
    private static final char SLASH = '/';

    /**
     * Creates a date formatter for the given format string.
     * @param formatString The format string
     */
    public DateFormatter(String formatString) {
        var scanner = new FormatScanner(formatString);
        var hasMonths = false;
        var hasWeeks = false;

        while (!scanner.atEnd()) {
            switch (scanner.peek()) {
                case QUOTE ->
                    fields.add(new Text(scanner.getText()));
                case SPACE, HYPHEN, SLASH ->
                    fields.add(new Text(Character.toString(scanner.next())));
                case DAY_OF_MONTH -> {
                    hasMonths = true;
                    fields.add(new DayOfMonth(scanner.getCount()));
                }
                case DAY_OF_YEAR ->
                    fields.add(new DayOfYear(scanner.getCount()));
                case ERA ->
                    fields.add(new EraName(count2form(scanner.getCount())));
                case MONTH_NAME -> {
                    hasMonths = true;
                    fields.add(new MonthName(count2form(scanner.getCount())));
                }
                case MONTH -> {
                    hasMonths = true;
                    fields.add(new MonthNumber(scanner.getCount()));
                }
                case WEEKDAY -> {
                    hasWeeks = true;
                    fields.add(new WeekdayName(count2form(scanner.getCount())));
                }
                case YEAR ->
                    fields.add(new YearNumber(scanner.getCount()));
                default ->
                    throw new CalendarException("Unknown conversion character: " +
                        "\"" + scanner.peek() + "\".");
            }
        }

        this.needsMonths = hasMonths;
        this.needsWeeks = hasWeeks;
    }

    private Form count2form(int count) {
        return switch (count) {
            case 1 -> Form.TINY;
            case 2 -> Form.UNAMBIGUOUS;
            case 3 -> Form.SHORT;
            default -> Form.FULL;
        };
    }

    //-------------------------------------------------------------------------
    // Public Methods

    /**
     * Gets whether this formatter is compatible with the given calendar.
     * @param calendar The calendar
     * @return true or false
     */
    public boolean isCompatibleWith(Calendar calendar) {
        return (!needsMonths || calendar.hasMonths())
            && (!needsWeeks  || calendar.hasWeeks());
    }

    /**
     * Returns true if the formatter requires a calendar that defines a
     * monthly cycle.
     * @return true or false
     */
    public boolean needsMonths() {
        return needsMonths;
    }

    /**
     * Returns true if the formatter requires a calendar that defines a weekly
     * cycle.
     * @return true or false
     */
    public boolean needsWeeks() {
        return needsWeeks;
    }

    /**
     * Formats an epoch day as a date string for the given calendar.
     * @param cal The calendar
     * @param day The epoch day
     * @return The string
     */
    public String format(Calendar cal, int day) {
        var buff = new StringBuilder();
        var yearDay = cal.day2yearDay(day);
        var date = cal.hasMonths() ? cal.day2date(day) : null;
        var weekday = cal.hasWeeks() ? cal.day2weekday(day) : null;

        for (var field : fields) {
            switch (field) {
                case DayOfMonth fld -> {
                    assert date != null;
                    buff.append(zeroPad(date.dayOfMonth(), fld.digits()));
                }
                case DayOfYear fld ->
                    buff.append(zeroPad(yearDay.dayOfYear(), fld.digits()));
                case EraName fld ->
                    buff.append(yearDay.year() > 0
                        ? cal.era().getForm(fld.form())
                        : cal.priorEra().getForm(fld.form()));
                case MonthName fld -> {
                    assert date !=  null;
                    buff.append(date.month().getForm(fld.form()));
                }
                case MonthNumber fld -> {
                    assert date != null;
                    buff.append(zeroPad(date.monthOfYear(), fld.digits()));
                }
                case Text fld ->
                    buff.append(fld.text());
                case WeekdayName fld -> {
                    assert weekday != null;
                    buff.append(weekday.getForm(fld.form()));
                }
                case YearNumber fld ->
                    buff.append(zeroPad(yearDay.year(), fld.digits()));
            }
        }

        return buff.toString();
    }

    private static String zeroPad(int number, int width) {
        return pad(Integer.toString(Math.abs(number)), "0", width);

    }

    private static String pad(String text, String padChar, int width) {
        return text.length() >= width
            ? text
            : padChar.repeat(width - text.length()) + text;
    }

    /**
     * Formats a date for the given calendar as a date string.
     * @param cal The calendar
     * @param date The date
     * @return The date string.
     */
    public String format(Calendar cal, Date date) {
        if (!date.calendar().equals(cal)) {
            throw new CalendarException("Mismatch between Date and Calendar.");
        }
        return format(cal, cal.date2day(date));
    }

    /**
     * Formats a YearDay for the given calendar as a date string.
     * @param cal The calendar
     * @param yearDay The date
     * @return The date string.
     */
    public String format(Calendar cal, YearDay yearDay) {
        if (!yearDay.calendar().equals(cal)) {
            throw new CalendarException("Mismatch between YearDay and Calendar.");
        }
        return format(cal, cal.yearDay2day(yearDay));
    }

    @SuppressWarnings("unused")
    public void dump() {
        fields.forEach(System.out::println);
    }

    //-----------------------------------------------------------------------------------------------------------------
    // Scanner

    private static class FormatScanner {
        private final String source;
        private int i = 0;
        private final int n;

        public FormatScanner(String source) {
            this.source = source;
            this.n = source.length();
        }

        public boolean atEnd() {
            return i >= n;
        }

        public char peek() {
            return source.charAt(i);
        }

        public char next() {
            char ch = peek();
            ++i;
            return ch;
        }

        public int getCount() {
            int count = 0;
            var ch = peek();

            while (!atEnd() && peek() == ch) {
                ++count;
                next();
            }

            return count;
        }

        public String getText() {
            assert peek() == QUOTE;
            int start = i + 1;

            do {
                next();
            } while (!atEnd() && peek() != QUOTE);

            if (atEnd()) {
                throw new CalendarException("Invalid format, missing close quote in \"" + source + "\".");
            }

            var string = source.substring(start, i);
            next();
            return string;
        }
    }
}
