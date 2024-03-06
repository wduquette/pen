package pen.diagram.timeline;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class TimelineEvent {
    //-------------------------------------------------------------------------
    // Instance Variables

    private int time = 0;
    private String text = "???";
    private final Set<String> entityIds = new HashSet<>();

    //-------------------------------------------------------------------------
    // Constructor

    public TimelineEvent() {
        // Nothing to do
    }

    //-------------------------------------------------------------------------
    // Getters

    public int getTime() {
        return time;
    }

    public String getText() {
        return text;
    }

    public Set<String> getEntityIds() {
        return Collections.unmodifiableSet(entityIds);
    }

    public boolean concerns(String entityId) {
        return entityIds.contains(entityId);
    }

    public boolean concerns(TimelineEntity entity) {
        return entityIds.contains(entity.getId());
    }

    //-------------------------------------------------------------------------
    // DSL

    public TimelineEvent time(int value) {
        this.time = time;
        return this;
    }

    public TimelineEvent text(String value) {
        this.text = value;
        return this;
    }

    public TimelineEvent entity(String id) {
        entityIds.add(id);
        return this;
    }
}
