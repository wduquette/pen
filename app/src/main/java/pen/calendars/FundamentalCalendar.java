package pen.calendars;

import java.util.List;
import java.util.Objects;

/**
 * Defines a fundamental calendar.  The name is typically a short symbol,
 * e.g., "FC".  The {@code yearLength} is a function that determines the
 * length of a year in days given the year number.  The epoch is year 1; it
 * is preceded by year -1.  The days of the year are
 */
public class FundamentalCalendar implements Calendar {
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
    private FundamentalCalendar(Builder builder) {
        this.era             = Objects.requireNonNull(builder.era);
        this.priorEra        = Objects.requireNonNull(builder.priorEra);
        this.yearLength      = Objects.requireNonNull(builder.yearLength);

        this.dayOfYearDigits = builder.dayOfYearDigits;
        this.week            = builder.week;
    }

    //-------------------------------------------------------------------------
    // FundamentalCalendar Getters (other than Calendar API getters)

    public String era() {
        return era;
    }

    public String priorEra() {
        return priorEra;
    }

    public YearDelta yearLength() {
        return yearLength;
    }

    //-------------------------------------------------------------------------
    // Calendar API

    public Month month(int monthOfYear) {
        throw new UnsupportedOperationException(
            "Calendar lacks a monthly cycle.");
    }

    /**
     * Returns the string "{era}{year}-{dayOfYear} for positive years and
     * "{priorEra}{-year}/{dayOfYear}" for negative years.
     * @param day The fundamental day
     * @return The formatted string
     */
    @Override
    public String formatDate(int day) {
        return yearDayOfYear2string(day2yearDayOfYear(day));
    }

    /**
     * Parses a date string into a fundamental day.
     * @param dateString the date string
     * @return The day
     * @throws CalendarException on parse error
     */
    @Override
    public int parseDate(String dateString) {
        return yearDayOfYear2day(string2yearDayOfYear(dateString));
    }

    @Override
    public boolean hasWeeks() {
        return week != null;
    }

    @Override
    public Weekday day2weekday(int day) {
        if (week != null) {
            return week.day2weekday(day);
        } else {
            throw new UnsupportedOperationException(
                "Calendar lacks a weekly cycle.");
        }
    }

    @Override
    public Week week() {
        return week;
    }

    //-------------------------------------------------------------------------
    // FundamentalCalendar conversions

    /**
     * Returns the number of days in the given year.
     * @param year The year
     * @return The number of days
     */
    public int daysInYear(int year) {
        if (year > 0) {
            return yearLength.apply(year);
        } else if (year < 0) {
            return yearLength.apply(year + 1);
        } else {
             throw new IllegalArgumentException("year is 0");
        }
    }

    /**
     * Converts an arbitrary day since the epoch to a date.
     * @param day The day
     * @return The date
     */
    public YearDay day2yearDayOfYear(int day) {
        if (day >= 0) {
            int year = 1;
            var daysInYear = daysInYear(year);

            while (day >= daysInYear) {
                day -= daysInYear;
                year++;
                daysInYear = daysInYear(year);
            }

            return new YearDay(this, year, day + 1);
        } else {
            int year = -1;
            day = -day;

            var daysInYear = daysInYear(year);

            while (day > daysInYear) {
                day -= daysInYear;
                year--;
                daysInYear = daysInYear(year);
            }

            var dayOfYear = daysInYear - day + 1;
            return new YearDay(this, year, dayOfYear);
        }
    }

    /**
     * Converts an arbitrary date to the day since the epoch.
     * @param date The date
     * @return The day
     * @throws CalendarException if the date is invalid.
     */
    public int yearDayOfYear2day(YearDay date) {
        // FIRST, validate the dayOfYear.
        validate(date);

        // NEXT, positive years, then negative years
        if (date.year() > 0) {
            var day = date.dayOfYear() - 1;
            var year = date.year() - 1;

            while (year >= 1) {
                day += daysInYear(year);
                year--;
            }

            return day;
        } else {
            var day = daysInYear(date.year()) - date.dayOfYear() + 1;
            var year = date.year() + 1;

            while (year < 0) {
                day += daysInYear(year);
                year++;
            }

            return -day;
        }
    }

    /**
     * Returns the string "{era}{year}-{dayOfYear}" for positive years and
     * "{priorEra}{-year}-{dayOfYear}" for negative years.
     * @param date The date
     * @return The formatted string
     */
    public String yearDayOfYear2string(YearDay date) {
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
    public YearDay string2yearDayOfYear(String dateString) {
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

    /**
     * Validates that the date is a valid date.
     * @param date The date
     * @throws CalendarException if the date is invalid.
     */
    public void validate(YearDay date) {
        if (!date.calendar().equals(this)) {
            throw new CalendarException(
                "Calendar mismatch, expected \"" + this + "\", got \"" +
                date.calendar() + "\"");
        }
        if (date.year() == 0) {
            throw new CalendarException("year is 0 in date: \"" + date + "\".");
        }

        if (date.dayOfYear() < 1 ||
            date.dayOfYear() > daysInYear(date.year()))
        {
            throw new CalendarException("dayOfYear out of range for year " +
                date.year() + " in date: \"" + date + "\"");
        }
    }

    //-------------------------------------------------------------------------
    // Object API

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FundamentalCalendar that = (FundamentalCalendar) o;

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
        public FundamentalCalendar build() {
            return new FundamentalCalendar(this);
        }

        /**
         * Sets the era string for this calendar.  Defaults to "FE",
         * "Fundamental Epoch".
         * @param era The era string.
         * @return the builder
         */
        public FundamentalCalendar.Builder era(String era) {
            this.era = Objects.requireNonNull(era).toUpperCase();
            return this;
        }

        /**
         * Sets the prior era string for this calendar.  Defaults to "BFE",
         * "Before Fundamental Epoch".
         * @param priorEra The era string.
         * @return the builder
         */
        public FundamentalCalendar.Builder priorEra(String priorEra) {
            this.priorEra = Objects.requireNonNull(priorEra).toUpperCase();
            return this;
        }

        /**
         * Sets the year length to the given function.
         * @param function The function
         * @return The builder
         */
        public FundamentalCalendar.Builder yearLength(YearDelta function) {
            this.yearLength = function;
            return this;
        }

        /**
         * Sets the year length to a fixed quantity
         * @param length The length
         * @return The builder
         */
        public FundamentalCalendar.Builder yearLength(int length) {
            this.yearLength = (dummy -> length);
            return this;
        }

        /**
         * Sets the number of digits when formatting the day of the year
         * @param digits The number
         * @return The builder
         */
        public FundamentalCalendar.Builder dayOfYearDigits(int digits) {
            this.dayOfYearDigits = digits;
            return this;
        }

        /**
         * Sets the weekly cycle.
         * @param week The cycle
         * @return The builder
         */
        public FundamentalCalendar.Builder week(Week week) {
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
        public FundamentalCalendar.Builder week(
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
        return "FundamentalCalendar[" + era + "," + priorEra + "]";
    }
}
