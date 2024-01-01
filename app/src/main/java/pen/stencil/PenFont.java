package pen.stencil;

import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;

/**
 * A font, as retrieved by family, weight, posture, and size
 */
@SuppressWarnings("unused")
public final class PenFont {
    public static final PenFont SANS12 = new PenFont.Builder("sans12")
        .family("sans-serif").size(12).build();
    public static final PenFont SERIF12 = new PenFont.Builder("serif12")
        .family("serif").size(12).build();
    public static final PenFont MONO12 = new PenFont.Builder("mono12")
        .family("monospace").size(12).build();

    public static final String SANS = "sans-serif";
    public static final String SERIF = "serif";
    public static final String MONO = "monospace";

    //-------------------------------------------------------------------------
    // Instance Variables

    private final String name;
    private final String family;
    private final FontWeight weight;
    private final FontPosture posture;
    private final double size;
    private final Font font;

    //-------------------------------------------------------------------------
    // Constructor

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

    public String getName() {
        return name;
    }

    public String getFamily() {
        return family;
    }

    public FontWeight getWeight() {
        return weight;
    }

    public FontPosture getPosture() {
        return posture;
    }

    public double getSize() {
        return size;
    }

    public Font getRealFont() {
        return font;
    }

    public String getRealName() {
        return font.getName();
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

    public static class Builder {
        private final String name;
        private String family = "sans-serif";
        private FontWeight weight = FontWeight.NORMAL;
        private FontPosture posture = FontPosture.REGULAR;
        private double size = 12;

        public Builder(String name) {
            this.name = name;
        }

        public PenFont build() {
            return new PenFont(this);
        }

        public Builder family(String family) {
            this.family = family;
            return this;
        }

        public Builder weight(FontWeight weight) {
            this.weight = weight;
            return this;
        }

        public Builder posture(FontPosture posture) {
            this.posture = posture;
            return this;
        }

        public Builder size(double size) {
            this.size = size;
            return this;
        }
    }
}
