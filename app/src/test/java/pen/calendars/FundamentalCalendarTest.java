package pen.calendars;

import org.junit.Test;

import static pen.checker.Checker.check;
import static pen.checker.Checker.checkThrows;

public class FundamentalCalendarTest {
    // A calendar with 10 day "years"
    private static final FundamentalCalendar TEN =
        new FundamentalCalendar("AT", "BT", y -> 10, 2);

    // A calendar with 10 day "years" plus a leap year every fourth year
    private static final FundamentalCalendar LEAP =
        new FundamentalCalendar("AT", "BT", y -> y % 4 == 0 ? 11 : 10, 2);

    @Test
    public void testDay2date_TEN() {
        // Positive days
        check(TEN.day2date(0)).eq(date(0,1));
        check(TEN.day2date(1)).eq(date(0,2));
        check(TEN.day2date(9)).eq(date(0,10));
        check(TEN.day2date(10)).eq(date(1,1));
        check(TEN.day2date(11)).eq(date(1,2));

        // Negative days
        check(TEN.day2date(-1)).eq(date(-1,10));
        check(TEN.day2date(-2)).eq(date(-1,9));
        check(TEN.day2date(-3)).eq(date(-1,8));
        check(TEN.day2date(-4)).eq(date(-1,7));
        check(TEN.day2date(-5)).eq(date(-1,6));
        check(TEN.day2date(-6)).eq(date(-1,5));
        check(TEN.day2date(-7)).eq(date(-1,4));
        check(TEN.day2date(-8)).eq(date(-1,3));
        check(TEN.day2date(-9)).eq(date(-1,2));
        check(TEN.day2date(-10)).eq(date(-1,1));
        check(TEN.day2date(-11)).eq(date(-2,10));
    }

    @Test
    public void testDateToDay_TEN() {
        for (int i = -25; i <= 25; i++) {
            var date = TEN.day2date(i);
            check(TEN.date2day(date)).eq(i);
        }

        // Exception
        checkThrows(() -> TEN.date2day(date(0, 0)))
            .containsString("dayOfYear out of range for year 0: 0");
        checkThrows(() -> TEN.date2day(date(0, -1)))
            .containsString("dayOfYear out of range for year 0: -1");
        checkThrows(() -> TEN.date2day(date(0, 11)))
            .containsString("dayOfYear out of range for year 0: 11");
    }

    @Test
    public void testValidate_TEN() {
        // OK
        for (var day = -25; day <= 251; day++) {
            TEN.validate(TEN.day2date(day));
        }

        // Exception
        checkThrows(() -> TEN.validate(date(0, 0)))
            .containsString("dayOfYear out of range for year 0: 0");
        checkThrows(() -> TEN.validate(date(0, -1)))
            .containsString("dayOfYear out of range for year 0: -1");
        checkThrows(() -> TEN.validate(date(0, 11)))
            .containsString("dayOfYear out of range for year 0: 11");
    }

    @Test
    public void testDate2stringTEN() {
        // Positive dates
        check(TEN.date2string(date(0,1))).eq("AT0-01");
        check(TEN.date2string(date(0,2))).eq("AT0-02");
        check(TEN.date2string(date(0,10))).eq("AT0-10");
        check(TEN.date2string(date(1,1))).eq("AT1-01");
        check(TEN.date2string(date(1,2))).eq("AT1-02");

        // Negative dates
        check(TEN.date2string(date(-1,10))).eq("BT1-10");
        check(TEN.date2string(date(-1,9))).eq("BT1-09");
        check(TEN.date2string(date(-1,1))).eq("BT1-01");
        check(TEN.date2string(date(-2,10))).eq("BT2-10");

        // From day
        for (int day = -101; day <= 101; day++) {
            var date = TEN.day2date(day);
            check(TEN.formatDate(day)).eq(TEN.date2string(date));
        }

        // Exception
        checkThrows(() -> TEN.date2string(date(0, 0)))
            .containsString("dayOfYear out of range for year 0: 0");
        checkThrows(() -> TEN.date2string(date(0, 11)))
            .containsString("dayOfYear out of range for year 0: 11");
    }

    @Test
    public void testParseDate_TEN() {
        // Positive dates
        check(TEN.parseDate("AT0-01")).eq(0);
        check(TEN.parseDate("AT0-02")).eq(1);
        check(TEN.parseDate("AT0-10")).eq(9);
        check(TEN.parseDate("AT1-01")).eq(10);
        check(TEN.parseDate("AT1-02")).eq(11);

        // Negative dates
        check(TEN.parseDate("BT1-10")).eq(-1);
        check(TEN.parseDate("BT1-09")).eq(-2);
        check(TEN.parseDate("BT1-01")).eq(-10);
        check(TEN.parseDate("BT2-10")).eq(-11);

        // Errors
        checkThrows(() -> TEN.parseDate("ABC0-01"))
            .containsString("Invalid format, expected \"AT|BT<year>-<dayOfYear>");
        checkThrows(() -> TEN.parseDate("AT-01"))
            .containsString("Invalid format, expected \"AT|BT<year>-<dayOfYear>");
        checkThrows(() -> TEN.parseDate("AT1-01-xxx"))
            .containsString("Invalid format, expected \"AT|BT<year>-<dayOfYear>");
        checkThrows(() -> TEN.parseDate("AT1-00"))
            .containsString("dayOfYear out of range");
    }

    @Test
    public void testDaysInYear_LEAP() {
        check(LEAP.daysInYear(-4)).eq(11);
        check(LEAP.daysInYear(-3)).eq(10);
        check(LEAP.daysInYear(-2)).eq(10);
        check(LEAP.daysInYear(-1)).eq(10);
        check(LEAP.daysInYear(0)).eq(11);
        check(LEAP.daysInYear(1)).eq(10);
        check(LEAP.daysInYear(2)).eq(10);
        check(LEAP.daysInYear(3)).eq(10);
        check(LEAP.daysInYear(4)).eq(11);
    }

    @Test
    public void testDay2date_LEAP() {
        // Positive days
        check(LEAP.day2date(0)).eq(date(0,1));
        check(LEAP.day2date(1)).eq(date(0,2));
        check(LEAP.day2date(9)).eq(date(0,10));
        check(LEAP.day2date(10)).eq(date(0,11));
        check(LEAP.day2date(11)).eq(date(1,1));
        check(LEAP.day2date(20)).eq(date(1,10));
        check(LEAP.day2date(21)).eq(date(2,1));

        // Negative days
        check(LEAP.day2date(-1)).eq(date(-1,10));
        check(LEAP.day2date(-2)).eq(date(-1,9));
        check(LEAP.day2date(-3)).eq(date(-1,8));
        check(LEAP.day2date(-4)).eq(date(-1,7));
        check(LEAP.day2date(-5)).eq(date(-1,6));
        check(LEAP.day2date(-6)).eq(date(-1,5));
        check(LEAP.day2date(-7)).eq(date(-1,4));
        check(LEAP.day2date(-8)).eq(date(-1,3));
        check(LEAP.day2date(-9)).eq(date(-1,2));
        check(LEAP.day2date(-10)).eq(date(-1,1));
        check(LEAP.day2date(-11)).eq(date(-2,10));
    }

    @Test
    public void testDateToDay_LEAP() {
        for (int i = -70; i <= 70; i++) {
            var date = LEAP.day2date(i);
            check(LEAP.date2day(date)).eq(i);
        }

        // Exception
        checkThrows(() -> LEAP.date2day(date(0, 0)))
            .containsString("dayOfYear out of range for year 0: 0");
        checkThrows(() -> LEAP.date2day(date(0, -1)))
            .containsString("dayOfYear out of range for year 0: -1");
        checkThrows(() -> LEAP.date2day(date(0, 12)))
            .containsString("dayOfYear out of range for year 0: 12");
    }

    private FundamentalDate date(int year, int day) {
        return new FundamentalDate(year, day);
    }
}
