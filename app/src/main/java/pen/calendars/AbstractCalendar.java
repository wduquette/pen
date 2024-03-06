package pen.calendars;

public abstract class AbstractCalendar implements Calendar {
    //-------------------------------------------------------------------------
    // Instance Variables

    // The era symbol for positive years.
    private final Era era;

    // The era symbol for negative years
    private final Era priorEra;

    //-------------------------------------------------------------------------
    // Constructor

    public AbstractCalendar(
        Era era,
        Era priorEra
    ) {
        this.era = era;
        this.priorEra = priorEra;
    }

    //-------------------------------------------------------------------------
    // Calendar API

    @Override
    public Era era() {
        return era;
    }

    @Override
    public Era priorEra() {
        return priorEra;
    }

}
