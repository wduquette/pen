package pen.calendars;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * A SimpleCalendar is a calendar with some number of months, tied to
 * an epoch day on a Fundamental Calendar.  New Year's Day is the first
 * day of the first month.  A SimpleCalendar can be modified by a
 * RegnalCalendar.
 */
public class SimpleCalendar implements Calendar {
    //-------------------------------------------------------------------------
    // Instance Variables

    // The fundamental calendar day corresponding to 1/1/1 in this
    // calendar.
    private final int epochDay;


    // The era symbol for positive years.
    private final String era;

    // The era symbol for negative years
    private final String priorEra;

    // The month definitions
    private final List<MonthRecord> months;

    // The weekly cycle; possibly null
    private final Week week;

    //-------------------------------------------------------------------------
    // Constructor

    // Creates the calendar given the builder parameters.
    private SimpleCalendar(Builder builder) {
        this.epochDay  = builder.epochDay;
        this.era       = Objects.requireNonNull(builder.era);
        this.priorEra  = Objects.requireNonNull(builder.priorEra);
        this.months    = Collections.unmodifiableList(builder.months);
        this.week      = builder.week;
    }

    //-------------------------------------------------------------------------
    // SimpleCalendar Getters (other than Calendar API getters)

    public int epochDay() {
        return epochDay;
    }

    public String era() {
        return era;
    }

    public String priorEra() {
        return priorEra;
    }

    public List<Month> months() {
        return months.stream().map(MonthRecord::month).toList();
    }

    //-------------------------------------------------------------------------
    // Calendar API

    @Override
    public String formatDate(int day) {
        return date2string(day2date(day));
    }

    @Override
    public int parseDate(String dateString) {
        return date2day(string2date(dateString));
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
    // SimpleCalendar Methods

    public Month month(int monthOfYear) {
        return months.get(monthOfYear - 1).month;
    }

    /**
     * Gets the number of days in the given calendar year.
     * @param year The year, according to this calendar
     * @return The number of days in that year.
     */
    public int daysInYear(int year) {
        if (year > 0) {
            return months.stream()
                .mapToInt(m -> m.daysInMonth().apply(year))
                .sum();
        } else if (year < 0) {
            return months.stream()
                .mapToInt(m -> m.daysInMonth().apply(year + 1))
                .sum();
        } else {
            throw new CalendarException("Year cannot be 0.");
        }
    }

    public int daysInMonth(int year, int monthOfYear) {
        if (year > 0) {
            return months.get(monthOfYear - 1).daysInMonth().apply(year);
        } else if (year < 0) {
            return months.get(monthOfYear - 1).daysInMonth().apply(year + 1);
        } else {
            throw new CalendarException("Year cannot be 0.");
        }
    }

    public Date date(int year, int month, int day) {
        var date = new Date(this, year, month, day);
        validate(date);
        return date;
    }

    public void validate(Date date) {
        if (date.year() == 0) {
            throw new CalendarException("Year is 0!");
        }

        if (date.monthOfYear() < 1 || date.monthOfYear() > months.size()) {
            throw new CalendarException(
                "Month is out of range (1,...," + months.size() + ")");
        }

        var daysInMonth = daysInMonth(date.year(), date.monthOfYear());
        if (date.dayOfMonth() < 1 || date.dayOfMonth() > daysInMonth) {
            throw new CalendarException(
                "Day is out of range (1,...," + daysInMonth + ")");
        }
    }

    /**
     * Converts an arbitrary day since the fundamental epoch to a date.
     * @param fundamentalDay The fundamental day
     * @return The date
     */
    public Date day2date(int fundamentalDay) {
        var day = fundamentalDay - epochDay;
        int year;
        int dayOfYear;

        if (day >= 0) {
            // FIRST, get the year and day of year
            year = 1;

            var daysInYear = daysInYear(year);
            while (day >= daysInYear) {
                day -= daysInYear;
                year++;
                daysInYear = daysInYear(year);
            }

            // NEXT, get the month and day of month
            return yearDay2date(year, day + 1);
        } else {
            // FIRST, get the year and day of year
            year = -1;
            day = -day;

            var daysInYear = daysInYear(year);
            while (day > daysInYear) {
                day -= daysInYear;
                year--;
                daysInYear = daysInYear(year);
            }

            dayOfYear = daysInYear - day + 1;

            // NEXT, get the month and day of month
            return yearDay2date(year, dayOfYear);
        }
    }

    // Given a year and a dayOfYear 1 to N, get the date
    private Date yearDay2date(int year, int dayOfYear) {
        var monthOfYear = 0;
        var dayOfMonth = 0;

        for (int i = 1; i <= months.size(); i++) {
            var daysInMonth = daysInMonth(year, i);

            if (dayOfYear <= daysInMonth) {
                dayOfMonth = dayOfYear;
                monthOfYear = i;
                break;
            }

            dayOfYear -= daysInMonth;
        }

        return new Date(this, year, monthOfYear, dayOfMonth);
    }

    public int date2day(Date date) {
        var year = date.year();

        // FIRST, days in this month
        var day = date.dayOfMonth() - 1;

        // NEXT, days in earlier months
        for (int m = 1; m <= date.monthOfYear() - 1; m++) {
            day += daysInMonth(year, m);
        }

        // NEXT, days in years since era start.
        if (year > 0) {
            for (int y = 1; y < year; y++) {
                day += daysInYear(y);
            }
        } else {
            for (int y = -1; y >= year; y--) {
                day -= daysInYear(y);
            }
        }

        return day + epochDay;
    }

    /**
     * Returns the string "{year}-{monthOfYear}-{dayOfMonth} {era}" for
     * positive years and "{-year}-{monthOfYear}-{dayOfMonty} {priorEra}"
     * for negative years.
     * @param date The date
     * @return The formatted string
     */
    public String date2string(Date date) {
        validate(date);

        var sym = (date.year() >= 0) ? era : priorEra;
        var year = Math.abs(date.year());

        return year + "-" + date.monthOfYear() + "-" + date.dayOfMonth()
            + "-" + sym;
    }

    Date string2date(String dateString) {
        var tokens = dateString.trim().split("-");

        if (tokens.length != 4) {
            throw badFormat(dateString);
        }

        var sym = tokens[3].toUpperCase();

        if (!sym.equals(era) && !sym.equals(priorEra)) {
            throw badFormat(dateString);
        }

        try {
            var year = Integer.parseInt(tokens[0]);

            var date = new Date(
                this,
                sym.equals(era) ? year : -year,
                Integer.parseInt(tokens[1]),
                Integer.parseInt(tokens[2]));
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

        SimpleCalendar that = (SimpleCalendar) o;

        if (epochDay != that.epochDay) return false;
        if (!era.equals(that.era)) return false;
        if (!priorEra.equals(that.priorEra)) return false;
        if (!months.equals(that.months)) return false;
        return Objects.equals(week, that.week);
    }

    @Override
    public int hashCode() {
        int result = epochDay;
        result = 31 * result + era.hashCode();
        result = 31 * result + priorEra.hashCode();
        result = 31 * result + months.hashCode();
        result = 31 * result + (week != null ? week.hashCode() : 0);
        return result;
    }


    //-------------------------------------------------------------------------
    // Helpers

    private CalendarException badFormat(String dateString) {
        throw new CalendarException(
            "Invalid format, expected \"" +
                "<year>-<monthOfYear>-<dayOfMonth>-" + era + "|" + priorEra +
                "\", got \"" + dateString + "\".");
    }

    public String toString() {
        return "SimpleCalendar[" + era + "," + priorEra + "," + months.size()
            + "]";
    }

    //-------------------------------------------------------------------------
    // Helper Classes

    /**
     * Defines a month in terms of an external object (an enum or a string)
     * and the length of the month given the fundamental year.
     * @param month The month
     * @param daysInMonth The month length function
     */
    public record MonthRecord(
        Month month,
        YearDelta daysInMonth
    ) {
        // Nothing to do yet
    }

    //-------------------------------------------------------------------------
    // Builder

    public static class Builder {
        //---------------------------------------------------------------------
        // Instance Data

        private int epochDay = 0;
        private String era = "AE";
        private String priorEra = "BE";
        private final List<MonthRecord> months = new ArrayList<>();
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
        public SimpleCalendar build() {
            return new SimpleCalendar(this);
        }

        /**
         * Sets the fundamental day corresponding to year 1, month 1, day 1.
         * @param day The epoch day
         * @return The builder
         */
        public Builder epochDay(int day) {
            this.epochDay = day;
            return this;
        }

        /**
         * Sets the era string for this calendar.  Defaults to "AE",
         * "After Epoch".
         * @param era The era string.
         * @return the builder
         */
        public Builder era(String era) {
            this.era = Objects.requireNonNull(era).toUpperCase();
            return this;
        }

        /**
         * Sets the prior era string for this calendar.  Defaults to "BE",
         * "Before Epoch".
         * @param priorEra The era string.
         * @return the builder
         */
        public Builder priorEra(String priorEra) {
            this.priorEra = Objects.requireNonNull(priorEra).toUpperCase();
            return this;
        }

        /**
         * Adds a month of the given length to the calendar
         * @param month The month
         * @param length The length
         * @return The builder
         */
        public Builder month(Month month, int length) {
            Objects.requireNonNull(month, "month is  null!");
            months.add(new MonthRecord(month, y -> length));
            return this;
        }

        /**
         * Adds a month with the given length function to the calendar
         * @param month The month
         * @param length The length function
         * @return The builder
         */
        public Builder month(Month month, YearDelta length) {
            Objects.requireNonNull(month, "month is  null!");
            Objects.requireNonNull(length, "month length function is  null!");
            months.add(new MonthRecord(month, length));
            return this;
        }

        /**
         * Sets the weekly cycle.
         * @param week The cycle
         * @return The builder
         */
        public Builder week(Week week) {
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
        public Builder week(
            List<Weekday> weekdays,
            int offset
        ) {
            return week(new Week(weekdays, offset));
        }
    }
}
