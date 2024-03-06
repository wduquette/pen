package pen.calendars.formatter;

import pen.calendars.*;

import java.util.ArrayList;
import java.util.Collections;
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
public class DateFormat {
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
    public DateFormat(String formatString) {
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
     * Gets the date field definitions for this format.
     * @return The fields
     */
    public List<DateField> fields() {
        return Collections.unmodifiableList(fields);
    }


    /**
     * Formats an epoch day as a date string for the given calendar.
     * @param format The format
     * @param cal The calendar
     * @param day The epoch day
     * @return The string
     */
    public static String format(DateFormat format, Calendar cal, int day) {
        var buff = new StringBuilder();
        var yearDay = cal.day2yearDay(day);
        var date = cal.hasMonths() ? cal.day2date(day) : null;
        var weekday = cal.hasWeeks() ? cal.day2weekday(day) : null;

        for (var field : format.fields()) {
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
     * Parses the given dateString with respect to the given calendar,
     * returning the corresponding epoch day.  Era, month, and weekday
     * names are expected to match the precise form indicated by the
     * format string.  Numeric fields are expected to have the exact
     * number of digits given in the format string, unless that number is
     * 1; in that case, the field will consume available digits.
     * @param format The format
     * @param cal The calendar
     * @param dateString The date string
     * @return The epoch day
     * @throws CalendarException on parse failure
     */
    public static int parse(DateFormat format, Calendar cal, String dateString) {
        if (!format.isCompatibleWith(cal)) {
            throw new IllegalArgumentException(
                "Calendar is not compatible with this DateFormatter.");
        }

        return new DateParser(format, cal, dateString).parse();
    }

    //-------------------------------------------------------------------------
    // DateParser

    // We have a separate class for parsing to hold the transient state.
    private static class DateParser {
        // Input data
        private final DateFormat format;
        private final Calendar cal;
        private final String dateString;

        // Parsed Information
        private boolean isPriorEra = false;
        private Integer year = null;
        private Integer monthOfYear = null;
        private Integer dayOfMonth = null;
        private Integer dayOfYear = null;

        // Parsing Progress
        private final int n;
        private int i = 0;

        DateParser(DateFormat format, Calendar cal, String dateString) {
            this.format = format;
            this.cal = cal;
            this.dateString = dateString.toUpperCase();
            this.n = dateString.length();
        }

        // Parses the string according to the fields, and computes an epoch
        // day if possible.
        int parse() {
            // FIRST, parse available fields
            for (var field : format.fields()) {
                parseField(field);
            }

            // NEXT, See if we have sufficient data to compute the epoch day.
            return computeEpochDay();
        }

        private void parseField(DateField field) {
            if (atEnd()) {
                throw expected(field.getClass().getSimpleName(), "");
            }

            switch (field) {
                case DayOfMonth fld ->
                    dayOfMonth = nextInt("d", fld.digits());
                case DayOfYear fld ->
                    dayOfYear = nextInt("D", fld.digits());
                case EraName fld -> {
                    var era = cal.era().getForm(fld.form()).toUpperCase();
                    var prior = cal.priorEra().getForm(fld.form()).toUpperCase();

                    if (peekString().startsWith(era)) {
                        skipString(era);
                    } else if (peekString().startsWith(prior)) {
                        skipString(prior);
                        isPriorEra = true;
                    } else {
                        throw expected("era", peekString());
                    }
                }
                case MonthName fld ->
                    monthOfYear = findName("month", cal.months(), fld.form()) + 1;
                case MonthNumber fld ->
                    monthOfYear = nextInt("m", fld.digits());
                case Text fld -> {
                    var text = fld.text().toUpperCase();
                    if (peekString().startsWith(text)) {
                        skipString(text);
                    } else {
                        throw expected("\"" + fld.text() + "\"", peekString());
                    }
                }
                case WeekdayName fld ->
                    // We don't care which weekday it is, but we have to
                    // parse it.
                    findName("weekday", cal.week().weekdays(), fld.form());
                case YearNumber fld ->
                    year = nextInt("y", fld.digits());
            }
        }

        private int computeEpochDay() {
            // FIRST, if we don't know the year we're out of luck.
            if (year == null) {
                throw badInfo();
            }

            // NEXT, if they gave us the prior era, negate the year.
            if (isPriorEra) {
                year = -year;
            }

            // NEXT, if we have dayOfYear, that's sufficient.
            if (dayOfYear != null) {
                var yearDay = cal.yearDay(year, dayOfYear);
                cal.validate(yearDay);
                return cal.yearDay2day(yearDay);
            }

            // NEXT, we need monthOfYear and dayOfMonth
            if (monthOfYear != null && dayOfMonth != null) {
                var date = cal.date(year, monthOfYear, dayOfMonth);
                cal.validate(date);
                return cal.date2day(date);
            }

            throw badInfo();
        }

        //---------------------------------------------------------------------
        // Scanner Methods

        private boolean atEnd() {
            return i >= n;
        }

        private int charsLeft() {
            return n - i;
        }

        private int nextInt(String conv, int count) {
            // FIRST, make sure we've got at least the desired number of
            // characters
            if (count > charsLeft()) {
                throw expected("Field \"" + conv.repeat(count) + "\"",
                    peekString());
            }

            // NEXT, if count is 1, take all available digits.
            if (count == 1) {
                count = countLeadingDigits();
            }

            // NEXT, extract the characters and convert to integer.
            var token = dateString.substring(i, i + count);
            i += count;

            try {
                return Integer.parseInt(token);
            } catch (Exception ex) {
                throw expected("Field \"" + conv.repeat(count) + "\"",
                    token);
            }
        }

        private int countLeadingDigits() {
            var count = 0;
            var ndx = i;

            while (!atEnd() && Character.isDigit(dateString.charAt(ndx))) {
                ++ndx;
                ++count;
            }

            return count;
        }

        private int findName(
            String what,
            List<? extends CalendarName> names,
            Form form
        ) {
            for (var ndx = 0; ndx < names.size(); ndx++) {
                var text = names.get(ndx).getForm(form).toUpperCase();

                if (peekString().startsWith(text)) {
                    skipString(text);
                    return ndx;
                }
            }

            throw expected(what, peekString());
        }

        private String peekString() {
            return atEnd() ? "" : dateString.substring(i);
        }

        private void skipString(String token) {
            if (peekString().startsWith(token)) {
                i += token.length();
            } else {
                throw expected("\"" + token + "\"", peekString());
            }
        }

        //---------------------------------------------------------------------
        // Parsing Exceptions

        private CalendarException expected(String what, String got) {
            return new CalendarException("Expected " + what + ", got: \"" +
                got + "\".");
        }

        private CalendarException badInfo() {
            return new CalendarException("Insufficient information to compute the epoch day.");
        }
    }

    //-------------------------------------------------------------------------
    // FormatScanner

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
