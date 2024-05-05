package pen.history2;

/**
 * An entity in the history.  Entities are used as keys; entity IDs are used
 * for scripting and data entry.
 * This record defines and names the entity; other
 * information (e.g., the entity's life span) derives from relations.
 * If data is stored as relations, possibly this doesn't need to be a sealed
 * interface; maybe we need a normal EntityType enum.  However, we might want
 * to be able to define entity subtypes in a given history, i.e., different
 * kinds of period as well as different periods.
 */
public sealed interface Entity
    permits Entity.Person, Entity.Place, Entity.Period, Entity.Group
{
    /**
     * Every entity has a unique ID.  IDs are generally mnemonic.
     * @return The ID
     */
    String id();

    /**
     * Every entity has name, preferably unique; but an entity's name can
     * in theory change over time.
     * @return The name
     */
    String name();

    /** An individual. */
    record Person(
        String id,
        String name
    ) implements Entity {
        // I have defined this a record, but a person's name, place, and group
        // membership can change over time.  The name changes probably aren't
        // relevant here, but the place and group membership are, as is the
        // person's life-span.  Probably these should be held in an extended
        // Person record, or as relations.
    }

    /**
     * Defines in a place.
     * @param id The unique ID
     * @param name The display name
     * @param within A containing place
     */
    record Place(
        String id,
        String name,
        Place within
    ) implements Entity {
        // Probably "within" should be defined as a relation.
    }

    /**
     * A named time period, e.g., a war.
     * @param id The unique ID
     * @param name The display name
     */
    record Period(
        String id,
        String name
    ) implements Entity {}

    /** A group of people: a family, a club, a political party, a company. */
    record Group(
        String id,
        String name
    ) implements Entity {
        // In theory, a group could also be a group of groups, e.g.,
        // the U.N.
    }
}
