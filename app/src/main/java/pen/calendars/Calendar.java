package pen.calendars;

/**
 * A calendar for general use.
 */
public interface Calendar {
    String era();
    String priorEra();

    /**
     * The number of days in the given year, per the calendar
     * @param year The year
     * @return The number of days
     */
    int daysInYear(int year);

    /**
     * Formats the fundamental day as a date string for the given calendar.
     * @param day The fundamental day
     * @return The string
     */
    String formatDate(int day);

    /**
     * Parses the date string, returning a fundamental day.
     * @param dateString The date string
     * @return The fundamental day
     * @throws CalendarException on parse error
     */
    int parseDate(String dateString);

    default boolean hasMonths() {
        return false;
    }

    default Date date(int year, int monthOfYear, int dayOfMonth) {
        throw noMonthlyCycle();
    }

    default Month month(int monthOfYear) {
        throw noMonthlyCycle();
    }

    default int monthsInYear() {
        throw noMonthlyCycle();
    }

    default int daysInMonth(int year, int monthOfYear) {
        throw noMonthlyCycle();
    }

    default int date2day(Date date) {
        throw noMonthlyCycle();
    }

    default Date day2date(int day) {
        throw noMonthlyCycle();
    }

    /**
     * Gets whether the calendar has a weekly cycle or not.
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

    /**
     * Produces the day-of-week (1 through daysInWeek) for the given day
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

    static CalendarException noWeeklyCycle() {
        return new CalendarException("Calendar lacks a weekly cycle.");
    }

    static CalendarException noMonthlyCycle() {
        return new CalendarException("Calendar lacks a monthly cycle.");
    }
}
