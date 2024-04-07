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
            var type  = Quell.getColumnType(record.getClass(), name);
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

    public <T> T get(String column) {
        return data.get(column).value();
    }

    public void set(String column, Object value) {
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
     * @param cls The requested class
     * @return The new record
     * @param <R> The record type
     * @throws IllegalArgumentException if the requested class is incompatible
     * with the record value.
     */
    @SuppressWarnings("unchecked")
    public <R extends Record> R toRecord(Class<R> cls) {
        var columns = Quell.getColumns(cls);
        var values = new Object[columns.size()];

        for (var i = 0; i < columns.size(); i++) {
            var name = columns.get(i);
            var type = Quell.getColumnType(cls, name);
            var field = data.get(name);

            if (field == null) {
                throw new IllegalArgumentException(
                    "Expected column \"" + name + "\"; no such column.");
            }

            if (!field.type().equals(type)) {
                throw new IllegalArgumentException(
                    "Type mismatch for column \"" + name +
                    "\"; expected type " + type.getCanonicalName() +
                    ", but got type " + field.type().getCanonicalName() + ".");
            }

            values[i] = field.value();
        }

        try {
            var ctor = cls.getDeclaredConstructors()[0];
            return (R)ctor.newInstance(values);
        } catch (Exception ex) {
            throw new IllegalArgumentException(
                "Failed to create an instance of " + cls.getCanonicalName() +
                " from this row.", ex);
        }
    }

    //-------------------------------------------------------------------------
    // Field Class

}
