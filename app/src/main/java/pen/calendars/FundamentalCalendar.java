package pen.calendars;

/**
 * Defines a fundamental calendar.  The name is typically a short symbol,
 * e.g., "FC".  The {@code yearLength} is a function that determines the
 * length of a year in days given the year number.  The epoch is year 0; it
 * is followed by year 1 and preceded by year -1.  The days of the year are
 * numbered 1 to {@code yearLength}.
 * @param symbol The epoch symbol for non-negative years
 * @param symbol The epoch symbol for negative years
 * @param yearLength Function to compute the length of a year in days.
 * @param dayOfYearDigits Number of for dayOfYear in formatted dates
 */
public record FundamentalCalendar(
    String symbol,
    String beforeSymbol,
    YearLength yearLength,
    int dayOfYearDigits
) {
    /**
     * Converts an arbitrary day since the epoch to a date.
     * @param day The day
     * @return The date
     */
    public FundamentalDate day2date(int day) {
        if (day >= 0) {
            int year = 0;
            var daysInYear = yearLength.apply(year);

            while (day >= daysInYear) {
                year++;
                day -= daysInYear;
            }

            return new FundamentalDate(year, day + 1);
        } else {
            int year = -1;
            day = -day;

            var daysInYear = yearLength.apply(year);

            while (day > daysInYear) {
                year--;
                day -= daysInYear;
            }

            var daysInEarliestYear = yearLength.apply(year - 1);
            var dayOfYear = daysInEarliestYear - day + 1;
            return new FundamentalDate(year, dayOfYear);
        }
    }

    /**
     * Converts an arbitrary date to the day since the epoch.
     * @param date The date
     * @return The day
     * @throws CalendarException if the date is invalid.
     */
    public int date2day(FundamentalDate date) {
        // FIRST, validate the dayOfYear.
        validate(date);

        // NEXT, non-negative years, then negative years
        if (date.year() >= 0) {
            var year = date.year() - 1;
            var day = date.dayOfYear() - 1;

            while (year >= 0) {
                day += yearLength.apply(year);
                year--;
            }

            return day;
        } else {
            var day = yearLength.apply(date.year()) - date.dayOfYear() + 1;
            var year = date.year() + 1;

            while (year < 0) {
                day += yearLength.apply(year);
                year++;
            }

            return -day;
        }
    }

    /**
     * Returns the string "{name}{year}/{dayOfYear} for positive years and
     * "B{name}{-year}/{dayOfYear}" for negative years.
     * @param day The day
     * @return The formatted string
     */
    public String formatDate(int day) {
        return formatDate(day2date(day));
    }

    /**
     * Returns the string "{symbol}{year}/{dayOfYear}" for positive years and
     * "{beforeSymbol}{-year}/{dayOfYear}" for negative years.
     * @param date The date
     * @return The formatted string
     */
    public String formatDate(FundamentalDate date) {
        validate(date);

        var sym = (date.year() >= 0) ? symbol : beforeSymbol;
        var year = Math.abs(date.year());
        var dayOfYear = String.format("%0" + dayOfYearDigits + "d",
            date.dayOfYear());

        return sym + year + "/" + dayOfYear;
    }

    /**
     * Validates that the date is a valid date.
     * @param date The date
     * @throws CalendarException if the date is invalid.
     */
    public void validate(FundamentalDate date) {
        if (date.dayOfYear() < 1 ||
            date.dayOfYear() > yearLength.apply(date.year()))
        {
            throw new CalendarException("dayOfYear out of range for year " +
                date.year() + ": " + date.dayOfYear());
        }
    }

//
//    int parseDate(String dateString) {
//
//    }

    public String toString() {
        return "FundamentalCalendar[" + symbol + "," + beforeSymbol + "]";
    }
}
