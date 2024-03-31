package pen.history;

public enum Cap {
    /**
     * An entity bar cap drawn with a solid line. Used for entities with a
     * definite end time within the time frame of interest.
     */
    HARD,

    /**
     * An entity bar cap drawn with an invisible line.  Used for entity
     * bars if the entity's lifetime extends past the end of the bar.
     */
    SOFT

}
