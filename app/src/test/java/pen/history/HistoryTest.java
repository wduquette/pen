package pen.history;

import org.junit.Before;
import org.junit.Test;

import java.util.Set;

import static pen.checker.Checker.check;

public class HistoryTest {
    private History history;

    @Before
    public void setup() {
        history = new History();
    }

    @Test
    public void testCreation() {
        check(history.getIncidents().isEmpty()).eq(true);
        check(history.getEntityMap().isEmpty()).eq(true);
    }

    @Test
    public void testAddGetEntity() {
        var joe = new Entity("joe", "Joe", "person");
        history.addEntity(joe);
        check(history.getEntityMap().size()).eq(1);
        check(history.getEntity("joe").orElse(null)).eq(joe);
        check(history.getEntity("bob").orElse(null)).eq(null);
    }

    @Test
    public void testRemoveEntity() {
        var joe = new Entity("joe", "Joe", "person");
        history.addEntity(joe);
        check(history.getEntityMap().isEmpty()).eq(false);

        check(history.removeEntity("joe").orElse(null)).eq(joe);
        check(history.getEntityMap().isEmpty()).eq(true);

        check(history.removeEntity("bob").orElse(null)).eq(null);
    }

    @Test
    public void testGetTimeFrame_all() {
        populateHistory();
        var frame = history.getTimeFrame();
        check(frame).eq(new TimeFrame(10, 90));
    }

    @Test
    public void testGetTimeFrame_filtered() {
        System.out.println("testGetTimeFrame_filtered");
        populateHistory();
        var frame = history.getTimeFrame(i -> i.concerns("bob"));
        check(frame).eq(new TimeFrame(15, 85));
    }

    private void populateHistory() {
        history.addEntity(new Entity("joe", "Joe", "person"));
        history.addEntity(new Entity("bob", "Bob", "person"));
        history.getIncidents()
            .add(new Incident.EntityStart(10, "Joe is born", "joe", Cap.HARD));
        history.getIncidents()
            .add(new Incident.EntityStart(15, "Bob enters", "bob", Cap.FUZZY));
        history.getIncidents()
            .add(new Incident.Normal(50, "Joe and Bob talk", Set.of("joe", "bob")));
        history.getIncidents()
            .add(new Incident.EntityStart(85, "Bob leaves", "bob", Cap.FUZZY));
        history.getIncidents()
            .add(new Incident.EntityEnd(90, "Joe dies", "joe", Cap.HARD));
    }

}
