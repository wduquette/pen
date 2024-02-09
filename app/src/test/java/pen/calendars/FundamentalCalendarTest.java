package pen.calendars;

import org.junit.Test;

import static pen.checker.Checker.check;
import static pen.checker.Checker.checkThrows;

public class FundamentalCalendarTest {
    // For simplicity, all years are 100 days long.
    private static final FundamentalCalendar CAL =
        new FundamentalCalendar("AE", "BE", y -> 100, 3);

    @Test
    public void testDayToDate() {
        // Positive days
        check(CAL.day2date(0)).eq(date(0,1));
        check(CAL.day2date(1)).eq(date(0,2));
        check(CAL.day2date(99)).eq(date(0,100));
        check(CAL.day2date(100)).eq(date(1,1));
        check(CAL.day2date(101)).eq(date(1,2));

        // Negative days
        check(CAL.day2date(-1)).eq(date(-1,100));
        check(CAL.day2date(-2)).eq(date(-1,99));
        check(CAL.day2date(-100)).eq(date(-1,1));
        check(CAL.day2date(-101)).eq(date(-2,100));
    }

    @Test
    public void testValidate() {
        // OK
        for (var day = -101; day <= 101; day++) {
            CAL.validate(CAL.day2date(day));
        }

        // Exception
        checkThrows(() -> CAL.validate(date(0, -1)))
            .containsString("dayOfYear out of range for year 0: -1");
        checkThrows(() -> CAL.validate(date(0, 101)))
            .containsString("dayOfYear out of range for year 0: 101");
    }

    @Test
    public void testDateToDay() {
        // Positive dates
        check(CAL.date2day(date(0,1))).eq(0);
        check(CAL.date2day(date(0,2))).eq(1);
        check(CAL.date2day(date(0,100))).eq(99);
        check(CAL.date2day(date(1,1))).eq(100);
        check(CAL.date2day(date(1,2))).eq(101);

        // Negative dates
        check(CAL.date2day(date(-1,100))).eq(-1);
        check(CAL.date2day(date(-1,99))).eq(-2);
        check(CAL.date2day(date(-1,1))).eq(-100);
        check(CAL.date2day(date(-2,100))).eq(-101);

        // Exception
        checkThrows(() -> CAL.date2day(date(0, -1)))
            .containsString("dayOfYear out of range for year 0: -1");
        checkThrows(() -> CAL.date2day(date(0, 101)))
            .containsString("dayOfYear out of range for year 0: 101");
    }

    @Test
    public void testFormatDate() {
        // Positive dates
        check(CAL.date2string(date(0,1))).eq("AE0-001");
        check(CAL.date2string(date(0,2))).eq("AE0-002");
        check(CAL.date2string(date(0,100))).eq("AE0-100");
        check(CAL.date2string(date(1,1))).eq("AE1-001");
        check(CAL.date2string(date(1,2))).eq("AE1-002");

        // Negative dates
        check(CAL.date2string(date(-1,100))).eq("BE1-100");
        check(CAL.date2string(date(-1,99))).eq("BE1-099");
        check(CAL.date2string(date(-1,1))).eq("BE1-001");
        check(CAL.date2string(date(-2,100))).eq("BE2-100");

        // From day
        for (int day = -101; day <= 101; day++) {
            var date = CAL.day2date(day);
            check(CAL.formatDate(day)).eq(CAL.date2string(date));
        }

        // Exception
        checkThrows(() -> CAL.date2string(date(0, -1)))
            .containsString("dayOfYear out of range for year 0: -1");
        checkThrows(() -> CAL.date2string(date(0, 101)))
            .containsString("dayOfYear out of range for year 0: 101");
    }

    @Test
    public void testParseDate() {
        // Positive dates
        check(CAL.parseDate("AE0-001")).eq(0);
        check(CAL.parseDate("AE0-002")).eq(1);
        check(CAL.parseDate("AE0-100")).eq(99);
        check(CAL.parseDate("AE1-001")).eq(100);
        check(CAL.parseDate("AE1-002")).eq(101);

        // Negative dates
        check(CAL.parseDate("BE1-100")).eq(-1);
        check(CAL.parseDate("BE1-099")).eq(-2);
        check(CAL.parseDate("BE1-001")).eq(-100);
        check(CAL.parseDate("BE2-100")).eq(-101);

        // Errors
        checkThrows(() -> CAL.parseDate("ABC0-001"))
            .containsString("Invalid format, expected \"AE|BE<year>-<dayOfYear>");
        checkThrows(() -> CAL.parseDate("AE-001"))
            .containsString("Invalid format, expected \"AE|BE<year>-<dayOfYear>");
        checkThrows(() -> CAL.parseDate("AE1-001-xxx"))
            .containsString("Invalid format, expected \"AE|BE<year>-<dayOfYear>");
        checkThrows(() -> CAL.parseDate("AE1-000"))
            .containsString("dayOfYear out of range");
    }

    private FundamentalDate date(int year, int day) {
        return new FundamentalDate(year, day);
    }
}
