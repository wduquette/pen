package pen.calendars;

import org.junit.Test;

import static pen.checker.Checker.check;
import static pen.checker.Checker.checkThrows;

public class FundamentalCalendarTest {
    // For simplicity, all years are 100 days long.
    private FundamentalCalendar cal100 = new FundamentalCalendar("FC", y -> 100);

    @Test
    public void testDayToDate() {
        // Positive days
        check(cal100.day2date(0)).eq(date(0,1));
        check(cal100.day2date(1)).eq(date(0,2));
        check(cal100.day2date(99)).eq(date(0,100));
        check(cal100.day2date(100)).eq(date(1,1));
        check(cal100.day2date(101)).eq(date(1,2));

        // Negative days
        check(cal100.day2date(-1)).eq(date(-1,100));
        check(cal100.day2date(-2)).eq(date(-1,99));
        check(cal100.day2date(-100)).eq(date(-1,1));
        check(cal100.day2date(-101)).eq(date(-2,100));
    }

    @Test
    public void testDateToDay() {
        // Positive dates
        check(cal100.date2day(date(0,1))).eq(0);
        check(cal100.date2day(date(0,2))).eq(1);
        check(cal100.date2day(date(0,100))).eq(99);
        check(cal100.date2day(date(1,1))).eq(100);
        check(cal100.date2day(date(1,2))).eq(101);

        // Negative dates
        check(cal100.date2day(date(-1,100))).eq(-1);
        check(cal100.date2day(date(-1,99))).eq(-2);
        check(cal100.date2day(date(-1,1))).eq(-100);
        check(cal100.date2day(date(-2,100))).eq(-101);

        // Exception
        checkThrows(() -> cal100.date2day(date(0, -1)))
            .containsString("dayOfYear out of range for year 0: -1");
        checkThrows(() -> cal100.date2day(date(0, 101)))
            .containsString("dayOfYear out of range for year 0: 101");



    }

    private FundamentalDate date(int year, int day) {
        return new FundamentalDate(year, day);
    }
}
