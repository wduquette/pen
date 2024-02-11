package pen.calendars;

import java.util.ArrayList;
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
    // Instance variables

    // The fundamental day corresponding with year 1, month 1, day 1
    private final int epochDay;

    // The era string for years >= 1
    private final String era;

    // The era string for years <= -1
    private final String priorEra;

    // The month definitions
    private final List<MonthRecord> months;

    //-------------------------------------------------------------------------
    // Constructor

    private SimpleCalendar(Builder builder) {
        this.epochDay = builder.epochDay;
        this.era = builder.era;
        this.priorEra = builder.priorEra;
        this.months = builder.months;
    }

    //-------------------------------------------------------------------------
    // Calendar API

    @Override
    public String formatDate(int day) {
        return null;
    }

    @Override
    public int parseDate(String dateString) {
        return 0;
    }

    //-------------------------------------------------------------------------
    // SimpleCalendar Methods

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

    public YearMonthDay date(int year, int month, int day) {
        var date = new YearMonthDay(this, year, month, day);
        validate(date);
        return date;
    }

    public void validate(YearMonthDay date) {
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
    public YearMonthDay day2date(int fundamentalDay) {
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
    private YearMonthDay yearDay2date(int year, int dayOfYear) {
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

        return new YearMonthDay(this, year, monthOfYear, dayOfMonth);
    }

    public int date2day(YearMonthDay date) {
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
            this.era = Objects.requireNonNull(era);
            return this;
        }

        /**
         * Sets the prior era string for this calendar.  Defaults to "BE",
         * "Before Epoch".
         * @param priorEra The era string.
         * @return the builder
         */
        public Builder priorEra(String priorEra) {
            this.priorEra = Objects.requireNonNull(priorEra);
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
    }
}
