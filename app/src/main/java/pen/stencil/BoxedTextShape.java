package pen.stencil;

import javafx.geometry.Bounds;
import javafx.geometry.Dimension2D;

/**
 * A simple rectangle containing a text string.
 */
@SuppressWarnings("unused")
public class BoxedTextShape
    extends ContentShape<BoxedTextShape>
    implements Drawable
{
    /**
     * The default pad in pixels between the box's border and the text.
     */
    public static final double DEFAULT_PAD = 3;

    //-------------------------------------------------------------------------
    // Instance Variables

    // The pad around the text string, in pixels
    private double pad = DEFAULT_PAD;

    // The text to display
    private String text = "";


    //-------------------------------------------------------------------------
    // Constructor

    public BoxedTextShape() {
        // Nothing to do
    }

    //-------------------------------------------------------------------------
    // Accessors

    public double getPad() {
        return pad;
    }

    public String getText() {
        return text;
    }

    //-------------------------------------------------------------------------
    // DSL

    public BoxedTextShape pad(double pad) {
        this.pad = pad;
        return this;
    }

    public BoxedTextShape text(String text) {
        this.text = text;
        return this;
    }

    //-------------------------------------------------------------------------
    // Content Shape API

    @Override
    public Dimension2D getRealSize() {
        var textSize = Pen.getTextSize(getFont(), text);
        var minSize = getMinSize();
        var w = 2*pad + textSize.getWidth();
        var h = 2*pad + textSize.getHeight();
        return new Dimension2D(w, h);
    }

    //-------------------------------------------------------------------------
    // Drawable API

    @Override
    public Bounds draw(Stencil stencil) {
        var box = getBounds();
        stencil.draw(Stencil.rectangle()
            .style(this)
            .at(box.getMinX(), box.getMinY())
            .size(box.getWidth(), box.getHeight())
            .tack(Tack.NORTHWEST)
        );
        stencil.draw(Stencil.text()
            .at(box.getCenterX(), box.getCenterY())
            .text(text)
            .tack(Tack.CENTER)
        );
        return box;
    }
}
