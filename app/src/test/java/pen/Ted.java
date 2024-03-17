package pen;

@SuppressWarnings("unused")
public class Ted {
    public void test(String testName) {
        println("---- " + getClass().getSimpleName() + " " + testName);
    }

    public void print(String text) {
        System.out.print(text);
    }

    public void println(String text) {
        print(text);
        print("\n");
    }

    public void println() {
        print("\n");
    }

    public void printf(String format, Object... args) {
        print(String.format(format, args));
    }
}
