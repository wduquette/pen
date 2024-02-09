package pen.checker;

import java.util.Objects;

public class Checker<Value> {
    //-------------------------------------------------------------------------
    // Static Methods

    public static <V> Checker<V> check(V value) {
        return new Checker<>(value);
    }

    public static Checker<Exception> checkThrows(Runnable runnable) {
        try {
            runnable.run();
            throw new AssertionError("checkThrows: Expected exception");
        } catch (Exception ex) {
            return check(ex);
        }
    }

    //-------------------------------------------------------------------------
    // Instance Variables

    private final Value value;

    //-------------------------------------------------------------------------
    // Constructor

    private Checker(Value value) {
        this.value = value;
    }

    //-------------------------------------------------------------------------
    // Checkers

    public Checker<Value> eq(Value expected) {
        if (Objects.equals(value, expected)) {
            return this;
        } else {
            throw new AssertionError("eq: expected \"" + expected +
                "\", got: \"" + value + "\"");
        }
    }

    public Checker<Value> hasString(String expected) {
        if (value != null && value.toString().equals(expected)) {
            return this;
        }

        throw new AssertionError("hasString: expected \"" + expected +
            "\", got: \"" + value + "\"");
    }

    public Checker<Value> containsString(String expected) {
        if (value != null && value.toString().contains(expected)) {
            return this;
        }

        throw new AssertionError("containsString: expected \"" + expected +
            "\", got: \"" + value + "\"");
    }
}