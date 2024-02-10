package pen.calendars;

/**
 * Defines a fundamental calendar.  The name is typically a short symbol,
 * e.g., "FC".  The {@code yearLength} is a function that determines the
 * length of a year in days given the year number.  The epoch is year 1; it
 * is preceded by year -1.  The days of the year are
 * numbered 1 to {@code yearLength}.
 * @param era The era symbol for non-negative years
 * @param priorEra The era symbol for negative years
 * @param yearLength Function to compute the length of a year in days.
 * @param dayOfYearDigits Number of for dayOfYear in formatted dates
 */
public record FundamentalCalendar(
    String era,
    String priorEra,
    YearDelta yearLength,
    int dayOfYearDigits
) implements Calendar {
    //-------------------------------------------------------------------------
    // Calendar API

    /**
     * Returns the string "{era}{year}-{dayOfYear} for positive years and
     * "{priorEra}{-year}/{dayOfYear}" for negative years.
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
     * Returns the number of days in the given year.
     * @param year The year
     * @return The number of days
     */
    public int daysInYear(int year) {
        if (year > 0) {
            return yearLength.apply(year);
        } else if (year < 0) {
            return yearLength.apply(year + 1);
        } else {
             throw new IllegalArgumentException("year is 0");
        }
    }

    /**
     * Converts an arbitrary day since the epoch to a date.
     * @param day The day
     * @return The date
     */
    public YearDayOfYear day2date(int day) {
        if (day >= 0) {
            int year = 1;
            var daysInYear = daysInYear(year);

            while (day >= daysInYear) {
                day -= daysInYear;
                year++;
                daysInYear = daysInYear(year);
            }

            return new YearDayOfYear(year, day + 1);
        } else {
            int year = -1;
            day = -day;

            var daysInYear = daysInYear(year);

            while (day > daysInYear) {
                day -= daysInYear;
                year--;
                daysInYear = daysInYear(year);
            }

            var dayOfYear = daysInYear - day + 1;
            return new YearDayOfYear(year, dayOfYear);
        }
    }

    /**
     * Converts an arbitrary date to the day since the epoch.
     * @param date The date
     * @return The day
     * @throws CalendarException if the date is invalid.
     */
    public int date2day(YearDayOfYear date) {
        // FIRST, validate the dayOfYear.
        validate(date);

        // NEXT, positive years, then negative years
        if (date.year() > 0) {
            var day = date.dayOfYear() - 1;
            var year = date.year() - 1;

            while (year >= 1) {
                day += daysInYear(year);
                year--;
            }

            return day;
        } else {
            var day = daysInYear(date.year()) - date.dayOfYear() + 1;
            var year = date.year() + 1;

            while (year < 0) {
                day += daysInYear(year);
                year++;
            }

            return -day;
        }
    }

    /**
     * Returns the string "{era}{year}/{dayOfYear}" for positive years and
     * "{priorEra}{-year}/{dayOfYear}" for negative years.
     * @param date The date
     * @return The formatted string
     */
    public String date2string(YearDayOfYear date) {
        validate(date);

        var sym = (date.year() >= 0) ? era : priorEra;
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
    public YearDayOfYear string2date(String dateString) {
        dateString = dateString.trim().toUpperCase();

        // FIRST, get the symbol
        String sym;
        boolean isBefore = false;

        if (dateString.startsWith(era.toUpperCase())) {
            sym = era;
        } else if (dateString.startsWith(priorEra.toUpperCase())) {
            sym = priorEra;
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

            var date = new YearDayOfYear(
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
    public void validate(YearDayOfYear date) {
        if (date.year() == 0) {
            throw new CalendarException("year is 0 in date: \"" + date + "\".");
        }

        if (date.dayOfYear() < 1 ||
            date.dayOfYear() > daysInYear(date.year()))
        {
            throw new CalendarException("dayOfYear out of range for year " +
                date.year() + " in date: \"" + date + "\"");
        }
    }

    //-------------------------------------------------------------------------
    // Helpers

    private CalendarException badFormat(String dateString) {
        throw new CalendarException(
            "Invalid format, expected \"" +
                era + "|" + priorEra + "<year>-<dayOfYear>\": \"" +
            dateString + "\".");
    }

    public String toString() {
        return "FundamentalCalendar[" + era + "," + priorEra + "]";
    }
}
