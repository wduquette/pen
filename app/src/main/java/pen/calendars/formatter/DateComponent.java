package pen.calendars.formatter;

import pen.calendars.Date;

public sealed interface DateComponent permits
    DateComponent.DayOfMonth,
    DateComponent.Era,
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

    record Era(Form form) implements DateComponent {
        public String format(Date date) {
            // TODO: Define Era class, and pick string based on Form
            return date.era();
        }
    }

    record MonthName(Form form) implements DateComponent {
        public String format(Date date) {
            // TODO Probably just call month.toForm(form);
            return switch (form) {
                case TINY -> date.month().narrowForm();
                case SHORT,UNAMBIGUOUS -> date.month().shortForm();
                case FULL -> date.month().fullForm();
            };
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
            // TODO Probably just call weekday.toForm(form);
            return switch (form) {
                case TINY, UNAMBIGUOUS -> date.weekday().narrowForm();
                case SHORT -> date.weekday().shortForm();
                case FULL -> date.weekday().fullForm();
            };
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
