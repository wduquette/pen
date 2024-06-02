package pen.quell;

import org.junit.Before;
import org.junit.Test;
import pen.Ted;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static pen.checker.Checker.check;

public class QuellQueryTest extends Ted {
    List<Person> persons;
    QuellQuery query;

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
        query = Quell.query(persons);

        var table = query.result();

        // Table data is unchanged.
        check(table.isEmpty()).eq(false);
        check(table.size()).eq(4);
        check(table.getColumnNames()).eq(Set.of("id", "name", "age"));

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

    @Test
    public void testCreation_withAs() {
        test("testCreation_withAs");
        query = Quell.queryAs("p", persons);
        var table = query.result();

        check(table.isEmpty()).eq(false);
        check(table.size()).eq(4);
        check(table.getColumnNames()).eq(Set.of("p.id", "p.name", "p.age"));

        check(table.get(0, "p.name")).eq("Fred");
        check(table.get(1, "p.name")).eq("George");
        check(table.get(2, "p.name")).eq("Harry");
        check(table.get(3, "p.name")).eq("Tom");
    }

    @Test
    public void testFilter() {
        var table = Quell.queryAs("p", persons)
            .dump("Before filter")
            .filter(r -> (int)r.get("p.age") > 25)
            .dump("After filter")
            .result();

        check(table.size()).eq(1);
        check(table.getColumnNames()).eq(Set.of("p.id", "p.name", "p.age"));

        check(table.get(0, "p.id")).eq(4);
        check(table.get(0, "p.name")).eq("Tom");
        check(table.get(0, "p.age")).eq(74);
    }
}
