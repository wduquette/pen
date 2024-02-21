package pen.calendars.formatter;

import pen.calendars.CalendarException;
import pen.calendars.Date;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static pen.calendars.formatter.DateComponent.*;

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
 * e.g., "'Year: 'yyyy 'Day of year: ' DDD"</p>.
 */
public class DateFormatter {
    //-------------------------------------------------------------------------
    // Static Factories

    public static final DateFormatter define(String formatString) {
        return new DateFormatter(formatString);
    }

    //-------------------------------------------------------------------------
    // Instance Variables

    // The compiled list of components
    private final List<DateComponent> components = new ArrayList<>();

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

    public DateFormatter(String formatString) {
        var scanner = new Scanner(formatString);

        while (!scanner.atEnd()) {
            switch (scanner.peek()) {
                case QUOTE -> components.add(new Text(scanner.getText()));
                case DAY_OF_MONTH ->
                    components.add(new DayOfMonth(scanner.getCount()));
                case DAY_OF_YEAR ->
                    components.add(new DayOfYear(scanner.getCount()));
                case ERA ->
                    components.add(new Era(count2form(scanner.getCount())));
                case MONTH_NAME ->
                    components.add(new MonthName(count2form(scanner.getCount())));
                case MONTH ->
                    components.add(new MonthNumber(scanner.getCount()));
                case WEEKDAY ->
                    components.add(new Weekday(count2form(scanner.getCount())));
                case YEAR ->
                    components.add(new YearNumber(scanner.getCount()));
                default ->
                    throw new CalendarException("Unknown conversion character: " +
                        "\"" + scanner.peek() + "\".");
            }
        }
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

    @SuppressWarnings("unused")
    public void dump() {
        components.forEach(System.out::println);
    }

    public String format(Date date) {
        return components.stream()
            .map(c -> c.format(date))
            .collect(Collectors.joining());
    }

    //-----------------------------------------------------------------------------------------------------------------
    // Scanner

    private static class Scanner {
        private final String source;
        private int i = 0;
        private final int n;

        public Scanner(String source) {
            this.source = source;
            this.n = source.length();
        }

        public boolean atEnd() {
            return i >= n;
        }

        public char peek() {
            return source.charAt(i);
        }

        public void next() {
            ++i;
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
