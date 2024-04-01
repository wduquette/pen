package pen.quell;

import java.util.ArrayList;
import java.util.List;
import java.lang.reflect.Field;

public class Quell {
    public static <R extends Record> List<String> getColumns(Class<R> cls) {
        // Note: needs to be "getDeclaredFields()", as "getFields()" only
        // returns *public* member variables.
        for (var field : cls.getDeclaredFields()) {
            System.out.println("Field: " + field.getName() + " isa " + field.getType());
        }
        return List.of(cls.getDeclaredFields()).stream()
            .map(Field::getName)
            .toList();
    }
}
