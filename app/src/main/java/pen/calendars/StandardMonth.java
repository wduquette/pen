package pen.calendars;

import java.util.List;

@SuppressWarnings("unused")
public class StandardMonth {
    private StandardMonth() {} // Not instantiable

    public static final Month JANUARY =
        new SimpleMonth("January", "Jan", "Jan", "J");
    public static final Month FEBRUARY =
        new SimpleMonth("February", "Feb", "Feb", "F");
    public static final Month MARCH =
        new SimpleMonth("March", "Mar", "Mar", "M");
    public static final Month APRIL =
        new SimpleMonth("April", "Apr", "Apr", "A");
    public static final Month MAY =
        new SimpleMonth("May", "May", "May", "M");
    public static final Month JUNE =
        new SimpleMonth("June", "Jun", "Jun", "J");
    public static final Month JULY =
        new SimpleMonth("July", "Jul", "Jul", "J");
    public static final Month AUGUST =
        new SimpleMonth("August", "Aug", "Aug", "A");
    public static final Month SEPTEMBER =
        new SimpleMonth("September", "Sep", "Sep", "S");
    public static final Month OCTOBER =
        new SimpleMonth("October", "Oct", "Oct", "O");
    public static final Month NOVEMBER =
        new SimpleMonth("November", "Nov", "Nov", "N");
    public static final Month DECEMBER =
        new SimpleMonth("December", "Dec", "Dec", "D");

    /**
     * An unmodifiable list of the standard months.
     */
    public static final List<Month> MONTHS = List.of(
        JANUARY, FEBRUARY, MARCH, APRIL, MAY, JUNE,
        JULY, AUGUST, SEPTEMBER, OCTOBER, NOVEMBER, DECEMBER
    );
}
