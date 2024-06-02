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
    // Accessors

    public Set<String> columnNames() {
        return map.keySet();
    }

    public void put(String columnName, Object value) {
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
