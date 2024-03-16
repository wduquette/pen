package pen.table;

import java.util.*;

public class Table<R extends Row> {
    public List<R> rows = new ArrayList<>();
    public Map<String,R> index = new HashMap<>();

    public void insert(R row) {
        delete(row.key());
        rows.add(row);
        index.put(row.key(), row);
    }

    public void delete(String key) {
        var row = index.get(key);

        if (key != null) {
            index.remove(key);
            rows.remove(row);
        }
    }

    public Optional<R> get(String key) {
        return Optional.ofNullable(index.get(key));
    }
}
