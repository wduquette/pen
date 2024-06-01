package pen.quell;

import java.util.function.Predicate;

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

    //-------------------------------------------------------------------------
    // Pipeline methods

    /**
     * Produces a QuellQuery containing a table with only matching results.
     * @param predicate The predicate
     * @return The filtered query.
     */
    public QuellQuery filter(Predicate<QuellRow> predicate) {
        var filtered = QuellTable.withShape(result);
        for (int i = 0; i < result.size(); i++) {
            var row = result.get(i);
            if (predicate.test(row)) {
                filtered.add(row);
            }
        }

        return new QuellQuery(filtered);
    }

    public QuellQuery dump(String tag) {
        System.out.println("Dump: " + tag);
        for (int i = 0; i < result.size(); i++) {
            var row = result.get(i);
            System.out.println("  [" + i + "] " + row);
        }
        return this;
    }

    //-------------------------------------------------------------------------
    // Accessors

    public QuellTable result() {
        return result;
    }
}
