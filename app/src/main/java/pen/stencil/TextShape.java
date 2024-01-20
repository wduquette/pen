package pen.stencil;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;

public class TextShape
    extends SimpleShape<TextShape>
    implements Drawable
{
    //---------------------------------------------------------------------
    // Instance Variables

    private double x;
    private double y;
    private String text = "";
    private Tack tack = Tack.NORTHWEST;

    //---------------------------------------------------------------------
    // Constructor

    public TextShape() {
        // Nothing to do
    }

    //---------------------------------------------------------------------
    // DSL

    public TextShape text(String text) {
        this.text = text;
        return this;
    }

    public TextShape at(Point2D point) {
        return at(point.getX(), point.getY());
    }

    public TextShape at(double x, double y) {
        this.x = x;
        this.y = y;
        return this;
    }

    public TextShape tack(Tack tack) {
        this.tack = tack;
        return this;
    }

    public Bounds draw(Stencil stencil) {
        // FIRST, draw the text
        stencil.pen().save()
            .setFill(getTextColor())
            .setFont(getFont())
            .setTextBaseline(tack.vpos())
            .setTextAlign(tack.textAlign())
            .fillText(text, x, y)
            .restore();

        // NEXT, compute the bounds.
        var size = Pen.getTextSize(getFont(), text);
        return Pen.tack2bounds(tack, x, y, size.getWidth(), size.getHeight());
    }
}
