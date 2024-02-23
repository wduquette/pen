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
    private final Era era;

    // The era symbol for negative years
    private final Era priorEra;

    // A function for computing the length of the year in days given the
    // year number
    private final YearDelta yearLength;

    // The weekly cycle; possibly null
    private final Week week;

    //-------------------------------------------------------------------------
    // Constructor

    // Creates the calendar given the builder parameters.
    private TrivialCalendar(Builder builder) {
        this.era             = Objects.requireNonNull(builder.era);
        this.priorEra        = Objects.requireNonNull(builder.priorEra);
        this.yearLength      = Objects.requireNonNull(builder.yearLength);
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


    @Override
    public Era era() {
        return era;
    }

    @Override
    public Era priorEra() {
        return priorEra;
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

        private Era era = AFTER_EPOCH;
        private Era priorEra = BEFORE_EPOCH;
        private YearDelta yearLength = (y -> 365);
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
         * Sets the era for this calendar.  Defaults to "AE",
         * "After Epoch".
         * @param era The era .
         * @return the builder
         */
        public TrivialCalendar.Builder era(Era era) {
            this.era = Objects.requireNonNull(era);
            return this;
        }

        /**
         * Sets the prior era for this calendar.  Defaults to "BE",
         * "Before Epoch".
         * @param priorEra The era .
         * @return the builder
         */
        public TrivialCalendar.Builder priorEra(Era priorEra) {
            this.priorEra = Objects.requireNonNull(priorEra);
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
