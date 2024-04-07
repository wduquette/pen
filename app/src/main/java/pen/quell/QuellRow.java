package pen.quell;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class QuellRow {
    public static final String UNKNOWN = "unknown";

    //-------------------------------------------------------------------------
    // Instance Variables

    private String source = UNKNOWN;
    private final TreeMap<String, QuellField> data = new TreeMap<>();

    //-------------------------------------------------------------------------
    // Constructor

    public QuellRow() {
        // Nothing to do.
    }

    public <R extends Record> QuellRow(R record) {
        source = record.getClass().getSimpleName();

        for (var name : Quell.getColumns(record.getClass())) {
            var type  = Quell.getColumnType(record, name);
            var value = Quell.getColumnValue(record, name);
            data.put(name,
                new QuellField(type, value));
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
        return (T)data.get(column).value();
    }

    public void put(String column, Object value) {
        if (!data.containsKey(column)) {
            throw new IllegalStateException("Column type is unknown: \"" +
                column + "\".");
        }

        data.get(column).setValue(value);
    }

    @SuppressWarnings("unused")
    public Set<Map.Entry<String, QuellField>> entrySet() {
        return data.entrySet();
    }

    public Set<String> keySet() {
        return data.keySet();
    }

    //-------------------------------------------------------------------------
    // Conversion, Row to Record

    /**
     * Converts a row to a record of the given class.  All data must exist
     * and have the appropriate type.
     * @param cls
     * @return
     * @param <R>
     */
    public <R> R toRecord(Class<R> cls) {
        // TODO
        return null;
    }

    //-------------------------------------------------------------------------
    // Field Class

}
