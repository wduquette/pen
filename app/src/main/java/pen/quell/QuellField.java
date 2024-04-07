package pen.quell;

/**
 * A column's value and type in this row.
 */
public class QuellField {
    private final Class<?> type;
    private Object value;

    QuellField(Class<?> type, Object value) {
        this.type = type;
        this.value = value;
    }

    public Class<?> type() {
        return type;
    }

    @SuppressWarnings("unchecked")
    public <T> T value() {
        return (T) value;
    }

    void setValue(Object value) {
        if (type.isAssignableFrom(value.getClass())) {
            this.value = value;
        } else {
            throw new IllegalArgumentException("Value type, " +
                value.getClass() + ", is incompatible with column type, " +
                type + ".");
        }
    }
}
