package pen.pen;

/**
 * A concrete Style for use in StyleMaps.
 */
public class Style extends StyleBase<Style> {
    //-------------------------------------------------------------------------
    // Constructor

    public Style() {
        // nothing to do
    }

    public Style(StyleBase<?> other) {
        copyStyleFrom(other);
    }
}
