package pen.calendars;

import org.junit.Test;

import static pen.checker.Checker.check;
import static pen.checker.Checker.checkThrows;

public class WeekTest {
    private static final Week WEEK = new Week(Weekdays.values(), 2);

    @Test
    public void testDayToWeekday() {
        check(WEEK.day2weekday(-2)).eq(Weekdays.SUNDAY);
        check(WEEK.day2weekday(-1)).eq(Weekdays.MONDAY);
        check(WEEK.day2weekday(0)).eq(Weekdays.TUESDAY);
        check(WEEK.day2weekday(1)).eq(Weekdays.WEDNESDAY);
        check(WEEK.day2weekday(2)).eq(Weekdays.THURSDAY);
        check(WEEK.day2weekday(3)).eq(Weekdays.FRIDAY);
        check(WEEK.day2weekday(4)).eq(Weekdays.SATURDAY);
        check(WEEK.day2weekday(5)).eq(Weekdays.SUNDAY);
        check(WEEK.day2weekday(6)).eq(Weekdays.MONDAY);
        check(WEEK.day2weekday(7)).eq(Weekdays.TUESDAY);
    }

    @Test
    public void testIndexOf() {
        for (int i = 0; i < WEEK.weekdays().length; i++) {
            var weekday = WEEK.weekdays()[i];
            check(WEEK.indexOf(weekday)).eq(i);
        }

        checkThrows(() -> WEEK.indexOf(null))
            .containsString("Invalid weekday value: \"null\"");
    }

}
