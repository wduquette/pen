package pen.calendars;

import java.util.List;
import java.util.Objects;

/**
 * Defines a {@link Calendar} with no notion of months, just days and years
 * since the epoch.  It may optionally have a {@link Week}.
 *
 * <h2>Uses</h2>
 *
 * <p>This calendar is defined for two use cases:</p>
 *
 * <ul>
 * <li>Defining the epoch for a family of calendars.</li>
 * <li>Defining time scales when precise dates are not needed.</li>
 * </ul>
 *
 * <h2>Leap Years</h2>
 *
 * <p>The year may vary in length to support leap years and similar constructs.
 * The length of the year in days is determined by the {@code yearLength}
 * function, which returns a number of days given a year number.</p>
 *
 * <p>Years prior to the epoch are usually counted from -1; for mathematical
 * convenience, the {@code yearLength} function assumes prior years count
 * down from 0.  The {@code TrivialCalendar::daysInYear} function takes this
 * into account and works with standard calendar years, where there is
 * no year 0.</p>
 */
@SuppressWarnings("unused")
public class TrivialCalendar implements Calendar {
    //-------------------------------------------------------------------------
    // Instance Variables

    // The era symbol for positive years
    private final String era;

    // The era symbol for negative years
    private final String priorEra;

    // A function for computing the length of the year in days given the
    // year number
    private final YearDelta yearLength;

    // The number of digits for years, when formatting.
    // TODO Remove when DateFormatter is available.
    private final int dayOfYearDigits;

    // The weekly cycle; possibly null
    private final Week week;

    //-------------------------------------------------------------------------
    // Constructor

    // Creates the calendar given the builder parameters.
    private TrivialCalendar(Builder builder) {
        this.era             = Objects.requireNonNull(builder.era);
        this.priorEra        = Objects.requireNonNull(builder.priorEra);
        this.yearLength      = Objects.requireNonNull(builder.yearLength);

        this.dayOfYearDigits = builder.dayOfYearDigits;
        this.week            = builder.week;
    }

    //-------------------------------------------------------------------------
    // TrivialCalendar Methods, aside from the Calendar API

    /**
     * Gets the function used to compute the length of the year in days for
     * any given year.  Clients should prefer the {@code daysInYear(int)} method,
     * as the {@code yearLength} function assumes there is a year 0 and so
     * mishandles priorEra years.
     * @return The function
     */
    public YearDelta yearLength() {
        return yearLength;
    }

    //-------------------------------------------------------------------------
    // Calendar API: Basic Features, common to all implementations

    @Override
    public int daysInYear(int year) {
        if (year > 0) {
            return yearLength.apply(year);
        } else if (year < 0) {
            return yearLength.apply(year + 1);
        } else {
            throw new CalendarException("Year 0 is undefined.");
        }
    }


    // TODO: Replace with an Era object
    @Override
    public String era() {
        return era;
    }

    // TODO: Replace with an Era object
    @Override
    public String priorEra() {
        return priorEra;
    }

    /**
     * Returns the string "{era}{year}-{dayOfYear} for positive years and
     * "{priorEra}{-year}/{dayOfYear}" for negative years.
     * @param day The epoch day
     * @return The formatted string
     */
    @Override
    public String formatDate(int day) {
        return yearDay2string(day2yearDay(day));
    }

    /**
     * Parses a date string into an epoch day.
     * @param dateString the date string
     * @return The day
     * @throws CalendarException on parse error
     */
    @Override
    public int parseDate(String dateString) {
        return yearDay2day(string2yearDay(dateString));
    }

    //-------------------------------------------------------------------------
    // Weeks API

    @Override
    public boolean hasWeeks() {
        return week != null;
    }

    @Override
    public Week week() {
        return week;
    }

    //-------------------------------------------------------------------------
    // TrivialCalendar conversions


    /**
     * Returns the string "{era}{year}-{dayOfYear}" for positive years and
     * "{priorEra}{-year}-{dayOfYear}" for negative years.
     * @param date The date
     * @return The formatted string
     */
    public String yearDay2string(YearDay date) {
        validate(date);

        var sym = (date.year() >= 0) ? era : priorEra;
        var year = Math.abs(date.year());
        var dayOfYear = String.format("%0" + dayOfYearDigits + "d",
            date.dayOfYear());

        return sym + year + "-" + dayOfYear;
    }

    /**
     * Parses a date string into a date
     * @param dateString the date string
     * @return The date
     * @throws CalendarException on parse error
     */
    public YearDay string2yearDay(String dateString) {
        dateString = dateString.trim().toUpperCase();

        // FIRST, get the symbol
        String sym;
        boolean isBefore = false;

        if (dateString.startsWith(era.toUpperCase())) {
            sym = era;
        } else if (dateString.startsWith(priorEra.toUpperCase())) {
            sym = priorEra;
            isBefore = true;
        } else {
            throw badFormat(dateString);
        }

        // NEXT, split on "-"
        var tokens = dateString.substring(sym.length()).split("-");

        if (tokens.length != 2) {
            throw badFormat(dateString);
        }

        try {
            var year = Integer.parseInt(tokens[0]);
            var dayOfYear = Integer.parseInt(tokens[1]);

            var date = new YearDay(
                this,
                isBefore ? -year : year,
                dayOfYear);

            validate(date);
            return date;
        } catch (IllegalArgumentException ex) {
            throw badFormat(dateString);
        }
    }


    //-------------------------------------------------------------------------
    // Object API

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TrivialCalendar that = (TrivialCalendar) o;

        if (!era.equals(that.era)) return false;
        if (!priorEra.equals(that.priorEra)) return false;
        if (!yearLength.equals(that.yearLength)) return false;
        return Objects.equals(week, that.week);
    }

    @Override
    public int hashCode() {
        int result = era.hashCode();
        result = 31 * result + priorEra.hashCode();
        result = 31 * result + yearLength.hashCode();
        result = 31 * result + (week != null ? week.hashCode() : 0);
        return result;
    }


    //-------------------------------------------------------------------------
    // Builder

    public static class Builder {
        //---------------------------------------------------------------------
        // Instance Data

        private String era = "AE";
        private String priorEra = "BE";
        private YearDelta yearLength = (y -> 365);
        private int dayOfYearDigits = 1;
        private Week week = null;

        //---------------------------------------------------------------------
        // Constructor

        public Builder() {} // nothing to do

        //---------------------------------------------------------------------
        // Methods

        /**
         * Builds the calendar given the inputs.
         * @return The calendar
         */
        public TrivialCalendar build() {
            return new TrivialCalendar(this);
        }

        /**
         * Sets the era string for this calendar.  Defaults to "FE",
         * "Trivial Epoch".
         * @param era The era string.
         * @return the builder
         */
        public TrivialCalendar.Builder era(String era) {
            this.era = Objects.requireNonNull(era).toUpperCase();
            return this;
        }

        /**
         * Sets the prior era string for this calendar.  Defaults to "BFE",
         * "Before Trivial Epoch".
         * @param priorEra The era string.
         * @return the builder
         */
        public TrivialCalendar.Builder priorEra(String priorEra) {
            this.priorEra = Objects.requireNonNull(priorEra).toUpperCase();
            return this;
        }

        /**
         * Sets the year length to the given function.
         * @param function The function
         * @return The builder
         */
        public TrivialCalendar.Builder yearLength(YearDelta function) {
            this.yearLength = function;
            return this;
        }

        /**
         * Sets the year length to a fixed quantity
         * @param length The length
         * @return The builder
         */
        public TrivialCalendar.Builder yearLength(int length) {
            this.yearLength = (dummy -> length);
            return this;
        }

        /**
         * Sets the number of digits when formatting the day of the year
         * @param digits The number
         * @return The builder
         */
        public TrivialCalendar.Builder dayOfYearDigits(int digits) {
            this.dayOfYearDigits = digits;
            return this;
        }

        /**
         * Sets the weekly cycle.
         * @param week The cycle
         * @return The builder
         */
        public TrivialCalendar.Builder week(Week week) {
            this.week = week;
            return this;
        }

        /**
         * Sets the weekly cycle given a list of weekdays and an offset from
         * day 0.
         * @param weekdays the weekdays
         * @param offset The offset
         * @return The builder
         */
        public TrivialCalendar.Builder week(
            List<Weekday> weekdays,
            int offset
        ) {
            return week(new Week(weekdays, offset));
        }
    }

    //-------------------------------------------------------------------------
    // Helpers

    private CalendarException badFormat(String dateString) {
        throw new CalendarException(
            "Invalid format, expected \"" +
                era + "|" + priorEra + "<year>-<dayOfYear>\": \"" +
            dateString + "\".");
    }

    public String toString() {
        return "TrivialCalendar[" + era + "," + priorEra + "]";
    }
}
