package pen.history;

import java.util.List;
import java.util.Set;

@SuppressWarnings("unused")
public sealed interface Incident permits
    Incident.Start,
    Incident.Birthday,
    Incident.Memorial,
    Incident.Anniversary,
    Incident.Normal,
    Incident.End
{
    //-------------------------------------------------------------------------
    // Standard Methods

    /**
     * The moment at which the incident occurred.
     * @return The moment.
     */
    int moment();

    /**
     * What happened at that moment.
     * @return The label
     */
    String label();

    /**
     * Whether this incident concerns the given entity.
     * @param entityId The entity's ID
     * @return true or false
     */
    default boolean concerns(String entityId) {
        return entityIds().contains(entityId);
    }

    /**
     * The entities associated with this incident
     * @return The entities
     */
    Set<String> entityIds();

    /**
     * The cap to be used if this is the first/last incident for some entity.
     * @return The cap
     */
    Cap cap();

    /**
     * True if this is a recurring date that should be noted on the calendar
     * in each following year, and false otherwise.
     * @return true or false
     */
    boolean isRecurring();

    //-------------------------------------------------------------------------
    // Incident Types

    /**
     * An entity enters the history.
     * @param moment The moment of entry
     * @param label The label
     * @param entityId The entity ID
     */
    record Start(
        int moment,
        String label,
        String entityId
    ) implements Incident {
        public Set<String> entityIds() { return Set.of(entityId); }
        public Cap cap() { return Cap.HARD; }
        public boolean isRecurring() { return false; }
    }

    /**
     * A person (or persons, in cases of multiple birth) is born.
     * @param moment The moment of death.
     * @param label The label
     * @param entityIds The entity IDs
     */
    record Birthday(
        int moment,
        String label,
        Set<String> entityIds
    ) implements Incident {
        public Cap cap() { return Cap.HARD; }
        public boolean isRecurring() { return true; }
    }

    /**
     * A memorial of some kind, e.g., some event other than a birthday
     * which is remembered each following year.
     * @param moment The moment
     * @param label The label
     * @param entityIds The entity IDs
     */
    record Memorial(
        int moment,
        String label,
        Set<String> entityIds
    ) implements Incident {
        public Cap cap() { return Cap.SOFT; }
        public boolean isRecurring() { return true; }
    }

    /**
     * An anniversary of a recurring incident.  Anniversaries are usually
     * created by the expandRecurrent() query term.
     * @param moment The moment of the anniversary
     * @param age The age of the anniversary in years
     * @param start The original incident, of which this is an anniversary
     */
    record Anniversary(
        int moment,
        int age,
        Incident start
    ) implements Incident {
        public String label() {
            return start.label() + " (" + ordinal(age()) + ")";
        }
        public Set<String> entityIds() { return start.entityIds(); }
        public Cap cap() { return Cap.SOFT; }
        public boolean isRecurring() { return false; }

        private String ordinal(int number) {
            return switch (number) {
                case 1 -> "1st";
                case 2 -> "2nd";
                case 3 -> "3rd";
                default -> number + "th";
            };
        }
    }

    /**
     * A normal event, possibly concerning multiple entities.
     * @param moment The moment
     * @param label The label
     * @param entityIds The IDs of the concerned entities.
     */
    record Normal(
        int moment,
        String label,
        Set<String> entityIds
    ) implements Incident {
        public Cap cap() { return Cap.SOFT; }
        public boolean isRecurring() { return false; }
    }

    /**
     * An entity exits the history.
     * @param moment The moment of exit
     * @param label The label
     * @param entityId The entity ID
     */
    record End(
        int moment,
        String label,
        String entityId
    ) implements Incident {
        public Set<String> entityIds() { return Set.of(entityId); }
        public Cap cap() { return Cap.HARD; }
        public boolean isRecurring() { return false; }
    }
}
