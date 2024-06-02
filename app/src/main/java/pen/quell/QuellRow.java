package pen.quell;

import java.util.Set;
import java.util.TreeMap;

public class QuellRow {
    //-------------------------------------------------------------------------
    // Instance Variables

    private final TreeMap<String,Object> map = new TreeMap<>();

    //-------------------------------------------------------------------------
    // Constructor

    public QuellRow() {
        // nothing to do
    }

    //-------------------------------------------------------------------------
    // Package Private

    /**
     * This method is for use internally, when building rows from column data.
     * @param name The column name
     * @param value The value
     */
    void add(String name, Object value) {
        map.put(name, value);
    }

    //-------------------------------------------------------------------------
    // Public Accessors

    public Set<String> columnNames() {
        return map.keySet();
    }

    public void put(String columnName, Object value) {
        if (!map.containsKey(columnName)) {
            throw new IllegalArgumentException("Undefined column: " + columnName);
        }
        map.put(columnName, value);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String columnName) {
        return (T)map.get(columnName);
    }

    @Override
    public String toString() {
        return map.toString();
    }
}
