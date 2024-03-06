package pen.calendars;

import org.junit.Test;

import static pen.checker.Checker.check;
import static pen.checker.Checker.checkThrows;

public class TrivialCalendarTest {
    private static final YearDelta TEN_DAYS = dummy -> 10;
    private static final YearDelta LEAP_DAYS = y -> (y % 4) == 0 ? 11 : 10;
    private static final Week WEEK = new Week(StandardWeek.DAYS, 0);

    // A calendar with 10 day "years"
    private static final TrivialCalendar TEN =
        new TrivialCalendar.Builder()
            .yearLength(TEN_DAYS)
            .week(WEEK)
            .build();

    // A calendar with 10 day "years" plus a leap year every fourth year
    private static final TrivialCalendar LEAP =
        new TrivialCalendar.Builder()
            .yearLength(LEAP_DAYS)
            .week(WEEK)
            .build();

    @Test
    public void testBasics() {
        check(TEN.era()).eq(Calendar.AFTER_EPOCH);
        check(TEN.priorEra()).eq(Calendar.BEFORE_EPOCH);
        check(TEN.yearLength()).eq(TEN_DAYS);
        check(TEN.week()).eq(WEEK);

        check(LEAP.yearLength()).eq(LEAP_DAYS);
        check(LEAP.week()).eq(WEEK);

        // It's a pass-through; just spot check.
        check(TEN.week()).ne(null);
        check(TEN.hasWeeks()).eq(true);

        check(TEN.day2weekday(0)).eq(StandardWeek.SUNDAY);
        check(TEN.day2weekday(1)).eq(StandardWeek.MONDAY);
        check(TEN.day2weekday(7)).eq(StandardWeek.SUNDAY);
    }

    @Test
    public void testDay2date_TEN() {
        // Positive days
        check(TEN.day2yearDay(0)).eq(ten(1,1));
        check(TEN.day2yearDay(1)).eq(ten(1,2));
        check(TEN.day2yearDay(9)).eq(ten(1,10));
        check(TEN.day2yearDay(10)).eq(ten(2,1));
        check(TEN.day2yearDay(11)).eq(ten(2,2));

        // Negative days
        check(TEN.day2yearDay(-1)).eq(ten(-1,10));
        check(TEN.day2yearDay(-2)).eq(ten(-1,9));
        check(TEN.day2yearDay(-3)).eq(ten(-1,8));
        check(TEN.day2yearDay(-4)).eq(ten(-1,7));
        check(TEN.day2yearDay(-5)).eq(ten(-1,6));
        check(TEN.day2yearDay(-6)).eq(ten(-1,5));
        check(TEN.day2yearDay(-7)).eq(ten(-1,4));
        check(TEN.day2yearDay(-8)).eq(ten(-1,3));
        check(TEN.day2yearDay(-9)).eq(ten(-1,2));
        check(TEN.day2yearDay(-10)).eq(ten(-1,1));
        check(TEN.day2yearDay(-11)).eq(ten(-2,10));
    }

    @Test
    public void testDateToDay_TEN() {
        for (int i = -25; i <= 25; i++) {
            var date = TEN.day2yearDay(i);
//            System.out.printf("%3d %-8s %3d\n", i, date, TEN.date2day(date));
            check(TEN.yearDay2day(date)).eq(i);
        }

        // Exception
        checkThrows(() -> TEN.yearDay2day(ten(0, 0)));
    }

    @Test
    public void testValidate_TEN() {
        // OK
        for (var day = -25; day <= 251; day++) {
            TEN.validate(TEN.day2yearDay(day));
        }

        // Exception
        checkThrows(() -> TEN.validate(leap(1,1)))
            .containsString("mismatch");
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
        check(LEAP.day2yearDay(0)).eq(leap(1,1));
        check(LEAP.day2yearDay(1)).eq(leap(1,2));
        check(LEAP.day2yearDay(9)).eq(leap(1,10));
        check(LEAP.day2yearDay(10)).eq(leap(2,1));
        check(LEAP.day2yearDay(11)).eq(leap(2,2));
        check(LEAP.day2yearDay(20)).eq(leap(3,1));
        check(LEAP.day2yearDay(21)).eq(leap(3,2));

        // Negative days
        check(LEAP.day2yearDay(-1)).eq(leap(-1,11));
        check(LEAP.day2yearDay(-2)).eq(leap(-1,10));
        check(LEAP.day2yearDay(-3)).eq(leap(-1,9));
        check(LEAP.day2yearDay(-4)).eq(leap(-1,8));
        check(LEAP.day2yearDay(-5)).eq(leap(-1,7));
        check(LEAP.day2yearDay(-6)).eq(leap(-1,6));
        check(LEAP.day2yearDay(-7)).eq(leap(-1,5));
        check(LEAP.day2yearDay(-8)).eq(leap(-1,4));
        check(LEAP.day2yearDay(-9)).eq(leap(-1,3));
        check(LEAP.day2yearDay(-10)).eq(leap(-1,2));
        check(LEAP.day2yearDay(-11)).eq(leap(-1,1));
        check(LEAP.day2yearDay(-12)).eq(leap(-2,10));
    }

    @Test
    public void testDateToDay_LEAP() {
        for (int i = -70; i <= 70; i++) {
            var date = LEAP.day2yearDay(i);
            check(LEAP.yearDay2day(date)).eq(i);
        }

        // Exception
        checkThrows(() -> LEAP.yearDay2day(leap(0, 0)));
    }

    @Test
    public void testFormatParse() {
        var yearDay = TEN.yearDay(2,5);

        check(TEN.format(yearDay)).eq("AE-2-5");
        check(TEN.parse("AE-2-5")).eq(TEN.yearDay2day(yearDay));
    }

    private YearDay ten(int year, int day) {
        return new YearDay(TEN, year, day);
    }

    private YearDay leap(int year, int day) {
        return new YearDay(LEAP, year, day);
    }
}
