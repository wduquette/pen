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
        this.type = type;
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

    public <T> T get(int index) {
        return (T)values.get(index);
    }

    // Temporary, until I decide what the real API should be.
    public List<Object> values() {
        return values;
    }

}
