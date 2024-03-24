package pen.history;

import java.util.Set;

@SuppressWarnings("unused")
public sealed interface Incident permits
    Incident.Enters,
    Incident.Normal,
    Incident.Exits
{
    //-------------------------------------------------------------------------
    // Standard Methods

    int moment();
    String label();
    boolean concerns(String entityId);
    Cap cap();

    //-------------------------------------------------------------------------
    // Incident Types

    /**
     * An entity enters the history.
     * @param moment The moment of entry
     * @param label The label
     * @param entityId The entity ID
     * @param cap The cap, soft or hard
     */
    record Enters(
        int moment,
        String label,
        String entityId,
        Cap cap
    ) implements Incident {
        public boolean concerns(String entityId) {
            return this.entityId.equals(entityId);
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
     * @param cap The cap, soft or hard
     */
    record Exits(
        int moment,
        String label,
        String entityId,
        Cap cap
    ) implements Incident {
        public boolean concerns(String entityId) {
            return this.entityId.equals(entityId);
        }
    }
}
