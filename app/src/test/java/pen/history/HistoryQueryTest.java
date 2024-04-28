package pen.history;

import org.junit.Before;
import org.junit.Test;
import pen.Ted;
import pen.calendars.Gregorian;
import pen.calendars.TrivialCalendar;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import static pen.checker.Checker.check;
import static pen.checker.Checker.fail;

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
        var view = query.noLaterThan(0).execute(history); // No incidents.
        check(view.getIncidents().isEmpty()).eq(true);

        query.clear();
        view = query.execute(history); // Everything.

        check(view.getIncidents()).eq(history.getIncidents());
        check(view.getEntityMap()).eq(history.getEntityMap());
    }

    @Test
    public void testAfter() {
        populateHistory();
        var view = query.noEarlierThan(50).execute(history);
        check(view.getTimeFrame()).eq(TimeFrame.of(50, 90));
    }

    @Test
    public void testBefore() {
        populateHistory();
        var view = query.noLaterThan(50).execute(history);
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
    public void testExpandRecurring_normal() {
        var cal = Gregorian.CALENDAR;
        var birth = cal.date(1997, 2, 11);
        var finalDate = cal.date(2000, 3, 1);

        history.addEntity(new Entity("david", "David", "person", true));
        history.getIncidents().add(new Incident.Birthday(
            cal.date2day(birth), "David's birth", Set.of("david")));
        history.getIncidents().add(new Incident.Normal(
            cal.date2day(finalDate), "Final date", Set.of("david")));

        var view = query.expandAnniversaries(cal).execute(history);
        check(view.getIncidents().size()).eq(5);

        var result = view.getIncidents().stream()
            .sorted(Comparator.comparing(Incident::moment))
            .toList();

        // We kept the original two incidents.
        check(result.get(0)).eq(history.getIncidents().get(0));
        for (var i = 1; i <= 3; i++) {
            if (result.get(i) instanceof Incident.Anniversary ann) {
                check(ann.age()).eq(i);
                check(ann.label()).containsString("David's birth");
            } else {
                fail("Not an Anniversary: " + result.get(i));
            }
        }
        check(result.get(4)).eq(history.getIncidents().get(1));
    }

    @Test
    public void testExpandRecurring_noMonths() {
        var cal = new TrivialCalendar.Builder()
            .yearLength(100)
            .build();

        history.addEntity(new Entity("joe", "JoeP", "person", true));
        history.getIncidents()
            .add(new Incident.Birthday(10, "Joe is born", Set.of("joe")));
        // Ensure we have a following year
        history.getIncidents()
            .add(new Incident.Normal(120, "Final event", Set.of("joe")));

        // No calendar months implies no anniversaries
        var view = query.expandAnniversaries(cal).execute(history);
        check(view.getIncidents().size()).eq(2);
    }

    @Test
    public void testExpandRecurring_noIncidents() {
        var view = query.expandAnniversaries(Gregorian.CALENDAR).execute(history);

        // No incidents implies no anniversaries
        check(view.getIncidents().isEmpty()).eq(true);
    }

    @Test
    public void testExpandRecurring_noRecurring() {
        history.addEntity(new Entity("joe", "JoeP", "person", true));
        history.getIncidents()
            .add(new Incident.Start(10, "Joe is born", "joe"));

        // No recurring incidents implies no anniversaries
        var view = query.expandAnniversaries(Gregorian.CALENDAR).execute(history);
        check(view.getIncidents().size()).eq(1);
    }

    @Test
    public void testIncludes() {
        test("testIncludes");
        populateHistory();
        var view = query.includes("bob").execute(history);
        check(view.getEntityMap().keySet()).eq(Set.of("bob"));

        for (var evt : view.getIncidents()) {
            check(evt.concerns("bob")).eq(true);
        }

        dump("View", view);
        for (var e : view.getPeriods().entrySet()) {
            println("  " + e.getKey() + ": " + e.getValue());
        }
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

    @Test
    public void testGroupBySource() {
        test("testGroupBySource()");
        populateHistory2();
        var view = query.execute(history);

        check(view.getPeriodGroups()).eq(history.getPeriodGroups());
    }

    @Test
    public void testGroupByPrimes_entities_noEmptyGroups() {
        test("testGroupByPrimes_entities_noEmptyGroups()");
        populateHistory2();
        var view = query
            .groupByPrimes(List.of("c3", "b3", "a3"), null)
            .execute(history);

        dumpGroups("By Primes", view);
        check(groups2list(view)).eq(List.of(
            // Prime entity order is maintained
            "prime", "c3", "b3", "a3",
            // Non-prime type groups in alpha order
            // Entities sorted by period start in type groups
            "a", "a1", "a2",
            "b", "b1", "b2",
            "c", "c1", "c2"
        ));
    }

    @Test
    public void testGroupByPrimes_entities_emptyGroup() {
        test("testGroupByPrimes_entities_emptyGroup()");
        populateHistory2();
        var view = query
            .groupByPrimes(List.of("c3", "b3", "a1", "a2", "a3"), null)
            .execute(history);

        dumpGroups("By Primes", view);
        check(groups2list(view)).eq(List.of(
            // Prime entity order is maintained
            "prime", "c3", "b3", "a1", "a2", "a3",
            // Non-prime type groups in alpha order
            // Entities sorted by period start in type groups
            // Empty types omitted
            "b", "b1", "b2",
            "c", "c1", "c2"
        ));
    }

    @Test
    public void testGroupByPrimes_both_noEmptyGroups() {
        test("testGroupByPrimes_both_noEmptyGroups()");
        populateHistory2();
        var view = query
            .groupByPrimes(List.of("c3", "b3", "a3"), List.of("b"))
            .execute(history);

        dumpGroups("By Primes", view);
        check(groups2list(view)).eq(List.of(
            // Prime entity order is maintained
            "prime", "c3", "b3", "a3",
            // Entities sorted by period start in type groups
            // Prime type groups in order
            "b", "b1", "b2",
            // Non-prime type groups in alpha order
            "a", "a1", "a2",
            "c", "c1", "c2"
        ));
    }

    private void populateHistory() {
        history.addEntity(new Entity("joe", "JoeP", "person", true));
        history.addEntity(new Entity("bob", "BobC", "person", false));
        history.getIncidents()
            .add(new Incident.Start(10, "Joe is born", "joe"));
        history.getIncidents()
            .add(new Incident.Start(15, "Bob enters", "bob"));
        history.getIncidents()
            .add(new Incident.Normal(50, "Joe and Bob talk", Set.of("joe", "bob")));
        history.getIncidents()
            .add(new Incident.End(85, "Bob leaves", "bob"));
        history.getIncidents()
            .add(new Incident.End(90, "Joe dies", "joe"));
    }

    private void populateHistory2() {
        makeEntity("a1", "a", 10, 30);
        makeEntity("a2", "a", 13, 31);
        makeEntity("a3", "a", 16, 32);
        makeEntity("b1", "b", 11, 33);
        makeEntity("b2", "b", 14, 34);
        makeEntity("b3", "b", 17, 35);
        makeEntity("c1", "c", 12, 36);
        makeEntity("c2", "c", 15, 37);
        makeEntity("c3", "c", 18, 38);
    }

    private void makeEntity(String id, String type, int start, int end) {
        history.addEntity(new Entity(id, id.toUpperCase(), type, false));
        history.getIncidents()
            .add(new Incident.Start(start, null, id));
        history.getIncidents()
            .add(new Incident.End(end, null, id));
    }

    @SuppressWarnings("unused")
    private void dump(String title, History data) {
        println("Dump: " + title + ":");
        data.getEntityMap().values().forEach(e -> println("  " + e));
        data.getIncidents().forEach(i -> println("  " + i));
        var pgs = data.getPeriodGroups();

        for (var e : pgs.entrySet()) {
            println("  Group: " + e.getKey());
            e.getValue().forEach(p -> println("    " + p));
        }
    }

    @SuppressWarnings("unused")
    private void dumpGroups(String title, History data) {
        println("Dump Groups: " + title + ":");
        var pgs = data.getPeriodGroups();

        for (var e : pgs.entrySet()) {
            println("  Group: " + e.getKey());
            e.getValue().forEach(p -> println("    " + p));
        }
    }

    private List<String> groups2list(History data) {
        var pgs = data.getPeriodGroups();
        var list = new ArrayList<String>();

        for (var e : pgs.entrySet()) {
            list.add(e.getKey());
            e.getValue().forEach(p -> list.add(p.entity().id()));
        }

        return list;
    }
}
