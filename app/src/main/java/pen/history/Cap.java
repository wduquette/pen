package pen.history;

public enum Cap {
    /**
     * An entity bar cap drawn with a solid line. Used for entities with a
     * definite end time.
     */
    HARD,

    /**
     * An entity bar cap drawn with an invisible line.  Used for entity
     * bars that run past the end of the diagram.
     */
    SOFT,

    /**
     * A cap that extends past the limit in a fuzzy way.  Used for entities
     * whose lifetime extends before or after the time interval of interest.
     */
    FUZZY
}
