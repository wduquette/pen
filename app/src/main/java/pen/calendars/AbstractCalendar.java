package pen.calendars;

/**
 * An abstract base class for concrete {@link Calendar} classes.  Concrete
 * classes must:
 *
 * <ul>
 * <li>Define {@code daysInYear(int)}</li>
 * <li>Methods associated with optional APIs (e.g., {@code hasMonths}.</li>
 * </ul>
 */
public abstract class AbstractCalendar implements Calendar {
    //-------------------------------------------------------------------------
    // Instance Variables

    // The epoch day corresponding to day 1 of year 1 in this calendar.  This
    // is used to synchronize calendars in a setting.
    private final int epochOffset;

    // The era symbol for positive years.
    private final Era era;

    // The era symbol for negative years
    private final Era priorEra;

    // The weekly cycle; possibly null
    private final Week week;

    //-------------------------------------------------------------------------
    // Constructor

    public AbstractCalendar(
        int epochOffset,
        Era era,
        Era priorEra,
        Week week
    ) {
        this.epochOffset = epochOffset;
        this.era = era;
        this.priorEra = priorEra;
        this.week = week;
    }

    //-------------------------------------------------------------------------
    // Calendar API: Metadata

    @Override
    public final int epochOffset() {
        return epochOffset;
    }

    @Override
    public final Era era() {
        return era;
    }

    @Override
    public final Era priorEra() {
        return priorEra;
    }

    //-------------------------------------------------------------------------
    // Calendar API: YearDay Computations

    @Override
    public final YearDay yearDay(int year, int dayOfYear) {
        return new YearDay(this, year, dayOfYear);
    }

    @Override
    public final YearDay day2yearDay(int epochDay) {
        var day = epochDay - epochOffset();

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
            return yearDay(year, dayOfYear);
        }
    }

    @Override
    public final int yearDay2day(YearDay yearDay) {
        // FIRST, validate the dayOfYear.
        validate(yearDay);

        // NEXT, positive years, then negative years
        if (yearDay.year() > 0) {
            var day = yearDay.dayOfYear() - 1;
            var year = yearDay.year() - 1;

            while (year >= 1) {
                day += daysInYear(year);
                year--;
            }

            return day + epochOffset();
        } else {
            var day = daysInYear(yearDay.year()) - yearDay.dayOfYear() + 1;
            var year = yearDay.year() + 1;

            while (year < 0) {
                day += daysInYear(year);
                year++;
            }

            return -day + epochOffset();
        }
    }

    @Override
    public final void validate(YearDay yearDay) {
        if (!yearDay.calendar().equals(this)) {
            throw new CalendarException(
                "Calendar mismatch, expected \"" + this + "\", got \"" +
                    yearDay.calendar() + "\"");
        }

        if (yearDay.year() == 0) {
            throw new CalendarException("year is 0 in date: \"" + yearDay + "\".");
        }

        if (yearDay.dayOfYear() < 1 ||
            yearDay.dayOfYear() > daysInYear(yearDay.year()))
        {
            throw new CalendarException("dayOfYear out of range for year " +
                yearDay.year() + " in date: \"" + yearDay + "\"");
        }
    }

    //-------------------------------------------------------------------------
    // Week API

    /**
     * Does calendar define a cycle of weekdays?
     * See the Week API below, if true.
     * @return true or false
     */
    public final boolean hasWeeks() {
        return week != null;
    }

    /**
     * Gets the calendar's weekly cycle, if it has one.
     * @return The Week, or null.
     */
    public final Week week() {
        return week;
    }

    /**
     * Gets the number of days in a week.
     * @return The number
     * @throws CalendarException if this calendar lacks a weekly cycle.
     */
    public final int daysInWeek() {
        if (hasWeeks()) {
            return week.weekdays().size();
        } else {
            throw Calendar.noWeeklyCycle();
        }
    }

    /**
     * Produces the day-of-week (1 through daysInWeek()) for the given
     * epoch day.
     * @param day The epoch day
     * @return The day-of-week
     * @throws CalendarException if this calendar lacks a weekly cycle.
     */
    public final int day2dayOfWeek(int day) {
        if (hasWeeks()) {
            var weekday = week.day2weekday(day);
            return week.indexOf(weekday) + 1;
        } else {
            throw Calendar.noWeeklyCycle();
        }
    }
    /**
     * Produces the weekday for the given epoch day.
     * @param day The epoch day
     * @return The weekday
     * @throws CalendarException if this calendar lacks a
     * weekly cycle.
     */
    public final Weekday day2weekday(int day) {
        if (hasWeeks()) {
            return week.day2weekday(day);
        } else {
            throw Calendar.noWeeklyCycle();
        }
    }
}
