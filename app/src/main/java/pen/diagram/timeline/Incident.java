package pen.diagram.timeline;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("unused")
public sealed interface Incident permits
    Incident.EntityStart,
    Incident.Normal,
    Incident.EntityEnd
{
    //-------------------------------------------------------------------------
    // Standard Methods

    int moment();
    String label();
    boolean concerns(String entityId);
    Cap cap();

    //-------------------------------------------------------------------------
    // Incident Types

    record EntityStart (
        int moment,
        String label,
        String entityId,
        Cap cap
    ) implements Incident {
        public boolean concerns(String entityId) {
            return this.entityId.equals(entityId);
        }
    }

    record Normal(
        int moment,
        String label,
        Set<String> entityIds
    ) implements Incident {
        public boolean concerns(String entityId) {
            return entityIds.contains(entityId);
        }

        public Cap cap() {
            return Cap.FUZZY;
        }
    }

    record EntityEnd(
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
