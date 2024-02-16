package pen.calendars;

import org.junit.Test;

import static pen.checker.Checker.check;
import static pen.checker.Checker.checkThrows;

public class FundamentalCalendarTest {
    // A calendar with 10 day "years"
    private static final FundamentalCalendar TEN =
        new FundamentalCalendar.Builder()
            .era("AT")
            .priorEra("BT")
            .yearLength(10)
            .dayOfYearDigits(2)
//            .week()
            .build();

    // A calendar with 10 day "years" plus a leap year every fourth year
    private static final FundamentalCalendar LEAP =
        new FundamentalCalendar.Builder()
            .era("AL")
            .priorEra("BL")
            .yearLength(y -> (y % 4) == 0 ? 11 : 10)
            .dayOfYearDigits(2)
//            .week()
            .build();

    @Test
    public void testDay2date_TEN() {
        // Positive days
        check(TEN.day2date(0)).eq(ten(1,1));
        check(TEN.day2date(1)).eq(ten(1,2));
        check(TEN.day2date(9)).eq(ten(1,10));
        check(TEN.day2date(10)).eq(ten(2,1));
        check(TEN.day2date(11)).eq(ten(2,2));

        // Negative days
        check(TEN.day2date(-1)).eq(ten(-1,10));
        check(TEN.day2date(-2)).eq(ten(-1,9));
        check(TEN.day2date(-3)).eq(ten(-1,8));
        check(TEN.day2date(-4)).eq(ten(-1,7));
        check(TEN.day2date(-5)).eq(ten(-1,6));
        check(TEN.day2date(-6)).eq(ten(-1,5));
        check(TEN.day2date(-7)).eq(ten(-1,4));
        check(TEN.day2date(-8)).eq(ten(-1,3));
        check(TEN.day2date(-9)).eq(ten(-1,2));
        check(TEN.day2date(-10)).eq(ten(-1,1));
        check(TEN.day2date(-11)).eq(ten(-2,10));
    }

    @Test
    public void testDateToDay_TEN() {
        for (int i = -25; i <= 25; i++) {
            var date = TEN.day2date(i);
//            System.out.printf("%3d %-8s %3d\n", i, date, TEN.date2day(date));
            check(TEN.date2day(date)).eq(i);
        }

        // Exception
        checkThrows(() -> TEN.date2day(ten(0, 0)));
    }

    @Test
    public void testValidate_TEN() {
        // OK
        for (var day = -25; day <= 251; day++) {
            TEN.validate(TEN.day2date(day));
        }

        // Exception
        checkThrows(() -> TEN.validate(leap(1,1)))
            .containsString("expected \"FundamentalCalendar[AT,BT]\", got \"FundamentalCalendar[AL,BL]");
        checkThrows(() -> TEN.validate(ten(0, 0)))
            .containsString("year is 0 in date");
        checkThrows(() -> TEN.validate(ten(1, 0)))
            .containsString("dayOfYear out of range for year 1 in date:");
        checkThrows(() -> TEN.validate(ten(1, -1)))
            .containsString("dayOfYear out of range for year 1 in date:");
        checkThrows(() -> TEN.validate(ten(1, 11)))
            .containsString("dayOfYear out of range for year 1 in date:");
    }

    @Test
    public void testDate2stringTEN() {
        // Positive dates
        check(TEN.date2string(ten(1,1))).eq("AT1-01");
        check(TEN.date2string(ten(1,2))).eq("AT1-02");
        check(TEN.date2string(ten(1,10))).eq("AT1-10");
        check(TEN.date2string(ten(2,1))).eq("AT2-01");
        check(TEN.date2string(ten(2,2))).eq("AT2-02");

        // Negative dates
        check(TEN.date2string(ten(-1,10))).eq("BT1-10");
        check(TEN.date2string(ten(-1,9))).eq("BT1-09");
        check(TEN.date2string(ten(-1,1))).eq("BT1-01");
        check(TEN.date2string(ten(-2,10))).eq("BT2-10");

        // From day
        for (int day = -101; day <= 101; day++) {
            var date = TEN.day2date(day);
            check(TEN.formatDate(day)).eq(TEN.date2string(date));
        }

        // Exception
        checkThrows(() -> TEN.date2string(ten(1, 0)));
    }

    @Test
    public void testParseDate_TEN() {
        // Positive dates
        check(TEN.parseDate("AT1-01")).eq(0);
        check(TEN.parseDate("AT1-02")).eq(1);
        check(TEN.parseDate("AT1-10")).eq(9);
        check(TEN.parseDate("AT2-01")).eq(10);
        check(TEN.parseDate("AT2-02")).eq(11);

        // Negative dates
        check(TEN.parseDate("BT1-10")).eq(-1);
        check(TEN.parseDate("BT1-09")).eq(-2);
        check(TEN.parseDate("BT1-01")).eq(-10);
        check(TEN.parseDate("BT2-10")).eq(-11);

        // Errors
        checkThrows(() -> TEN.parseDate("ABC1-01"))
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
        check(LEAP.daysInYear(-4)).eq(10);
        check(LEAP.daysInYear(-3)).eq(10);
        check(LEAP.daysInYear(-2)).eq(10);
        check(LEAP.daysInYear(-1)).eq(11);
        check(LEAP.daysInYear(1)).eq(10);
        check(LEAP.daysInYear(2)).eq(10);
        check(LEAP.daysInYear(3)).eq(10);
        check(LEAP.daysInYear(4)).eq(11);

        checkThrows(() -> LEAP.daysInYear(0));
    }

    @Test
    public void testDay2date_LEAP() {
        // Positive days
        check(LEAP.day2date(0)).eq(leap(1,1));
        check(LEAP.day2date(1)).eq(leap(1,2));
        check(LEAP.day2date(9)).eq(leap(1,10));
        check(LEAP.day2date(10)).eq(leap(2,1));
        check(LEAP.day2date(11)).eq(leap(2,2));
        check(LEAP.day2date(20)).eq(leap(3,1));
        check(LEAP.day2date(21)).eq(leap(3,2));

        // Negative days
        check(LEAP.day2date(-1)).eq(leap(-1,11));
        check(LEAP.day2date(-2)).eq(leap(-1,10));
        check(LEAP.day2date(-3)).eq(leap(-1,9));
        check(LEAP.day2date(-4)).eq(leap(-1,8));
        check(LEAP.day2date(-5)).eq(leap(-1,7));
        check(LEAP.day2date(-6)).eq(leap(-1,6));
        check(LEAP.day2date(-7)).eq(leap(-1,5));
        check(LEAP.day2date(-8)).eq(leap(-1,4));
        check(LEAP.day2date(-9)).eq(leap(-1,3));
        check(LEAP.day2date(-10)).eq(leap(-1,2));
        check(LEAP.day2date(-11)).eq(leap(-1,1));
        check(LEAP.day2date(-12)).eq(leap(-2,10));
    }

    @Test
    public void testDateToDay_LEAP() {
        for (int i = -70; i <= 70; i++) {
            var date = LEAP.day2date(i);
            check(LEAP.date2day(date)).eq(i);
        }

        // Exception
        checkThrows(() -> LEAP.date2day(leap(0, 0)));
    }

    private YearDayOfYear ten(int year, int day) {
        return new YearDayOfYear(TEN, year, day);
    }

    private YearDayOfYear leap(int year, int day) {
        return new YearDayOfYear(LEAP, year, day);
    }
}
