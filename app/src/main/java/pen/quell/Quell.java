package pen.quell;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.lang.reflect.Field;
import java.util.stream.Stream;

public class Quell {
    public static <R extends Record> List<String> getColumns(Class<R> cls) {
        // Note: needs to be "getDeclaredFields()", as "getFields()" only
        // returns *public* member variables.
        for (var field : cls.getDeclaredFields()) {
            System.out.println("Field: " + field.getName() + " isa " + field.getType());
        }

        return Stream.of(cls.getDeclaredFields())
            .map(Field::getName)
            .toList();
    }

    /**
     * Returns the type of a record field.  Throws an exception if the field
     * type is not a simple class.
     * @param record The record
     * @param name The field name
     * @return The class
     * @param <R> The record type
     * @throws IllegalArgumentException if the value could not be retrieved.
     */
    @SuppressWarnings("unchecked")
    public static <R extends Record> Class<?> getColumnType(
        R record,
        String name
    ) {
        try {
            var method = record.getClass().getMethod(name);
            return method.getReturnType();
        } catch (NoSuchMethodException ex) {
            throw new IllegalArgumentException(
                "Record has no such field: \"" + name + "\".", ex);
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
                "Record has no such field: \"" + name + "\".", ex);
        } catch (IllegalAccessException | InvocationTargetException ex) {
            throw new IllegalArgumentException(
                "Could not access record field: \"" + name + "\".", ex);
        }
    }
}
