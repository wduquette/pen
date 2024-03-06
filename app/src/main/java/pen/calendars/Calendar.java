package pen.calendars;

import pen.calendars.formatter.DateFormat;

import java.util.List;

/**
 * A calendar for general use.  Time is measured in days since the epoch.
 * Epoch days can be converted to and from {@link YearDay} values; day 0
 * is year 1, day 1.  The year prior to year 1 is year -1.
 *
 * <h2>Weeks</h2>
 *
 * <p>Optionally the calendar may have a {@link Week}, a weekly cycle of N
 * {@link Weekday} objects; use {@code hasWeeks()} to test for a weekly
 * cycle.</p>
 *
 * <p>If the calendar has weeks, the cycle runs continuously backwards and
 * forwards from day 0; there is no support for intercalary days that are not
 * weekdays. (The real 7-day week stretches back unbroken into prehistory.)</p>
 *
 * <h2>Months</h2>
 *
 * <p>Optionally, the calendar may have a cycle of {@link SimpleMonth} objects;
 * use {@code hasMonths()} to test for a monthly cycle.</p>
 *
 * <p>If the calendar has months, then epoch days can be converted to and from
 * {@link Date} values; day 0 is year 1, month 1, day 1. Months can vary
 * in length to support leap years and similar patterns.</p>
 *
 * <h2>Converting Between Calendars</h2>
 *
 * <p>To convert dates between calendars, define them so that they have
 * the same epoch (day 0).</p>
 */
public interface Calendar {
    /** Default Calendar {@code era()}. */
    Era AFTER_EPOCH = new Era("AE", "After Epoch");

    /** Default Calendar {@code priorEra()} */
    Era BEFORE_EPOCH = new Era("BE", "Before Epoch");

    /** Default format for calendars with months. */
    DateFormat ERA_YMD = new DateFormat("E-y-m-d");

    /** Default format for calendars without months. */
    DateFormat ERA_YD = new DateFormat("E-y-D");

    //-------------------------------------------------------------------------
    // Calendar Metadata

    /**
     * The epoch day corresponding to day 1 of year 1 in this calendar.  This
     * is used to synchronize calendars in a setting.
     * @return The epoch offset
     */
    int epochOffset();

    /**
     * Gets the era for positive years.
     * @return The era
     */
    Era era();

    /**
     * Gets the era for negative years.
     * @return The prior era
     */
    Era priorEra();

    /**
     * The number of days in the given year, per the calendar
     * @param year The year
     * @return The number of days
     * @throws CalendarException for year 0
     */
    int daysInYear(int year);

    //-------------------------------------------------------------------------
    // Year/Day-of-year API

    /**
     * Creates a new YearDay value for this calendar.  Assumes that the
     * year and day-of-year are valid for the calendar.  Use
     * {@code validate(YearDay)} as needed.
     * @param year The year
     * @param dayOfYear The day of year
     * @return The value
     */
    YearDay yearDay(int year, int dayOfYear);

    /**
     * Converts an epoch day to a YearDay
     * @param epochDay The epoch day
     * @return The year/day-of-year
     */
    YearDay day2yearDay(int epochDay);

    /**
     * Converts a YearDay to epoch days.
     * @param yearDay The year/day-of-year
     * @return The day
     * @throws CalendarException if the data is invalid.
     */
    int yearDay2day(YearDay yearDay);

    /**
     * Validates the year/day-of-year against this calendar.
     * @param yearDay The year/day-of-year
     * @throws CalendarException if the data is invalid.
     */
    void validate(YearDay yearDay);

    //-------------------------------------------------------------------------
    // Date Formatting and Parsing

    String format(int day);
    String format(DateFormat format, int day);
    String format(Date date);
    String format(DateFormat format, Date date);
    String format(YearDay yearDay);
    String format(DateFormat format, YearDay yearDay);

    int parse(String dateString);
    int parse(DateFormat format, String dateString);

    //-------------------------------------------------------------------------
    // Month API, available if hasMonths().

    /**
     * Does this calendar divide the year into months?
     * See the Month API, below, if true.
     * @return true or false
     */
    default boolean hasMonths() {
        return false;
    }

    /**
     * Returns a date given the components. The components are presumed
     * to be valid; use {@code validate()} to check user input.
     * @param year The year number
     * @param monthOfYear The monthOfYear, 1 to monthsInYear()
     * @param dayOfMonth The dayOfMonth, 1 to daysInMonth(year, monthOfYear)
     * @return The date
     * @throws CalendarException if the date is invalid.
     * @throws CalendarException if !hasMonths()
     */
    default Date date(int year, int monthOfYear, int dayOfMonth) {
        throw noMonthlyCycle();
    }

    /**
     * Converts a {@link Date} to an epoch day.
     * @param date The date
     * @return The epoch day
     * @throws CalendarException if !hasMonths()
     */
    default int date2day(Date date) {
        throw noMonthlyCycle();
    }

    /**
     * Converts an epoch day to a {@link Date}.
     * @param day The day
     * @return The date
     * @throws CalendarException if !hasMonths()
     */
    default Date day2date(int day) {
        throw noMonthlyCycle();
    }

    /**
     * Returns the number of days in the given month in the given year.  (Month
     * lengths can vary!)
     * @param year The year
     * @param monthOfYear The monthOfYear, 1 to monthsInYear()
     * @return The number of days
     * @throws CalendarException if !hasMonths()
     */
    default int daysInMonth(int year, int monthOfYear) {
        throw noMonthlyCycle();
    }

    /**
     * Returns the number of months in the year.  Calendar assumes that all
     * years have the same number of months.
     * @return The number
     * @throws CalendarException if !hasMonths()
     */
    default int monthsInYear() {
        return months().size();
    }

    /**
     * Returns the Month object for the given month of the year.  The Month
     * object provides various forms of the month's name.
     * @param monthOfYear The monthOfYear,1 to monthsInYear()
     * @return The Month
     * @throws CalendarException if !hasMonths()
     */
    default Month month(int monthOfYear) {
        return months().get(monthOfYear - 1);
    }

    /**
     * Returns the list of {@link SimpleMonth} objects.
     * @return The list
     * @throws CalendarException if !hasMonths()
     */
    default List<Month> months() {
        throw noMonthlyCycle();
    }

    /**
     * Validates that a date is valid for this calendar.
     * @param date The date
     * @throws CalendarException if the date is invalid.
     * @throws CalendarException if !hasMonths.
     */
    default void validate(Date date) {
        throw noMonthlyCycle();
    }

    //-------------------------------------------------------------------------
    // Week API, available if hasWeeks()

    /**
     * Does calendar define a cycle of weekdays?
     * See the Week API below, if true.
     * @return true or false
     */
    boolean hasWeeks();

    /**
     * Gets the calendar's weekly cycle, if it has one.
     * @return The Week, or null.
     */
    Week week();

    /**
     * Gets the number of days in a week.
     * @return The number
     * @throws CalendarException if this calendar lacks a weekly cycle.
     */
    int daysInWeek();

    /**
     * Produces the day-of-week (1 through daysInWeek()) for the given
     * epoch day.
     * @param day The epoch day
     * @return The day-of-week
     * @throws CalendarException if this calendar lacks a weekly cycle.
     */
    int day2dayOfWeek(int day);

    /**
     * Produces the weekday for the given epoch day.
     * @param day The epoch day
     * @return The weekday
     * @throws CalendarException if this calendar lacks a
     * weekly cycle.
     */
    Weekday day2weekday(int day);

    //-------------------------------------------------------------------------
    // Standard Exception Factories

    /**
     * Used to a throw a "Calendar lacks a weekly cycle." exception.
     * @return The exception
     */
    static CalendarException noWeeklyCycle() {
        return new CalendarException("Calendar lacks a weekly cycle.");
    }

    /**
     * Used to a throw a "Calendar lacks a monthly cycle." exception.
     * @return The exception
     */
    static CalendarException noMonthlyCycle() {
        return new CalendarException("Calendar lacks a monthly cycle.");
    }
}
