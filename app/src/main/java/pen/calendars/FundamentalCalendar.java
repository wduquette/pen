package pen.calendars;

/**
 * Defines a fundamental calendar.  The name is typically a short symbol,
 * e.g., "FC".  The {@code yearLength} is a function that determines the
 * length of a year in days given the year number.  The epoch is year 0; it
 * is followed by year 1 and preceded by year -1.  The days of the year are
 * numbered 1 to {@code yearLength}.
 * @param symbol The epoch symbol for non-negative years
 * @param beforeSymbol The epoch symbol for negative years
 * @param yearLength Function to compute the length of a year in days.
 * @param dayOfYearDigits Number of for dayOfYear in formatted dates
 */
public record FundamentalCalendar(
    String symbol,
    String beforeSymbol,
    YearLength yearLength,
    int dayOfYearDigits
) implements Calendar {
    //-------------------------------------------------------------------------
    // Calendar API

    /**
     * Returns the string "{symbol}{year}-{dayOfYear} for positive years and
     * "{beforeSymbol}{-year}/{dayOfYear}" for negative years.
     * @param day The fundamental day
     * @return The formatted string
     */
    @Override
    public String formatDate(int day) {
        return date2string(day2date(day));
    }

    /**
     * Parses a date string into a fundamental day.
     * @param dateString the date string
     * @return The day
     * @throws CalendarException on parse error
     */
    @Override
    public int parseDate(String dateString) {
        return date2day(string2date(dateString));
    }

    //-------------------------------------------------------------------------
    // FundamentalCalendar conversions

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
     * Returns the string "{symbol}{year}/{dayOfYear}" for positive years and
     * "{beforeSymbol}{-year}/{dayOfYear}" for negative years.
     * @param date The date
     * @return The formatted string
     */
    public String date2string(FundamentalDate date) {
        validate(date);

        var sym = (date.year() >= 0) ? symbol : beforeSymbol;
        var year = Math.abs(date.year());
        var dayOfYear = String.format("%0" + dayOfYearDigits + "d",
            date.dayOfYear());

        return sym + year + "-" + dayOfYear;
    }

    /**
     * Parses a date string into a date
     * @param dateString the date string
     * @return The date
     * @throws CalendarException on parse error
     */
    public FundamentalDate string2date(String dateString) {
        dateString = dateString.trim().toUpperCase();

        // FIRST, get the symbol
        String sym;
        boolean isBefore = false;

        if (dateString.startsWith(symbol.toUpperCase())) {
            sym = symbol;
        } else if (dateString.startsWith(beforeSymbol.toUpperCase())) {
            sym = beforeSymbol;
            isBefore = true;
        } else {
            throw badFormat(dateString);
        }

        // NEXT, split on "-"
        var tokens = dateString.substring(sym.length()).split("-");

        if (tokens.length != 2) {
            throw badFormat(dateString);
        }

        try {
            var year = Integer.parseInt(tokens[0]);
            var dayOfYear = Integer.parseInt(tokens[1]);

            var date = new FundamentalDate(
                isBefore ? -year : year,
                dayOfYear);

            validate(date);
            return date;
        } catch (IllegalArgumentException ex) {
            throw badFormat(dateString);
        }
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

    //-------------------------------------------------------------------------
    // Helpers

    private CalendarException badFormat(String dateString) {
        throw new CalendarException(
            "Invalid format, expected \"" +
            symbol + "|" + beforeSymbol + "<year>-<dayOfYear>\": \"" +
            dateString + "\".");
    }

    public String toString() {
        return "FundamentalCalendar[" + symbol + "," + beforeSymbol + "]";
    }
}
