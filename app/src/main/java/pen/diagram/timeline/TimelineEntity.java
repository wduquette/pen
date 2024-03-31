package pen.diagram.timeline;

import java.util.Objects;

/**
 * An entity with a timeline on the {@link TimelineDiagram}.
 */
@SuppressWarnings("unused")
public class TimelineEntity {
    //-------------------------------------------------------------------------
    // Instance Variables

    private final String id;
    private String name;
    private Limit start = Limit.eventBased();
    private Limit end = Limit.eventBased();

    //-------------------------------------------------------------------------
    // Constructor

    public TimelineEntity(String id) {
        this.id = id;
        this.name = id;
    }

    //-------------------------------------------------------------------------
    // Getters

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Limit getStart() {
        return start;
    }

    public Limit getEnd() {
        return end;
    }

    //-------------------------------------------------------------------------
    // DSL

    public TimelineEntity name(String value) {
        this.name = Objects.requireNonNull(value);
        return this;
    }

    public TimelineEntity start(Limit value) {
        this.start = Objects.requireNonNull(value);
        return this;
    }

    public TimelineEntity end(Limit value) {
        this.end = Objects.requireNonNull(value);
        return this;
    }
}
