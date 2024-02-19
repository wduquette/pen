package pen.calendars;

import java.util.List;

/**
 * A calendar for general use.
 */
public interface Calendar {
    //-------------------------------------------------------------------------
    // Basic Features, common to all implementations

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

    // date2yearDay, yearDate2date

    //-------------------------------------------------------------------------
    // Feature Queries: What features does this calendar support?

    /**
     * Does this calendar divide the year into months?
     * See the Month API, below, if true.
     * @return true or false
     */
    default boolean hasMonths() {
        return false;
    }

    /**
     * Does calendar define a cycle of weekdays?
     * See the Week API below, if true.
     * @return true or false
     */
    default boolean hasWeeks() {
        return week() != null;
    }

    //-------------------------------------------------------------------------
    // Month API, available if hasMonths().

    /**
     * Returns a date given the components, which are presumed to be valid for
     * this calendar.
     * @param year The year number
     * @param monthOfYear The monthOfYear, 1 to monthsInYear()
     * @param dayOfMonth The dayOfMonth, 1 to daysInMonth(year, monthOfYear)
     * @return The date
     * @throws CalendarException if !hasMonths()
     */
    default Date date(int year, int monthOfYear, int dayOfMonth) {
        throw noMonthlyCycle();
    }

    /**
     * Converts a Date to a day since the fundamental epoch.
     * @param date The date
     * @return The fundamental day
     * @throws CalendarException if !hasMonths()
     */
    default int date2day(Date date) {
        throw noMonthlyCycle();
    }

    /**
     * Converts a day since the fundamental epoch to a Date.
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
        throw noMonthlyCycle();
    }

    /**
     * Returns the Month object for the given month of the year.  The Month
     * object provides various forms of the month's name.
     * @param monthOfYear The monthOfYear,1 to monthsInYear()
     * @return The Month
     * @throws CalendarException if !hasMonths()
     */
    default Month month(int monthOfYear) {
        throw noMonthlyCycle();
    }

    /**
     * Returns the list of Month objects.
     * @return The list
     * @throws CalendarException if !hasMonths()
     */
    default List<Month> months() {
        throw noMonthlyCycle();
    }

    /**
     * Throws CalendarException if the date is invalid for this calendar.
     * @param date The date
     * @throws CalendarException if the date is not valid.
     * @throws CalendarException if !hasMonths().
     */
    default void validate(Date date) {
        throw noMonthlyCycle();
    }

    //-------------------------------------------------------------------------
    // Week API, available if hasWeeks()

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
     * Produces the day-of-week (1 through daysInWeek()) for the given day
     * @param day The day
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
     * Produces the weekday for the given fundamental day.
     * @param day The day
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
