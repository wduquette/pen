package pen.diagram.timeline;

/**
 * A Period is an interval of time associated with an Entity.
 */
public record Period(
    Entity entity,
    int start,
    int end,
    Cap startCap,
    Cap endCap
) {}
