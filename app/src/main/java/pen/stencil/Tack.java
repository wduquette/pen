package pen.stencil;

import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.text.TextAlignment;

/**
 * A shape's Tack determines the location of its origin point relative to the
 * shape as a whole, i.e., the location of the tack used to tack it to the
 * drawing.
 */
public enum Tack {
    CENTER(Pos.CENTER, TextAlignment.CENTER),
    NORTH(Pos.TOP_CENTER, TextAlignment.CENTER),
    NORTHEAST(Pos.TOP_RIGHT, TextAlignment.RIGHT),
    EAST(Pos.CENTER_RIGHT, TextAlignment.RIGHT),
    SOUTHEAST(Pos.BOTTOM_RIGHT, TextAlignment.RIGHT),
    SOUTH(Pos.BOTTOM_CENTER, TextAlignment.CENTER),
    SOUTHWEST(Pos.BOTTOM_LEFT, TextAlignment.LEFT),
    WEST(Pos.CENTER_LEFT, TextAlignment.LEFT),
    NORTHWEST(Pos.TOP_LEFT, TextAlignment.LEFT);

    private final Pos pos;
    private final TextAlignment textAlign;

    Tack(Pos pos, TextAlignment textAlign) {
        this.pos = pos;
        this.textAlign = textAlign;
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

    public TextAlignment textAlign() {
        return textAlign;
    }
}
