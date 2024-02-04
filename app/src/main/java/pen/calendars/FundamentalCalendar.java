package pen.calendars;

/**
 * Defines a fundamental calendar.  The name is typically a short symbol,
 * e.g., "FC".  The {@code yearLength} is a function that determines the
 * length of a year in days given the year number.  The epoch is year 0; it
 * is followed by year 1 and preceded by year -1.  The days of the year are
 * numbered 1 to {@code yearLength}.
 * @param name The calendar's symbol
 * @param yearLength Function to compute the length of a year in days.
 */
public record FundamentalCalendar(String name, YearLength yearLength) {
    /**
     * Converts an arbitrary day since the epoch to a date.
     * @param day The day
     * @return The date
     */
    FundamentalDate day2date(int day) {
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
    int date2day(FundamentalDate date) {
        // FIRST, validate the dayOfYear.
        if (date.dayOfYear() < 1 ||
            date.dayOfYear() > yearLength.apply(date.year()))
        {
            throw new CalendarException("dayOfYear out of range for year " +
                date.year() + ": " + date.dayOfYear());
        }

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

//    String formatDate(int day) {
//
//    }
//
//    String formatDate(FundamentalDate date) {
//
//    }
//
//    int parseDate(String dateString) {
//
//    }
}
