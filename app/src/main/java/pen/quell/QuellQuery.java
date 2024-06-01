package pen.quell;

/**
 * A QuellQuery is a query pipeline created by the Quell.query() call.  At
 * present, I'm focusing on the API rather than the implementation; there's
 * no reason to think this will be particular efficient.
 */
public class QuellQuery {
    //-------------------------------------------------------------------------
    // Instance Variables

    // The current result set.
    private final QuellTable result;

    //-------------------------------------------------------------------------
    // Constructor

    /**
     * The query is created given a QuellTable produced by its creator.
     * @param table The table
     */
    QuellQuery(QuellTable table) {
        this.result = table;
    }

}
