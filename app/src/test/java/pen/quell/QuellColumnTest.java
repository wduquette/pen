package pen.quell;

import org.junit.Before;
import org.junit.Test;
import pen.Ted;

import java.util.List;

import static pen.checker.Checker.check;
import static pen.checker.Checker.checkThrow;

public class QuellColumnTest extends Ted {
    QuellColumn column;
    @Before
    public void setup() {
    }

    @Test
    public void testCreation() {
        test("testCreation");
        column = new QuellColumn("count", Integer.class);

        check(column.name()).eq("count");
        check(column.type().equals(Integer.class)).eq(true);
        check(column.isEmpty()).eq(true);
        check(column.size()).eq(0);
    }

    @Test
    public void testAddItem() {
        test("testAddItem");
        column = new QuellColumn("count", Integer.class);

        column.values().add(10);
        check(column.isEmpty()).eq(false);
        check(column.size()).eq(1);
    }
}
