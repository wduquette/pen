package pen.calendars.formatter;

import org.junit.Test;
import pen.calendars.*;

import static pen.checker.Checker.check;

public class DateFormatterTest {
    private final Calendar GREG = Gregorian.CALENDAR;
    private final Date ad = GREG.date(2024, 2, 20);
    private final Date bc = GREG.date(-44, 3, 15);
    private final int adDay = GREG.date2day(ad);
    private final YearDay adYearDay = GREG.day2yearDay(adDay);
    private final DateFormatter NUMERIC =
        new DateFormatter("yyyy'-'mm'-'dd' 'E");
    private final DateFormatter FANCY =
        new DateFormatter("WWWW', 'MMMM' 'd', 'y' 'E");
    private final DateFormatter YEARDAY =
        new DateFormatter("yyyy/DDD");
    private final Calendar TRIVIAL = new TrivialCalendar.Builder()
        .yearLength(365)
        .build();

    @Test
    public void testCompatibility() {
        check(NUMERIC.needsMonths()).eq(true);
        check(NUMERIC.needsWeeks()).eq(false);
        check(NUMERIC.isCompatibleWith(GREG)).eq(true);
        check(NUMERIC.isCompatibleWith(TRIVIAL)).eq(false);

        check(FANCY.needsMonths()).eq(true);
        check(FANCY.needsWeeks()).eq(true);
        check(FANCY.isCompatibleWith(GREG)).eq(true);
        check(FANCY.isCompatibleWith(TRIVIAL)).eq(false);

        check(YEARDAY.needsMonths()).eq(false);
        check(YEARDAY.needsWeeks()).eq(false);
        check(YEARDAY.isCompatibleWith(GREG)).eq(true);
        check(YEARDAY.isCompatibleWith(TRIVIAL)).eq(true);
    }

    @Test
    public void testFormatDate() {
        check(NUMERIC.format(GREG, ad)).eq("2024-02-20 AD");
        check(NUMERIC.format(GREG, bc)).eq("0044-03-15 BC");
        check(FANCY.format(GREG, ad)).eq("Tuesday, FEBRUARY 20, 2024 AD");
        check(FANCY.format(GREG, bc)).eq("Friday, MARCH 15, 44 BC");
        check(YEARDAY.format(GREG, ad)).eq("2024/051");
    }

    @Test
    public void testFormatDay() {
        check(NUMERIC.format(GREG, adDay)).eq("2024-02-20 AD");
        check(YEARDAY.format(GREG, adDay)).eq("2024/051");
    }

    @Test
    public void testFormatYearDay() {
        check(NUMERIC.format(GREG, adYearDay)).eq("2024-02-20 AD");
        check(YEARDAY.format(GREG, adYearDay)).eq("2024/051");
    }
}
