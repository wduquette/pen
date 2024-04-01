package pen.quell;

import org.junit.Before;
import org.junit.Test;
import pen.Ted;
import pen.history.*;

import java.util.List;
import java.util.Set;

import static pen.checker.Checker.check;

public class QuellTest extends Ted {
    @Before
    public void setup() {
    }

    @Test
    public void testGetColumns_record() {
        test("testGetColumns_record");
        var columns = Quell.getColumns(Person.class);

        check(columns).eq(List.of("id", "name", "age"));
    }

    //-------------------------------------------------------------------------
    // Helper Types

    record Person(int id, String name, int age) {}
}
