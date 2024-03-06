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
public class TrivialCalendar extends AbstractCalendar {
    //-------------------------------------------------------------------------
    // Instance Variables

    // A function for computing the length of the year in days given the
    // year number
    private final YearDelta yearLength;

    //-------------------------------------------------------------------------
    // Constructor

    // Creates the calendar given the builder parameters.
    private TrivialCalendar(Builder builder) {
        super(
            builder.epochOffset,
            builder.era,
            builder.priorEra,
            builder.week
        );
        this.yearLength = Objects.requireNonNull(builder.yearLength);
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

    //-------------------------------------------------------------------------
    // Builder

    public static class Builder {
        //---------------------------------------------------------------------
        // Instance Data

        private int epochOffset = 0;
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
         * Sets the epoch day corresponding to day 1 of year 1.
         * @param day The epoch day
         * @return The builder
         */
        public TrivialCalendar.Builder epochOffset(int day) {
            this.epochOffset = day;
            return this;
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

    public String toString() {
        return "TrivialCalendar[" + era() + "," + priorEra() + "]";
    }
}
