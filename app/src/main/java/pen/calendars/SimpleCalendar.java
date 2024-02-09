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
public class SimpleCalendar<T> implements Calendar {
    //-------------------------------------------------------------------------
    // Instance variables

    private final int epochDay;
    private final String era;
    private final String priorEra;
    private final List<Month<T>> months;

    //-------------------------------------------------------------------------
    // Constructor

    private SimpleCalendar(Builder<T> builder) {
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
        return months.stream()
            .mapToInt(m -> m.daysInMonth().apply(year))
            .sum();
    }

    /**
     * Converts an arbitrary day since the fundamental epoch to a date.
     * @param fundamentalDay The fundamental day
     * @return The date
     */
    public SimpleDate day2date(int fundamentalDay) {
        var day = fundamentalDay - epochDay;
        int year;
        int dayOfYear;

        if (day >= 0) {
            // FIRST, get the year and day of year
            year = 0;

            var daysInYear = daysInYear(year);
            while (day >= daysInYear) {
                day -= daysInYear;
                year++;
                daysInYear = daysInYear(year);
            }

            // NEXT, get the month and day of month
            return yearDay2date(year, day);
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

    private SimpleDate yearDay2date(int year, int dayOfYear) {
        var monthOfYear = 0;
        var dayOfMonth = 0;

        for (int i = 0; i < months.size(); i++) {
            var month = months.get(i);
            var daysInMonth = month.daysInMonth().apply(year);

            if (dayOfYear <= daysInMonth) {
                dayOfMonth = dayOfYear + 1;
                monthOfYear = i + 1;
                break;
            }

            dayOfYear -= daysInMonth;
        }

        return new SimpleDate(year, monthOfYear, dayOfMonth);
    }

    public int date2day(SimpleDate date) {
        var year = date.year();

        // FIRST, days in this month
        var day = date.dayOfMonth() - 1;

        // NEXT, days in earlier months
        for (int m = 0; m < date.monthOfYear() - 1; m++) {
            day += months.get(m).daysInMonth().apply(year);
        }

        // NEXT, days in years since era start.
        if (year > 0) {
            for (int i = 1; i < year; i++) {
                day += daysInYear(year);
            }
        } else {
            for (int i = -1; i > year; i--) {
                day += daysInYear(year - 1);
            }
        }

        return day + epochDay;
    }

    //-------------------------------------------------------------------------
    // Helper Classes

    /**
     * Defines a month in terms of an external object (an enum or a string)
     * and the length of the month given the fundamental year.
     * @param month The month
     * @param daysInMonth The month length function
     * @param <T> The month type
     */
    public record Month<T>(
        T month,
        YearDelta daysInMonth
    ) {
        // Nothing to do yet
    }

    //-------------------------------------------------------------------------
    // Builder

    public static class Builder<T> {
        //---------------------------------------------------------------------
        // Instance Data

        private int epochDay = 0;
        private String era = "CE";
        private String priorEra = null;
        private final List<Month<T>> months = new ArrayList<>();

        //---------------------------------------------------------------------
        // Constructor

        public Builder() {} // nothing to do

        //---------------------------------------------------------------------
        // Methods

        public SimpleCalendar<T> build() {
            return new SimpleCalendar<>(this);
        }

        public Builder<T> epochDay(int day) {
            this.epochDay = day;
            return this;
        }

        public Builder<T> era(String era) {
            this.era = Objects.requireNonNull(era);
            return this;
        }

        public Builder<T> priorEra(String priorEra) {
            this.priorEra = Objects.requireNonNull(priorEra);
            return this;
        }

        public Builder<T> month(T month, int length) {
            Objects.requireNonNull(month, "month is  null!");
            months.add(new Month<>(month, y -> length));
            return this;
        }

        public Builder<T> month(T month, YearDelta length) {
            Objects.requireNonNull(month, "month is  null!");
            Objects.requireNonNull(length, "month length function is  null!");
            months.add(new Month<>(month, length));
            return this;
        }
    }

}
