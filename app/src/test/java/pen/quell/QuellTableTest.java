package pen.quell;

import org.junit.Before;
import org.junit.Test;
import pen.Ted;

import java.util.ArrayList;
import java.util.List;

import static pen.checker.Checker.check;

public class QuellTableTest extends Ted {
    List<Person> persons;
    QuellTable table;
    @Before
    public void setup() {
        persons = new ArrayList<>();
        persons.add(new Person(1, "Fred", 25));
        persons.add(new Person(2, "George", 25));
        persons.add(new Person(3, "Harry", 21));
        persons.add(new Person(4, "Tom", 74));
    }

    @Test
    public void testCreation() {
        test("testCreation");
        table = new QuellTable(persons);

        check(table.isEmpty()).eq(false);
        check(table.size()).eq(4);

        check(table.get(0, "id")).eq(1);
        check(table.get(0, "name")).eq("Fred");
        check(table.get(0, "age")).eq(25);

        check(table.get(1, "id")).eq(2);
        check(table.get(1, "name")).eq("George");
        check(table.get(1, "age")).eq(25);

        check(table.get(2, "id")).eq(3);
        check(table.get(2, "name")).eq("Harry");
        check(table.get(2, "age")).eq(21);

        check(table.get(3, "id")).eq(4);
        check(table.get(3, "name")).eq("Tom");
        check(table.get(3, "age")).eq(74);
    }
}
