package pen.calendars.formatter;

import org.junit.Test;
import pen.calendars.*;

import static pen.checker.Checker.check;

public class DateFormatTest {
    private final Calendar GREG = Gregorian.CALENDAR;
    private final Date ad = GREG.date(2024, 2, 20);
    private final Date bc = GREG.date(-44, 3, 15);
    private final int adDay = GREG.date2day(ad);
    private final YearDay adYearDay = GREG.day2yearDay(adDay);
    private final DateFormat NUMERIC =
        new DateFormat("yyyy'-'mm'-'dd' 'E");
    private final DateFormat FANCY =
        new DateFormat("WWWW', 'MMMM' 'd', 'y' 'E");
    private final DateFormat YEARDAY =
        new DateFormat("yyyy/DDD");
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
        check(GREG.format(NUMERIC, ad)).eq("2024-02-20 AD");
        check(GREG.format(NUMERIC, bc)).eq("0044-03-15 BC");
        check(GREG.format(FANCY, ad)).eq("Tuesday, February 20, 2024 AD");
        check(GREG.format(FANCY, bc)).eq("Friday, March 15, 44 BC");
        check(GREG.format(YEARDAY, ad)).eq("2024/051");
    }

    @Test
    public void testFormatDay() {
        check(GREG.format(NUMERIC, adDay)).eq("2024-02-20 AD");
        check(GREG.format(YEARDAY, adDay)).eq("2024/051");
    }

    @Test
    public void testFormatYearDay() {
        check(GREG.format(NUMERIC, adYearDay)).eq("2024-02-20 AD");
        check(GREG.format(YEARDAY, adYearDay)).eq("2024/051");
    }

    @Test
    public void testParseDate() {
        check(date(GREG.parse(NUMERIC, "2024-02-20 AD"))).eq(ad);
        check(date(GREG.parse(NUMERIC, "0044-03-15 BC"))).eq(bc);
        check(date(GREG.parse(FANCY, "Tuesday, February 20, 2024 AD"))).eq(ad);
        check(date(GREG.parse(FANCY, "Friday, March 15, 44 BC"))).eq(bc);
        check(date(GREG.parse(YEARDAY, "2024/051"))).eq(ad);
    }

    private Date date(int day) { return GREG.day2date(day); }
}
