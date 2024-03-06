package pen.calendars;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * A {@link Calendar} with a cycle of {@link Month} values and an optional
 * {@link Week}.  Day-of-year 1 of any year is equivalent to
 * (year 1, month-of-year 1, day-of-month 1).
 *
 * <p>It is intended  for support the following use cases.</p>
 *
 * <ul>
 * <li>Being a fundamental calendar for worlds that have multiple reckonings
 * (e.g., regnal calendars) based on the same monthly and weekly cycles.</li>
 * <li>Mimicking the Gregorian Calendar at needed.</li>
 * </ul>
 */
@SuppressWarnings("unused")
public class BasicCalendar extends AbstractCalendar {
    //-------------------------------------------------------------------------
    // Instance Variables

    // The epoch day corresponding to 1/1/1 in this calendar.  This can be set
    // so that this calendar uses the same epoch days as other calendars in a
    // setting.
    private final int epochOffset;

    // The era symbol for positive years.
    private final Era era;

    // The era symbol for negative years
    private final Era priorEra;

    // The month definitions
    private final List<MonthRecord> months;

    // The weekly cycle; possibly null
    private final Week week;

    //-------------------------------------------------------------------------
    // Constructor

    // Creates the calendar given the builder parameters.
    private BasicCalendar(Builder builder) {
        this.epochOffset   = builder.epochOffset;
        this.era           = Objects.requireNonNull(builder.era);
        this.priorEra      = Objects.requireNonNull(builder.priorEra);
        this.months        = Collections.unmodifiableList(builder.months);
        this.week          = builder.week;
    }

    //-------------------------------------------------------------------------
    // Methods specific to BasicCalendar

    /**
     * Gets the offset between the epoch day 0 and this calendar's
     * (year 1, month 1, day 1).
     * @return The offset
     */
    public int epochOffset() {
        return epochOffset;
    }

    //-------------------------------------------------------------------------
    // Calendar API: Features common to all implementations

    @Override
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

    @Override
    public Era era() {
        return era;
    }

    @Override
    public Era priorEra() {
        return priorEra;
    }

    //-------------------------------------------------------------------------
    // Calendar API: Months

    @Override
    public boolean hasMonths() {
        return true;
    }

    @Override
    public Date date(int year, int month, int day) {
        return new Date(this, year, month, day);
    }

    @Override
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

        return day + epochOffset;
    }

    @Override
    public Date day2date(int epochDay) {
        var day = epochDay - epochOffset;
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

    @Override
    public int daysInMonth(int year, int monthOfYear) {
        if (year > 0) {
            return months.get(monthOfYear - 1).daysInMonth().apply(year);
        } else if (year < 0) {
            return months.get(monthOfYear - 1).daysInMonth().apply(year + 1);
        } else {
            throw new CalendarException("Year cannot be 0.");
        }
    }

    @Override
    public List<Month> months() {
        return months.stream().map(MonthRecord::month).toList();
    }

    @Override
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


    //-------------------------------------------------------------------------
    // Calendar API: Weeks

    @Override
    public boolean hasWeeks() {
        return week != null;
    }

    @Override
    public Week week() {
        return week;
    }

    //-------------------------------------------------------------------------
    // Helper Methods

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

    //-------------------------------------------------------------------------
    // Object API

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BasicCalendar that = (BasicCalendar) o;

        if (epochOffset != that.epochOffset) return false;
        if (!era.equals(that.era)) return false;
        if (!priorEra.equals(that.priorEra)) return false;
        if (!months.equals(that.months)) return false;
        return Objects.equals(week, that.week);
    }

    @Override
    public int hashCode() {
        int result = epochOffset;
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
        return "BasicCalendar[" + era + "," + priorEra + "," + months.size()
            + "]";
    }

    //-------------------------------------------------------------------------
    // Helper Classes

    /**
     * Defines a month in terms of an external object (an enum or a string)
     * and the length of the month given the calendar year.
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

        private int epochOffset = 0;
        private Era era = AFTER_EPOCH;
        private Era priorEra = BEFORE_EPOCH;
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
        public BasicCalendar build() {
            return new BasicCalendar(this);
        }

        /**
         * Sets the epoch day corresponding to year 1, month 1, day 1.
         * @param day The epoch day
         * @return The builder
         */
        public Builder epochOffset(int day) {
            this.epochOffset = day;
            return this;
        }

        /**
         * Sets the era for this calendar.  Defaults to "AE",
         * "After Epoch".
         * @param era The era.
         * @return the builder
         */
        public Builder era(Era era) {
            this.era = Objects.requireNonNull(era);
            return this;
        }

        /**
         * Sets the prior era for this calendar.  Defaults to "BE",
         * "Before Epoch".
         * @param priorEra The era.
         * @return the builder
         */
        public Builder priorEra(Era priorEra) {
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
