package pen.diagram.timeline;

/**
 * An entity in a history.
 * @param id The entity's unique ID
 * @param name An entity name for display
 * @param type The entity's type, for grouping.
 */
public record Entity(
    String id,
    String name,
    String type
) {
}
