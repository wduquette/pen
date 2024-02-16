package pen.calendars;

import java.util.function.Function;

/**
 * A function that returns a number of days given a year number in the
 * given calendar.  YearDelta is used to compute the number of days in
 * a year, the number days in a month, etc., based on the year.
 */
public interface YearDelta extends Function<Integer,Integer> {
}
