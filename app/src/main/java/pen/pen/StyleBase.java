package pen.pen;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;

/**
 * A collection of style parameters used by Stencil shapes.  It
 * provides the API for retrieving style parameters and the DSL for setting
 * them. Most Stencil shapes subclass style.
 *
 * <p><b>Note:</b> Subclasses should be generic in themselves, i.e., Self should
 * be the subclass itself; this allows the DSL to work properly</p>
 *
 * @param <Self> The concrete subclass type
 */
@SuppressWarnings({"unused", "unchecked"})
public class StyleBase<Self extends StyleBase<Self>> {
    //-------------------------------------------------------------------------
    // Instance Variables

    private Paint background = Color.WHITE;
    private Font font = Pen.DEFAULT_FONT;
    private Paint foreground = Color.BLACK;
    private double lineWidth = 1;
    private Paint textColor = Color.BLACK;

    //-------------------------------------------------------------------------
    // Constructor

    // None

    //-------------------------------------------------------------------------
    // Management

    /**
     * Copies all style parameter values from the other style object into this
     * one.
     * @param other Another style object.
     */
    public final void copyStyleFrom(StyleBase<?> other) {
        this.background = other.background;
        this.font = other.font;
        this.foreground = other.foreground;
        this.lineWidth = other.lineWidth;
        this.textColor = other.textColor;
    }

    //-------------------------------------------------------------------------
    // Getters

    public Paint getBackground() {
        return background;
    }

    public Font getFont() {
        return font;
    }

    public Paint getForeground() {
        return foreground;
    }

    public double getLineWidth() {
        return lineWidth;
    }

    public Paint getTextColor() {
        return textColor;
    }

    //-------------------------------------------------------------------------
    // DSL

    public Self style(StyleBase<?> other) {
        copyStyleFrom(other);
        return (Self)this;
    }

    public Self background(Paint color) {
        this.background = color;
        return (Self)this;
    }

    public Self font(Font font) {
        this.font = font;
        return (Self)this;
    }

    public Self foreground(Paint color) {
        this.foreground = color;
        return (Self)this;
    }

    public Self lineWidth(double pixels) {
        this.lineWidth = pixels;
        return (Self)this;
    }
}
