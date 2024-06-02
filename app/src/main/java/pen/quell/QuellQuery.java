package pen.quell;

import java.util.function.Predicate;

/**
 * A QuellQuery is a query pipeline created by the Quell.query() call.  At
 * present, I'm focusing on the API rather than the implementation; there's
 * no reason to think this will be particular efficient.
 */
public class QuellQuery {
    //-------------------------------------------------------------------------
    // Types

    public interface Row2ValueMapper {
        Object map(QuellRow row);
    }

    public interface RowUpdater {
        void update(QuellRow row);
    }

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

    /**
     * Given a column name, adds a new column whose value is produced by the
     * mapper.
     * @param name The column name
     * @param mapper The mapping function from row to column value.
     * @return The updated query.
     */
    public QuellQuery addColumn(String name, Class<?> type, Row2ValueMapper mapper) {
        var mapped = QuellTable.withShape(result);
        if (mapped.getColumnNames().contains(name)) {
            throw new IllegalArgumentException("Column already exists: " + name);
        }

        mapped.addColumn(name, type);

        for (int i = 0; i < result.size(); i++) {
            var row = result.get(i);
            row.add(name, mapper.map(row));
            mapped.add(row);
        }

        return new QuellQuery(mapped);
    }

    /**
     * Given an update function, updates each row's column values.  The
     * mapper is not allowed to add new columns.
     * @param updater A function to update the row's values.
     * @return The updated query.
     */
    public QuellQuery update(RowUpdater updater) {
        var mapped = QuellTable.withShape(result);

        for (int i = 0; i < result.size(); i++) {
            var row = result.get(i);
            updater.update(row);
            mapped.add(row);
        }

        return new QuellQuery(mapped);
    }

    /**
     * Dumps the current query result to System.out, as an aid to debugging
     * the pipeline.
     * @param tag A tag to indicate where in the pipeline the dump occurred.
     * @return The query
     */
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
