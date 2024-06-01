package pen.quell;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

/**
 * A QuellTable is a collection of QuellColumns, each containing a column of
 * data in a table.
 */
public class QuellTable {
    private final int size;
    private final TreeMap<String, QuellColumn> columns = new TreeMap<>();

    /**
     * Creates a table in which each column has its own name.
     * @param rows The rows
     * @param <R> The record type
     */
    public <R extends Record> QuellTable(List<R> rows) {
        this(null, rows);
    }

    /**
     * Creates a table in which each column's name is qualified by the "as" name.
     * If "as" is null, the name will be unqualified.
     * @param as The logical table name.
     * @param rows The rows
     * @param <R> The record type
     */
    public <R extends Record> QuellTable(String as, List<R> rows) {
        this.size = rows.size();

        if (rows.isEmpty()) {
            return;
        }

        var cls = rows.getFirst().getClass();

        for (var name : Quell.getColumns(cls)) {
            var type = Quell.getColumnType(cls,name);
            var column = new QuellColumn(name, type);
            for (var row : rows) {
                column.values().add(Quell.getColumnValue(row, name));
            }

            var fullName = as != null ? as + "." + column.name : column.name;
            columns.put(fullName, column);
        }
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    public Set<String> getColumnNames() {
        return columns.keySet();
    }

    public <T> T get(int index, String name) {
        return (T)columns.get(name).get(index);
    }
}
