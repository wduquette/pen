package pen.calendars;

import org.junit.Test;

import java.util.List;

/**
 * This is not actually a test, but just a simple way to do some experimenting.
 */
public class DevTest {
    // Fundamental Calendar
    private static final FundamentalCalendar FC =
        new FundamentalCalendar.Builder()
            .era("AE")
            .priorEra("BE")
            .yearLength(366)
            .dayOfYearDigits(3)
            .build();

    // Era for Armorica tales.
    // 979 ME = 1 AF.  Years have 366 days.
    // Let's set epochDay to -978*366
    private static final SimpleCalendar ME = new SimpleCalendar.Builder()
        .era("ME")
        .priorEra("BME")
        .epochDay(-978*366)
        .month(StandardMonths.JANUARY, 31)
        .month(StandardMonths.FEBRUARY, 28)
        .month(StandardMonths.MARCH, 31)
        .month(StandardMonths.APRIL, 30)
        .month(StandardMonths.MAY, 31)
        .month(StandardMonths.JUNE, 30)
        .month(StandardMonths.JULY, 31)
        .month(StandardMonths.AUGUST, 31)
        .month(StandardMonths.SEPTEMBER, 30)
        .month(StandardMonths.OCTOBER, 31)
        .month(StandardMonths.NOVEMBER, 31)
        .month(StandardMonths.DECEMBER, 31)
        .build();
    private static final Week week =
        new Week(List.of(StandardWeekDays.values()), 1);

    @Test
    public void testExperiment() {
        var date = ME.date(1011, 1, 1);
        var day = ME.date2day(date);
        System.out.println("date    = " + date);
        System.out.println("day     = " + day);
        System.out.println("datestr = " + ME.formatDate(day));
        System.out.println("fcdate  = " + FC.formatDate(day));
        System.out.println("weekday = " + week.day2weekday(day));
    }

}
