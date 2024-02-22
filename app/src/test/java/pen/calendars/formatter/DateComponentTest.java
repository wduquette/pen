package pen.calendars.formatter;

import org.junit.Test;
import pen.calendars.Form;

import static pen.calendars.Gregorian.CALENDAR;

import static pen.calendars.formatter.DateComponent.*;
import static pen.checker.Checker.check;

public class DateComponentTest {
    @Test
    public void testDayOfMonth() {
        var today = CALENDAR.date(2024,2,6);

        check(new DayOfMonth(0).format(today)).eq("6");
        check(new DayOfMonth(1).format(today)).eq("6");
        check(new DayOfMonth(2).format(today)).eq("06");
        check(new DayOfMonth(3).format(today)).eq("006");
    }

    @Test
    public void testDayOfYear() {
        var today = CALENDAR.date(2024,2,6);

        check(new DayOfYear(0).format(today)).eq("37");
        check(new DayOfYear(1).format(today)).eq("37");
        check(new DayOfYear(2).format(today)).eq("37");
        check(new DayOfYear(3).format(today)).eq("037");
    }

    @Test
    public void testEra() {
        var ad = CALENDAR.date(2024,2,6);

        // TODO Should be "Anno Domini", "AD", "AD"
        check(new EraName(Form.SHORT).format(ad)).eq("AD");
    }

    @Test
    public void testMonthName() {
        var ad = CALENDAR.date(2024,2,6);

        // TODO Should be "February", "Feb", "Feb", "F"
        check(new MonthName(Form.FULL).format(ad)).eq("FEBRUARY");
        check(new MonthName(Form.SHORT).format(ad)).eq("FEB");
        check(new MonthName(Form.UNAMBIGUOUS).format(ad)).eq("FEB");
        check(new MonthName(Form.TINY).format(ad)).eq("F");
    }

    @Test
    public void testMonthNumber() {
        var ad = CALENDAR.date(2024,2,6);

        check(new MonthNumber(0).format(ad)).eq("2");
        check(new MonthNumber(1).format(ad)).eq("2");
        check(new MonthNumber(2).format(ad)).eq("02");
        check(new MonthNumber(3).format(ad)).eq("002");
    }

    @Test
    public void testText() {
        var ad = CALENDAR.date(2024,2,6);

        check(new Text("XYZ").format(ad)).eq("XYZ");
    }

    @Test
    public void testWeekday() {
        var ad = CALENDAR.date(2024,2,6);

        // TODO: Should be "Tuesday", "Tue", "Tu", "T"
        check(new Weekday(Form.FULL).format(ad)).eq("TUESDAY");
        check(new Weekday(Form.SHORT).format(ad)).eq("TUE");
        check(new Weekday(Form.UNAMBIGUOUS).format(ad)).eq("TU");
        check(new Weekday(Form.TINY).format(ad)).eq("T");
    }

    @Test
    public void testYearNumber() {
        var early = CALENDAR.date(600,1,1);
        var today = CALENDAR.date(2024,2,6);

        check(new YearNumber(0).format(early)).eq("600");
        check(new YearNumber(1).format(early)).eq("600");
        check(new YearNumber(2).format(early)).eq("600");
        check(new YearNumber(3).format(early)).eq("600");
        check(new YearNumber(4).format(early)).eq("0600");

        check(new YearNumber(0).format(today)).eq("2024");
        check(new YearNumber(1).format(today)).eq("2024");
        check(new YearNumber(2).format(today)).eq("2024");
        check(new YearNumber(3).format(today)).eq("2024");
        check(new YearNumber(4).format(today)).eq("2024");
    }
}
