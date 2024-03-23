package pen.history;

import org.junit.Before;
import org.junit.Test;
import pen.Ted;

import java.util.Set;

import static pen.checker.Checker.check;

public class HistoryQueryTest extends Ted {
    private HistoryBank history;
    private HistoryQuery query;

    @Before
    public void setup() {
        history = new HistoryBank();
        query = new HistoryQuery();
    }

    @Test
    public void testNullQuery() {
        test("testNullQuery");
        populateHistory();
        var view = query.execute(history);
        check(view.getIncidents()).eq(history.getIncidents());
        check(view.getEntityMap()).eq(history.getEntityMap());
    }

    @Test
    public void testClear() {
        test("testClear");
        populateHistory();
        var view = query.before(0).execute(history); // No incidents.
        check(view.getIncidents().isEmpty()).eq(true);

        query.clear();
        view = query.execute(history); // Everything.

        check(view.getIncidents()).eq(history.getIncidents());
        check(view.getEntityMap()).eq(history.getEntityMap());
    }

    @Test
    public void testAfter() {
        populateHistory();
        var view = query.after(50).execute(history);
        check(view.getTimeFrame()).eq(TimeFrame.of(50, 90));
    }

    @Test
    public void testBefore() {
        populateHistory();
        var view = query.before(50).execute(history);
        check(view.getTimeFrame()).eq(TimeFrame.of(10, 50));
    }

    @Test
    public void testFilter_incidents() {
        populateHistory();
        var view = query.filter(i -> i.label().equals("Bob leaves"))
            .execute(history);
        check(view.getTimeFrame()).eq(TimeFrame.of(85, 85));
    }

    @Test
    public void testIncludes() {
        populateHistory();
        var view = query.includes("bob").execute(history);
        check(view.getEntityMap().keySet()).eq(Set.of("bob"));
    }

    @Test
    public void testExcludes() {
        populateHistory();
        var view = query.excludes("bob").execute(history);
        check(view.getEntityMap().keySet()).eq(Set.of("joe"));
    }

    @Test
    public void testIncludeTypes() {
        populateHistory();
        var view = query.includeTypes("person").execute(history);
        check(view.getEntityMap().keySet()).eq(Set.of("bob", "joe"));
    }

    @Test
    public void testExcludeTypes() {
        populateHistory();
        var view = query.excludeTypes("person").execute(history);
        check(view.getEntityMap().keySet()).eq(Set.of());
    }

    @Test
    public void testBoundByEntities_specific() {
        populateHistory();
        var view = query.boundByEntities("bob").execute(history);
        check(view.getTimeFrame()).eq(TimeFrame.of(15, 85));
    }

    @Test
    public void testBoundByEntities_allRemaining() {
        populateHistory();
        var view = query.includes("bob").boundByEntities().execute(history);
        check(view.getTimeFrame()).eq(TimeFrame.of(15, 85));
    }

    private void populateHistory() {
        history.addEntity(new Entity("joe", "JoeP", "person"));
        history.addEntity(new Entity("bob", "BobC", "person"));
        history.getIncidents()
            .add(new Incident.EntityStart(10, "Joe is born", "joe", Cap.HARD));
        history.getIncidents()
            .add(new Incident.EntityStart(15, "Bob enters", "bob", Cap.SOFT));
        history.getIncidents()
            .add(new Incident.Normal(50, "Joe and Bob talk", Set.of("joe", "bob")));
        history.getIncidents()
            .add(new Incident.EntityStart(85, "Bob leaves", "bob", Cap.SOFT));
        history.getIncidents()
            .add(new Incident.EntityEnd(90, "Joe dies", "joe", Cap.HARD));
    }

    @SuppressWarnings("unused")
    private void dump(History data) {
        println("History Dump");
        data.getEntityMap().values().forEach(e -> println("  " + e));
        data.getIncidents().forEach(i -> println("  " + i));
    }
}
