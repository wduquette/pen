package pen.stencil;

import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.text.TextAlignment;

/**
 * A shape's Tack determines the location of its origin point relative to the
 * shape's bounding box, i.e., the location of the tack used to tack it to the
 * drawing.
 */
public enum Tack {
    CENTER   (Pos.CENTER,        TextAlignment.CENTER),
    NORTH    (Pos.TOP_CENTER,    TextAlignment.CENTER),
    NORTHEAST(Pos.TOP_RIGHT,     TextAlignment.RIGHT),
    EAST     (Pos.CENTER_RIGHT,  TextAlignment.RIGHT),
    SOUTHEAST(Pos.BOTTOM_RIGHT,  TextAlignment.RIGHT),
    SOUTH    (Pos.BOTTOM_CENTER, TextAlignment.CENTER),
    SOUTHWEST(Pos.BOTTOM_LEFT,   TextAlignment.LEFT),
    WEST     (Pos.CENTER_LEFT,   TextAlignment.LEFT),
    NORTHWEST(Pos.TOP_LEFT,      TextAlignment.LEFT);

    // The Pos value corresponding to the Tack
    private final Pos pos;

    // The TextAlignment corresponding to the Tack
    private final TextAlignment textAlign;

    Tack(Pos pos, TextAlignment textAlign) {
        this.pos = pos;
        this.textAlign = textAlign;
    }

    /**
     * Gets the Pos value equivalent to this Tack
     * @return The Pos.
     */
    public Pos pos() {
        return pos;
    }

    /**
     * Gets the HPos value implied by this tack
     * @return The HPos
     */
    public HPos hpos() {
        return pos.getHpos();
    }

    /**
     * Gets the VPos value implied by this tack
     * @return The VPos
     */
    public VPos vpos() {
        return pos.getVpos();
    }

    /**
     * Gets the TextAlignment value implied by this tack
     * @return The alignment
     */
    public TextAlignment textAlign() {
        return textAlign;
    }
}
