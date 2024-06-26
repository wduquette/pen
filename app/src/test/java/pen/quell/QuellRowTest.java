package pen.quell;

import org.junit.Before;
import org.junit.Test;
import pen.Ted;

import java.util.Set;

import static pen.checker.Checker.check;

public class QuellRowTest extends Ted {
    QuellRow row;
    Person person;

    @Before
    public void setup() {
    }

    @Test
    public void testCreate_empty() {
        test("testCreate_empty");
        row = new QuellRow();

        check(row.isEmpty()).eq(true);
        check(row.getSource()).eq(QuellRow.UNKNOWN);
    }

    @Test
    public void testCreate_person() {
        test("testCreate_person");
        person = new Person(1, "a1", 23);
        row = new QuellRow(person);

        check(row.isEmpty()).eq(false);
        check(row.getSource()).eq("Person");
        check(row.keySet()).eq(Set.of("id", "name", "age"));
        check(row.get("id")).eq(1);
        check(row.get("name")).eq("a1");
        check(row.get("age")).eq(23);
    }

    @Test
    public void testToRecord_good() {
        test("testToRecord_good");
        person = new Person(1, "a1", 23);
        row = new QuellRow(person);
        var copy = row.toRecord(Person.class);

        check(copy.id()).eq(1);
        check(copy.name()).eq("a1");
        check(copy.age()).eq(23);
    }
}
