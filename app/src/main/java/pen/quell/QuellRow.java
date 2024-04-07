package pen.quell;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class QuellRow {
    public static final String UNKNOWN = "unknown";

    //-------------------------------------------------------------------------
    // Instance Variables

    // TODO: it isn't yet clear what metadata I want to retain.
    private String source = UNKNOWN;
    private final TreeMap<String,Object> data = new TreeMap<>();

    //-------------------------------------------------------------------------
    // Constructor

    public QuellRow() {
        // Nothing to do.
    }

    public <R extends Record> QuellRow(R record) {
        source = record.getClass().getSimpleName();

        for (var name : Quell.getColumns(record.getClass())) {
            data.put(name, Quell.getColumnValue(record, name));
        }
    }

    //-------------------------------------------------------------------------
    // Operations

    public boolean isEmpty() {
        return data.isEmpty();
    }

    public String getSource() {
        return source;
    }

    @SuppressWarnings("unused")
    public void setSource(String source) {
        this.source = source;
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String column) {
        return (T)data.get(column);
    }

    public void put(String column, Object value) {
        data.put(column, value);
    }

    @SuppressWarnings("unused")
    public Set<Map.Entry<String,Object>> entrySet() {
        return data.entrySet();
    }

    public Set<String> keySet() {
        return data.keySet();
    }
}
