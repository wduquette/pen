package pen.quell;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class QuellTable {
    public static final String UNKNOWN = "unknown";

    private String source = UNKNOWN;
    private int size = 0;
    private TreeMap<String, Class<?>> types = new TreeMap<>();
    private TreeMap<String, List<Object>> columns = new TreeMap<>();

    public <R extends Record> QuellTable(List<R> rows) {
        if (rows.isEmpty()) {
            return;
        }
        size = rows.size();

        var cls = rows.getFirst().getClass();
        source = cls.getSimpleName();

        for (var name : Quell.getColumns(cls)) {
            types.put(name, Quell.getColumnType(cls, name));
            var list = new ArrayList<>(rows.size());
            columns.put(name, list);

            for (var row : rows) {
                list.add(Quell.getColumnValue(row, name));
            }
        }
    }

    public int size() {
        return size;
    }
}
