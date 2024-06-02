package pen.quell;

import java.util.List;
import java.util.Set;
import java.util.TreeMap;

/**
 * A QuellTable is a collection of QuellColumns, each containing a column of
 * data in a table.
 */
public class QuellTable {
    private int size = 0;
    private final TreeMap<String, QuellColumn> columns = new TreeMap<>();

    /**
     * Creates a new table with the same column definitions but no data as
     * another table.
     * @param other The other table
     * @return The new table
     */
    public static QuellTable withShape(QuellTable other) {
        var table = new QuellTable();
        for (var fullName : other.columns.keySet()) {
            var column = other.columns.get(fullName);
            var newColumn = new QuellColumn(column.name, column.type);
            table.columns.put(fullName, newColumn);
        }

        return table;
    }

    QuellTable() {
        // Nothing to do
    }

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
        return columns.get(name).get(index);
    }

    public QuellRow get(int index) {
        var row = new QuellRow();
        for (var fullName : columns.keySet()) {
            var column = columns.get(fullName);
            row.put(fullName, column.get(index));
        }

        return row;
    }

    /**
     * Adds a column to the table with the given name and type.
     * If the table contains any rows, the column's values are initialized to null.
     * @param name The column name
     * @param type The column's value type.
     */
    public void addColumn(String name, Class<?> type) {
        if (columns.containsKey(name)) {
            throw new IllegalArgumentException("Column already exists: " + name);
        }

        var column = new QuellColumn(name, type);
        columns.put(name, column);

        for (int i = 0; i < size; i++) {
            column.values().add(null);
        }
    }

    public void add(QuellRow row) {
        if (row.columnNames().size() != columns.size()) {
            throw new IllegalArgumentException("Invalid row: expected " +
                columns.size() + " columns, got: " + row.columnNames().size());
        }

        for (var fullName : row.columnNames()) {
            var value = row.get(fullName);
            var column = columns.get(fullName);

            if (column == null) {
                throw new IllegalArgumentException("Unknown column: " + fullName);
            }

            if (!column.type().isAssignableFrom(value.getClass())) {
                throw new IllegalArgumentException("Type mismatch for column " +
                    fullName + ": expected " + column.type() + ", got " +
                    value.getClass());
            }

            column.values().add(value);
        }

        ++size;
    }
}
