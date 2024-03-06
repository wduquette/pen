package pen.calendars.formatter;

import pen.calendars.Form;

/**
 * Date field types.  Used by DateFormatter.
 */
public sealed interface DateField permits
    DateField.DayOfMonth,
    DateField.DayOfYear,
    DateField.EraName,
    DateField.MonthName,
    DateField.MonthNumber,
    DateField.Text,
    DateField.YearNumber,
    DateField.WeekdayName
{
    record DayOfMonth(int digits)  implements DateField { }
    record DayOfYear(int digits)   implements DateField { }
    record EraName(Form form)      implements DateField { }
    record MonthName(Form form)    implements DateField { }
    record MonthNumber(int digits) implements DateField { }
    record Text(String text)       implements DateField { }
    record WeekdayName(Form form)  implements DateField { }
    record YearNumber(int digits)  implements DateField { }
}
