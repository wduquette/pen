package pen.calendars;

/**
 * A standard GregorianCalendar
 */
public class Gregorian {
    private Gregorian() {} // Not instantiable.

    public static final Week WEEK = new Week(StandardWeekDays.weekdays(), 1);
    public static final BasicCalendar CALENDAR = new BasicCalendar.Builder()
        .era(new Era("AD", "Anno Domini"))
        .priorEra(new Era("BC", "Before Christ"))
        .month(StandardMonths.JANUARY, 31)
        .month(StandardMonths.FEBRUARY, Gregorian::lengthOfFebruary)
        .month(StandardMonths.MARCH, 31)
        .month(StandardMonths.APRIL, 30)
        .month(StandardMonths.MAY, 31)
        .month(StandardMonths.JUNE, 30)
        .month(StandardMonths.JULY, 31)
        .month(StandardMonths.AUGUST, 31)
        .month(StandardMonths.SEPTEMBER, 30)
        .month(StandardMonths.OCTOBER, 31)
        .month(StandardMonths.NOVEMBER, 30)
        .month(StandardMonths.DECEMBER, 31)
        .week(WEEK)
        .build();

    private static int lengthOfFebruary(int year) {
        if (year % 400 == 0) {
            return 29;
        } else if (year % 100 == 0) {
            return 28;
        } else if (year % 4 == 0) {
            return 29;
        } else {
            return 28;
        }
    }
}
