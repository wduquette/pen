package pen.calendars;

import java.util.List;

@SuppressWarnings("unused")
public class StandardMonth {
    private StandardMonth() {} // Not instantiable

    public static final Month JANUARY =
        new Month("January", "Jan", "Jan", "J");
    public static final Month FEBRUARY =
        new Month("February", "Feb", "Feb", "F");
    public static final Month MARCH =
        new Month("March", "Mar", "Mar", "M");
    public static final Month APRIL =
        new Month("April", "Apr", "Apr", "A");
    public static final Month MAY =
        new Month("May", "May", "May", "M");
    public static final Month JUNE =
        new Month("June", "Jun", "Jun", "J");
    public static final Month JULY =
        new Month("July", "Jul", "Jul", "J");
    public static final Month AUGUST =
        new Month("August", "Aug", "Aug", "A");
    public static final Month SEPTEMBER =
        new Month("September", "Sep", "Sep", "S");
    public static final Month OCTOBER =
        new Month("October", "Oct", "Oct", "O");
    public static final Month NOVEMBER =
        new Month("November", "Nov", "Nov", "N");
    public static final Month DECEMBER =
        new Month("December", "Dec", "Dec", "D");

    /**
     * An unmodifiable list of the standard months.
     */
    public static final List<Month> MONTHS = List.of(
        JANUARY, FEBRUARY, MARCH, APRIL, MAY, JUNE,
        JULY, AUGUST, SEPTEMBER, OCTOBER, NOVEMBER, DECEMBER
    );
}
