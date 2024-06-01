package pen.quell;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.lang.reflect.Field;
import java.util.stream.Stream;

/**
 * Quell is the main entry point to the Quell query language.  It is a static
 * class providing utility methods as well as the query pipeline factory.
 * Data is provided in the form of lists of Java records.
 */
public class Quell {
    //-------------------------------------------------------------------------
    // Static Methods

    /**
     * Given a record class, gets the names of its fields.
     * @param cls The record class
     * @return The list of names.
     * @param <R> The record class type
     */
    public static <R extends Record> List<String> getColumns(Class<R> cls) {
        // Note: needs to be "getDeclaredFields()", as "getFields()" only
        // returns *public* member variables.
        return Stream.of(cls.getDeclaredFields())
            .map(Field::getName)
            .toList();
    }

    /**
     * Returns the type of a record field.  Throws an exception if the field
     * type is not a simple class.
     * @param cls The record class
     * @param name The field name
     * @return The class
     * @param <R> The record type
     * @throws IllegalArgumentException if the value could not be retrieved.
     */
    public static <R extends Record> Class<?> getColumnType(
        Class<R> cls,
        String name
    ) {
        try {
            var method = cls.getMethod(name);
            return method.getReturnType();
        } catch (NoSuchMethodException ex) {
            throw new IllegalArgumentException(
                cls.getSimpleName() +
                " has no such field: \"" + name + "\".", ex);
        }
    }

    /**
     * Returns the value of a record field.  Provided that the given name
     * was received via Quell.getColumns(), this method should not throw
     * any exceptions.
     * @param record The record
     * @param name The field name
     * @return The value
     * @param <R> The record type
     * @param <T> The column type
     * @throws IllegalArgumentException if the value could not be retrieved.
     */
    @SuppressWarnings("unchecked")
    public static <R extends Record, T> T getColumnValue(
        R record,
        String name
    ) {
        try {
            var method = record.getClass().getMethod(name);
            return (T)method.invoke(record);
        } catch (NoSuchMethodException ex) {
            throw new IllegalArgumentException(
                record.getClass().getSimpleName() +
                    " has no such field: \"" + name + "\".", ex);
        } catch (IllegalAccessException | InvocationTargetException ex) {
            throw new IllegalArgumentException(
                "Could not access " + record.getClass().getSimpleName() +
                " field: \"" + name + "\".", ex);
        }
    }
}
