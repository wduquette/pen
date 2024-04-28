package pen.history;

/**
 * An entity type in a history.
 * @param id The type's ID.
 * @param name The type's display name
 * @param prime Whether the type is of primary importance or not.
 */
public record EntityType(
    String id,
    String name,
    boolean prime
) { }
