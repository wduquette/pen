package pen.calendars;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A SimpleCalendar is a calendar with some number of months, tied to a
 * an epoch day on a Fundamental Calendar.
 */
public class SimpleCalendar<T> implements Calendar {
    //-------------------------------------------------------------------------
    // Instance variables

    private final FundamentalCalendar foundation;
    private final int epochDay;
    private final String era;
    private final String priorEra;
    private final List<Month<T>> months;
    // TODO: newYearsDay

    //-------------------------------------------------------------------------
    // Constructor

    private SimpleCalendar(Builder builder) {
        this.foundation = builder.foundation;
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
     * Gets the number of days in the given year.
     * @param fundamentalYear The year
     * @return The number of days in that year.
     */
    public int daysInYear(int fundamentalYear) {
        return months.stream()
            .mapToInt(m -> daysInYear(fundamentalYear))
            .sum();
    }

    // NOTE: Might want a fundamental calendar with months.
    // The rest is simply year reckoning based on a fundamental day and
    // new year's date.

    /**
     * Converts an arbitrary day since the fundamental epoch to a date.
     * @param fundamentalDay The fundamental day
     * @return The date
     */
    public SimpleDate day2date(int fundamentalDay) {
        var fundamentalDate = foundation.day2date(fundamentalDay);
        var fundamentalYear = fundamentalDate.year();
        var daysInYear = foundation.daysInYear(fundamentalYear);

        var day = fundamentalDay - epochDay;

        if (day >= 0) {
            // FIRST, get the year
            int year = 0;

            while (day >= daysInYear) {
                year++;
                day -= daysInYear;
            }

            // NEXT, get the month and day of month
            var monthOfYear = 0;
            var dayOfMonth = 0;

            for (int i = 0; i < months.size(); i++) {
                var month = months.get(i);
                var daysInMonth = month.daysInMonth().apply(fundamentalYear);

                if (day <= daysInMonth) {
                    dayOfMonth = day + 1;
                    monthOfYear = i + 1;
                    break;
                }

                day -= daysInMonth;
            }

            return new SimpleDate(year, monthOfYear, dayOfMonth);
        } else {
//            int year = -1;
//            day = -day;
//
//            while (day > daysInYear) {
//                year--;
//                day -= daysInYear;
//            }
//
//            var daysInEarliestYear = yearLength.apply(year - 1);
//            var dayOfYear = daysInEarliestYear - day + 1;
//            return new FundamentalDate(year, dayOfYear);
            throw new UnsupportedOperationException("TODO");
        }
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
    public static record Month<T>(
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

        private FundamentalCalendar foundation;
        private int epochDay = 0;
        private String era = "CE";
        private String priorEra = null;
        private List<Month<T>> months = new ArrayList<>();

        //---------------------------------------------------------------------
        // Constructor

        public Builder() {} // nothing to do

        //---------------------------------------------------------------------
        // Methods

        public SimpleCalendar build() {
            return new SimpleCalendar(this);
        }

        public Builder foundation(FundamentalCalendar foundation) {
            this.foundation = Objects.requireNonNull(foundation);
            return this;
        }

        public Builder epochDay(int day) {
            this.epochDay = day;
            return this;
        }

        public Builder era(String era) {
            this.era = Objects.requireNonNull(era);
            return this;
        }

        public Builder priorEra(String priorEra) {
            this.priorEra = Objects.requireNonNull(priorEra);
            return this;
        }

        public Builder month(T month, int length) {
            Objects.requireNonNull(month, "month is  null!");
            months.add(new Month(month, y -> length));
            return this;
        }

        public Builder month(T month, YearDelta length) {
            Objects.requireNonNull(month, "month is  null!");
            Objects.requireNonNull(length, "month length function is  null!");
            months.add(new Month(month, length));
            return this;
        }
    }

}
