package pen.quell;

import java.util.ArrayList;
import java.util.List;

/**
 * A QuellColumn represents a column in a QuellTable: a type, a name, and the
 * list of values.
 */
public class QuellColumn {
    //-------------------------------------------------------------------------
    // Instance Variables

    public final String name;
    public final Class<?> type;
    public List<Object> values = new ArrayList<>();

    //-------------------------------------------------------------------------
    // Constructor

    public QuellColumn(String name, Class<?> type) {
        this.name = name;
        this.type = mapType(type);
    }

    private Class<?> mapType(Class<?> type) {
        if (type == boolean.class) {
            return Boolean.class;
        } else if (type == double.class) {
            return Double.class;
        } else if (type == float.class) {
            return Float.class;
        } else if (type == int.class) {
            return Integer.class;
        } else if (type == long.class) {
            return Long.class;
        } else {
            return type;
        }
    }

    //-------------------------------------------------------------------------
    // Public Accessors

    public String name() {
        return name;
    }

    public Class<?> type() {
        return type;
    }

    public boolean isEmpty() {
        return values.isEmpty();
    }

    public int size() {
        return values.size();
    }

    @SuppressWarnings("unchecked")
    public <T> T get(int index) {
        return (T)values.get(index);
    }

    // Temporary, until I decide what the real API should be.
    public List<Object> values() {
        return values;
    }

}
