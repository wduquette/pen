package pen.history;

import java.util.Set;

@SuppressWarnings("unused")
public sealed interface Incident permits
    Incident.Beginning,
    Incident.Normal,
    Incident.Ending
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
    boolean concerns(String entityId);

    Set<String> entityIds();

    /**
     * The cap to be used if this is the first/last incident for some entity.
     * @return The cap
     */
    Cap cap();

    //-------------------------------------------------------------------------
    // Incident Types

    /**
     * An entity enters the history.
     * @param moment The moment of entry
     * @param label The label
     * @param entityId The entity ID
     */
    record Beginning(
        int moment,
        String label,
        String entityId
    ) implements Incident {
        public boolean concerns(String entityId) {
            return this.entityId.equals(entityId);
        }

        public Set<String> entityIds() {
            return Set.of(entityId);
        }

        public Cap cap() {
            return Cap.HARD;
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
        public boolean concerns(String entityId) {
            return entityIds.contains(entityId);
        }

        public Cap cap() {
            return Cap.SOFT;
        }
    }

    /**
     * An entity exits the history.
     * @param moment The moment of exit
     * @param label The label
     * @param entityId The entity ID
     */
    record Ending(
        int moment,
        String label,
        String entityId
    ) implements Incident {
        public boolean concerns(String entityId) {
            return this.entityId.equals(entityId);
        }

        public Set<String> entityIds() {
            return Set.of(entityId);
        }

        public Cap cap() {
            return Cap.HARD;
        }
    }
}
