package pen.calendars;

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
 * <p>Optionally, the calendar may have a cycle of {@link Month} objects;
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
    //-------------------------------------------------------------------------
    // Features common to all implementations

    /**
     * The number of days in the given year, per the calendar
     * @param year The year
     * @return The number of days
     * @throws CalendarException for year 0
     */
    int daysInYear(int year);

    // TODO: Replace with an Era object.
    String era();
    String priorEra();

    // TODO: Support a DateFormatter class
    String formatDate(int day);
    int parseDate(String dateString);

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

    // date2yearDay, yearDate2date

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
     * Returns the list of {@link Month} objects.
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
    default boolean hasWeeks() {
        return week() != null;
    }

    /**
     * Gets the calendar's weekly cycle, if it has one.
     * @return The Week, or null.
     */
    default Week week() {
        return null;
    }

    /**
     * Gets the number of days in a week.
     * @return The number
     * @throws CalendarException if this calendar lacks a weekly cycle.
     */
    default int daysInWeek() {
        if (hasWeeks()) {
            return week().weekdays().size();
        } else {
            throw noWeeklyCycle();
        }
    }

    /**
     * Produces the day-of-week (1 through daysInWeek()) for the given
     * epoch day.
     * @param day The epoch day
     * @return The day-of-week
     * @throws CalendarException if this calendar lacks a weekly cycle.
     */
    default int day2dayOfWeek(int day) {
        if (hasWeeks()) {
            var weekday = week().day2weekday(day);
            return week().indexOf(weekday) + 1;
        } else {
            throw noWeeklyCycle();
        }
    }
    /**
     * Produces the weekday for the given epoch day.
     * @param day The epoch day
     * @return The weekday
     * @throws CalendarException if this calendar lacks a
     * weekly cycle.
     */
    default Weekday day2weekday(int day) {
        if (hasWeeks()) {
            return week().day2weekday(day);
        } else {
            throw noWeeklyCycle();
        }
    }

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
