package pen.calendars;

import org.junit.Test;

import java.util.List;

import static pen.checker.Checker.check;
import static pen.checker.Checker.checkThrows;

public class WeekTest {
    private static final Week WEEK =
        new Week(List.of(StandardWeekDays.values()), 2);

    @Test
    public void testDayToWeekday() {
        check(WEEK.day2weekday(-2)).eq(StandardWeekDays.SUNDAY);
        check(WEEK.day2weekday(-1)).eq(StandardWeekDays.MONDAY);
        check(WEEK.day2weekday(0)).eq(StandardWeekDays.TUESDAY);
        check(WEEK.day2weekday(1)).eq(StandardWeekDays.WEDNESDAY);
        check(WEEK.day2weekday(2)).eq(StandardWeekDays.THURSDAY);
        check(WEEK.day2weekday(3)).eq(StandardWeekDays.FRIDAY);
        check(WEEK.day2weekday(4)).eq(StandardWeekDays.SATURDAY);
        check(WEEK.day2weekday(5)).eq(StandardWeekDays.SUNDAY);
        check(WEEK.day2weekday(6)).eq(StandardWeekDays.MONDAY);
        check(WEEK.day2weekday(7)).eq(StandardWeekDays.TUESDAY);
    }

    @Test
    public void testIndexOf() {
        for (int i = 0; i < WEEK.weekdays().size(); i++) {
            var weekday = WEEK.weekdays().get(i);
            check(WEEK.indexOf(weekday)).eq(i);
        }
    }
}
