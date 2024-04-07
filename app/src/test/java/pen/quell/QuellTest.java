package pen.quell;

import org.junit.Before;
import org.junit.Test;
import pen.Ted;

import java.util.List;

import static pen.checker.Checker.check;
import static pen.checker.Checker.checkThrows;

public class QuellTest extends Ted {
    @Before
    public void setup() {
    }

    @Test
    public void testGetColumns() {
        test("testGetColumns");
        var columns = Quell.getColumns(Person.class);

        check(columns).eq(List.of("id", "name", "age"));
    }

    @Test
    public void testGetColumnValue_good() {
        test("testGetColumnValue_good");
        var person = new Person(1, "a1", 23);

        check(Quell.getColumnValue(person, "id").toString()).eq("1");
        check(Quell.getColumnValue(person, "name").toString()).eq("a1");
        check(Quell.getColumnValue(person, "age").toString()).eq("23");
    }

    @Test
    public void testGetColumnValue_bad() {
        test("testGetColumnValue_bad");
        var person = new Person(1, "a1", 23);

        checkThrows(() -> Quell.getColumnValue(person, "nonesuch"))
            .containsString("Record has no such field: \"nonesuch\"");
    }
}
