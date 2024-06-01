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

    public <R extends Record> QuellTable(List<R> rows) {
        this.size = rows.size();

        if (rows.isEmpty()) {
            return;
        }

        var cls = rows.getFirst().getClass();

        for (var name : Quell.getColumns(cls)) {
            var type = Quell.getColumnType(cls,name);
            var column = new QuellColumn(name, type);
            columns.put(name, column);

            for (var row : rows) {
                column.values().add(Quell.getColumnValue(row, name));
            }
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
