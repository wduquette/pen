package pen.calendars.formatter;

import pen.calendars.Date;
import pen.calendars.Form;

public sealed interface DateComponent permits
    DateComponent.DayOfMonth,
    DateComponent.DayOfYear,
    DateComponent.EraName,
    DateComponent.MonthName,
    DateComponent.MonthNumber,
    DateComponent.Text,
    DateComponent.YearNumber,
    DateComponent.Weekday
{
    /**
     * Format a particular component of a Date.
     * @param date The date
     * @return The formatted string.
     */
    String format(Date date);

    //-------------------------------------------------------------------------
    // Component types

    record DayOfMonth(int digits) implements DateComponent {
        public String format(Date date) {
            return zeroPad(date.dayOfMonth(), digits);
        }
    }

    record DayOfYear(int digits) implements DateComponent {
        public String format(Date date) {
            return zeroPad(date.dayOfYear(), digits);
        }
    }

    record EraName(Form form) implements DateComponent {
        public String format(Date date) {
            return date.era().getForm(form);
        }
    }

    record MonthName(Form form) implements DateComponent {
        public String format(Date date) {
            return date.month().getForm(form);
        }
    }

    record MonthNumber(int digits) implements DateComponent {
        public String format(Date date) {
            return zeroPad(date.monthOfYear(), digits);
        }
    }

    record Text(String text) implements DateComponent {
        public String format(Date date) {
            return text;
        }
    }

    record Weekday(Form form) implements DateComponent {
        public String format(Date date) {
            return date.weekday().getForm(form);
        }
    }

    record YearNumber(int digits) implements DateComponent {
        public String format(Date date) {
            return zeroPad(date.year(), digits);
        }
    }

    //-------------------------------------------------------------------------
    // Helpers

    private static String zeroPad(int number, int width) {
        return pad(Integer.toString(Math.abs(number)), "0", width);

    }

    private static String pad(String text, String padChar, int width) {
        return text.length() >= width
            ? text
            : padChar.repeat(width - text.length()) + text;
    }
}
