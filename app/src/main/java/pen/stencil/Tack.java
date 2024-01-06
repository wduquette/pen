package pen.stencil;

import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;

/**
 * A shape's Tack determines the location of its origin point relative to the
 * shape as a whole, i.e., the location of the tack used to tack it to the
 * drawing.
 */
public enum Tack {
    CENTER(Pos.CENTER),
    NORTH(Pos.TOP_CENTER),
    NORTHEAST(Pos.TOP_RIGHT),
    EAST(Pos.CENTER_RIGHT),
    SOUTHEAST(Pos.BOTTOM_RIGHT),
    SOUTH(Pos.BOTTOM_CENTER),
    SOUTHWEST(Pos.BOTTOM_LEFT),
    WEST(Pos.CENTER_LEFT),
    NORTHWEST(Pos.TOP_LEFT);

    private final Pos pos;

    Tack(Pos pos) {
        this.pos = pos;
    }

    public Pos pos() {
        return pos;
    }

    public HPos hpos() {
        return pos.getHpos();
    }

    public VPos vpos() {
        return pos.getVpos();
    }
}
