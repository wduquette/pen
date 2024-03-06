package pen.calendars;

public abstract class AbstractCalendar implements Calendar {
    //-------------------------------------------------------------------------
    // Instance Variables

    // The epoch day corresponding to day 1 of year 1 in this calendar.  This
    // is used to synchronize calendars in a setting.
    private final int epochOffset;

    // The era symbol for positive years.
    private final Era era;

    // The era symbol for negative years
    private final Era priorEra;



    //-------------------------------------------------------------------------
    // Constructor

    public AbstractCalendar(
        int epochOffset,
        Era era,
        Era priorEra
    ) {
        this.epochOffset = epochOffset;
        this.era = era;
        this.priorEra = priorEra;
    }

    //-------------------------------------------------------------------------
    // Calendar API: Metadata

    @Override
    public int epochOffset() {
        return epochOffset;
    }

    @Override
    public Era era() {
        return era;
    }

    @Override
    public Era priorEra() {
        return priorEra;
    }

}
