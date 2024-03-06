package pen.calendars;

/**
 * The standard Armorican Calendars
 */
public class Armorican {
    private Armorican() {} // Not instantiable.

    public static final Week WEEK = new Week(StandardWeek.DAYS, 1);
    public static final BasicCalendar ME = new BasicCalendar.Builder()
        .era(new Era("ME", "Modern Era"))
        .priorEra(new Era("BME", "Before Modern Era"))
        .epochOffset(-978 * 366)
        .month(StandardMonth.JANUARY, 31)
        .month(StandardMonth.FEBRUARY, 28)
        .month(StandardMonth.MARCH, 31)
        .month(StandardMonth.APRIL, 30)
        .month(StandardMonth.MAY, 31)
        .month(StandardMonth.JUNE, 30)
        .month(StandardMonth.JULY, 31)
        .month(StandardMonth.AUGUST, 31)
        .month(StandardMonth.SEPTEMBER, 30)
        .month(StandardMonth.OCTOBER, 31)
        .month(StandardMonth.NOVEMBER, 31)
        .month(StandardMonth.DECEMBER, 31)
        .week(WEEK)
        .build();

    // TODO: Provide French weeks and months
    public static final BasicCalendar AF = new BasicCalendar.Builder()
        .era(new Era("AF", "After Founding"))
        .priorEra(new Era("BF", "Before Founding"))
        .month(StandardMonth.JANUARY, 31)
        .month(StandardMonth.FEBRUARY, 28)
        .month(StandardMonth.MARCH, 31)
        .month(StandardMonth.APRIL, 30)
        .month(StandardMonth.MAY, 31)
        .month(StandardMonth.JUNE, 30)
        .month(StandardMonth.JULY, 31)
        .month(StandardMonth.AUGUST, 31)
        .month(StandardMonth.SEPTEMBER, 30)
        .month(StandardMonth.OCTOBER, 31)
        .month(StandardMonth.NOVEMBER, 31)
        .month(StandardMonth.DECEMBER, 31)
        .week(WEEK)
        .build();
}
