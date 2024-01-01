package pen.stencil;

import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;

/**
 * A font, as retrieved by family, weight, posture, and size.  PenFont is for
 * use with {@link Pen} and {@link Stencil}, and wraps instances of the
 * JavaFX {@code Font} class.
 *
 * <h3>Rationale</h3>
 *
 * <p>JavaFX lets you request a JavaFX {@code Font} by family, weight, posture,
 * and size.  The created font knows its size, but it does not know its weight
 * or posture, and while it knows its "family" the reported string is going to
 * be what was found in the font file rather than in your request.  For example,
 * you can request the `monospace` family, but the returned font (on my
 * machine) has family `Monospaced`.</p>
 *
 * <p>PenFont is intended to used by a Tcl scripting API; we want access to
 * the details of the "real" font (certainly) but we also want to remember
 * why the user asked for.</p>
 *
 * <h3>Warning</h3>
 *
 * <p>The user is free to request any combination of font family, weight,
 * and posture.  However, there are issues.</p>
 *
 * <ul>
 * <li>JavaFX will always provide a font.  If the requested font is not found,
 * you'll get something else.</li>
 * <li>
 * JavaFX guarantees the presence of the {@code sans-serif}, {@code serif}, and
 * {@code monospace} font families.
 * </li>
 * <li>
 * In practice, it seems that JavaFX only honors weight and posture choices for
 * the {@code sans-serif}, {@code serif}, and {@code monospace} font families.
 * </li>
 * </ul>
 */
@SuppressWarnings("unused")
public final class PenFont {
    /** Sans-serif 12 font. */
    public static final PenFont SANS12 = new PenFont.Builder("sans12")
        .family("sans-serif").size(12).build();

    /** Serif 12 font. */
    public static final PenFont SERIF12 = new PenFont.Builder("serif12")
        .family("serif").size(12).build();

    /** Monospace 12 font. */
    public static final PenFont MONO12 = new PenFont.Builder("mono12")
        .family("monospace").size(12).build();

    //-------------------------------------------------------------------------
    // Instance Variables

    // The name, as assigned by the user
    private final String name;

    // The font family, as requested by the user.
    private final String family;

    // The font weight, e.g., {@code NORMAL} or {@code BOLD}, as requested by
    // the user.
    private final FontWeight weight;

    // The font posture, e.g., {@code REGULAR} or {@code ITALIC}, as requested
    // by the user.
    private final FontPosture posture;

    // The font size.
    private final double size;

    // The Font provided by JavaFX
    private final Font font;

    //-------------------------------------------------------------------------
    // Constructor

    // Creates the font given the builder.
    private PenFont(Builder builder) {
        this.name = builder.name;
        this.family = builder.family;
        this.weight = builder.weight;
        this.posture = builder.posture;
        this.size = builder.size;
        this.font = Font.font(family, weight, posture, size);
    }

    //-------------------------------------------------------------------------
    // Getters

    /**
     * Gets the font's name, as assigned by the client when the font was
     * created.
     * @return The name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the font's family, as requested by the client.
     * @return The family
     */
    public String getFamily() {
        return family;
    }

    /**
     * Gets the font's weight, as requested by the client.
     * @return The weight
     */
    public FontWeight getWeight() {
        return weight;
    }

    /**
     * Gets the font's posture, as requested by the client.
     * @return The posture
     */
    public FontPosture getPosture() {
        return posture;
    }

    /**
     * Gets the font's size, as requested by the client.
     * @return The size
     */
    public double getSize() {
        return size;
    }

    /**
     * Gets the font provided by JavaFX in response the user's requests.
     * @return The font.
     */
    public Font getRealFont() {
        return font;
    }

    @Override
    public String toString() {
        return "StencilFont[" + family + "," + weight + "," + posture + "," +
            size + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PenFont that = (PenFont) o;

        if (Double.compare(that.size, size) != 0) return false;
        if (!name.equals(that.name)) return false;
        if (!family.equals(that.family)) return false;
        if (weight != that.weight) return false;
        if (posture != that.posture) return false;
        return font.equals(that.font);
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = name.hashCode();
        result = 31 * result + family.hashCode();
        result = 31 * result + weight.hashCode();
        result = 31 * result + posture.hashCode();
        temp = Double.doubleToLongBits(size);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + font.hashCode();
        return result;
    }

    //-------------------------------------------------------------------------
    // Builder

    /**
     * Builds a new PenFont.
     */
    public static class Builder {
        private final String name;
        private String family = "sans-serif";
        private FontWeight weight = FontWeight.NORMAL;
        private FontPosture posture = FontPosture.REGULAR;
        private double size = 12;

        /**
         * Creates the builder and sets the font's name.
         * @param name The name
         */
        public Builder(String name) {
            this.name = name;
        }

        /**
         * Builds the font as requested.
         * @return The font
         */
        public PenFont build() {
            return new PenFont(this);
        }

        /**
         * Requests a specific font family; otherwise, defaults to
         * {@code sans-serif}.
         * @param family The family
         * @return The builder
         */
        public Builder family(String family) {
            this.family = family;
            return this;
        }

        /**
         * Requests a specific font weight; otherwise, defaults to
         * {@code NORMAL}.
         * @param weight The weight
         * @return The builder
         */
        public Builder weight(FontWeight weight) {
            this.weight = weight;
            return this;
        }

        /**
         * Requests a specific font posture; otherwise, defaults to
         * {@code REGULAR}.
         * @param posture The posture
         * @return The builder
         */
        public Builder posture(FontPosture posture) {
            this.posture = posture;
            return this;
        }

        /**
         * Requests a specific font size; otherwise, defaults to 12.
         * @param size The size
         * @return The builder
         */
        public Builder size(double size) {
            this.size = size;
            return this;
        }
    }
}
