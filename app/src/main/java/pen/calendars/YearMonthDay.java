package pen.calendars;

/**
 * A date in a particular calendar
 * @param calendar The calendar in question
 * @param year The year number, omitting 0
 * @param monthOfYear The month number, counting from 1
 * @param dayOfMonth The day of the month, counting from 1
 */
public record YearMonthDay(
    Calendar calendar,
    int year,
    int monthOfYear,
    int dayOfMonth
) {
    @Override
    public String toString() {
        return calendar + ":" + year + "-" + monthOfYear + "-" + dayOfMonth;
    }

    public String era() {
        return (year > 0) ? calendar.era() : calendar.priorEra();
    }

    public Month month() {
        return calendar.month(monthOfYear);
    }

    public int daysInMonth() {
        return calendar.daysInMonth(year, monthOfYear);
    }

    public Weekday weekday() {
        return calendar().day2weekday(calendar.date2day(this));
    }

    public int dayOfWeek() {
        return calendar().day2dayOfWeek(calendar.date2day(this));
    }

}
