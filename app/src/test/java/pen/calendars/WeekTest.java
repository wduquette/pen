package pen.calendars;

import org.junit.Test;

import static pen.checker.Checker.check;

public class WeekTest {
    private static final Week WEEK =
        new Week(StandardWeek.DAYS, 2);

    @Test
    public void testDayToWeekday() {
        check(WEEK.day2weekday(-2)).eq(StandardWeek.SUNDAY);
        check(WEEK.day2weekday(-1)).eq(StandardWeek.MONDAY);
        check(WEEK.day2weekday(0)).eq(StandardWeek.TUESDAY);
        check(WEEK.day2weekday(1)).eq(StandardWeek.WEDNESDAY);
        check(WEEK.day2weekday(2)).eq(StandardWeek.THURSDAY);
        check(WEEK.day2weekday(3)).eq(StandardWeek.FRIDAY);
        check(WEEK.day2weekday(4)).eq(StandardWeek.SATURDAY);
        check(WEEK.day2weekday(5)).eq(StandardWeek.SUNDAY);
        check(WEEK.day2weekday(6)).eq(StandardWeek.MONDAY);
        check(WEEK.day2weekday(7)).eq(StandardWeek.TUESDAY);
    }

    @Test
    public void testIndexOf() {
        for (int i = 0; i < WEEK.weekdays().size(); i++) {
            var weekday = WEEK.weekdays().get(i);
            check(WEEK.indexOf(weekday)).eq(i);
        }
    }
}
