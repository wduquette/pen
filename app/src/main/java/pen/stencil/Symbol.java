package pen.stencil;

/**
 * Symbols for use as arrowheads, map symbols, etc.  All symbols that point,
 * point at their (x,y) point.
 */
public enum Symbol {
    /**
     * No symbol at all.
     */
    NONE,

    /**
     * A solid triangular arrowhead filled with the foreground color. The
     * arrowhead points to the left, with its origin at its tip.
     */
    ARROW_SOLID,

    /**
     * A simple open arrowhead. The arrowhead points to the left, with its
     * origin at its tip.
     */
    ARROW_OPEN,

    /**
     * A small dot filled with the foreground color, centered at its origin.
     */
    DOT_SOLID,

    /**
     * A small dot filled with the foreground color, tangent to its origin
     * point which is on dot's left edge.
     */
    DOT_SOLID_OFFSET,

    /**
     * A small dot filled with the background color, centered at its origin.
     */
    DOT_OPEN,


    /**
     * A small dot filled with the background color, tangent to its origin
     * point which is on dot's left edge.
     */
    DOT_OPEN_OFFSET
}
