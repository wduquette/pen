package pen.calendars.formatter;

import org.junit.Test;
import pen.calendars.Date;

import static pen.calendars.Gregorian.CALENDAR;
import static pen.checker.Checker.check;

public class DateFormatterTest {
    private final Date ad = CALENDAR.date(2024, 2, 20);
    private final Date bc = CALENDAR.date(-44, 3, 15);
    private final DateFormatter NUMERIC =
        DateFormatter.define("yyyy'-'mm'-'dd' 'E");
    private final DateFormatter FANCY =
        DateFormatter.define("WWWW', 'MMMM' 'd', 'y' 'E");

    @Test
    public void testFormat() {
        check(NUMERIC.format(ad)).eq("2024-02-20 AD");
        check(NUMERIC.format(bc)).eq("0044-03-15 BC");
        check(FANCY.format(ad)).eq("TUESDAY, FEBRUARY 20, 2024 AD");
        check(FANCY.format(bc)).eq("FRIDAY, MARCH 15, 44 BC");
    }

}
