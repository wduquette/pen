package pen.calendars;

/**
 * A standard GregorianCalendar
 */
public class Gregorian {
    private Gregorian() {} // Not instantiable.

    public static final Week WEEK = new Week(StandardWeek.DAYS, 1);
    public static final BasicCalendar CALENDAR = new BasicCalendar.Builder()
        .era(new Era("AD", "Anno Domini"))
        .priorEra(new Era("BC", "Before Christ"))
        .month(StandardMonth.JANUARY, 31)
        .month(StandardMonth.FEBRUARY, Gregorian::februaryDays)
        .month(StandardMonth.MARCH, 31)
        .month(StandardMonth.APRIL, 30)
        .month(StandardMonth.MAY, 31)
        .month(StandardMonth.JUNE, 30)
        .month(StandardMonth.JULY, 31)
        .month(StandardMonth.AUGUST, 31)
        .month(StandardMonth.SEPTEMBER, 30)
        .month(StandardMonth.OCTOBER, 31)
        .month(StandardMonth.NOVEMBER, 30)
        .month(StandardMonth.DECEMBER, 31)
        .week(WEEK)
        .build();

    public static boolean isLeapYear(int year) {
        if (year % 400 == 0) {
            return true;
        } else if (year % 100 == 0) {
            return false;
        } else {
            return year % 4 == 0;
        }
    }

    public static int februaryDays(int year) {
        return isLeapYear(year) ? 29 : 28;
    }
}
