package pen.stencil;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;

public class StencilLabel
    extends StyleBase<StencilLabel>
    implements StencilShape
{
    //---------------------------------------------------------------------
    // Instance Variables

    private double x;
    private double y;
    private String text = "";
    private Tack tack = Tack.NORTHWEST;

    //---------------------------------------------------------------------
    // Constructor

    public StencilLabel() {
        // Nothing to do
    }

    //---------------------------------------------------------------------
    // DSL

    public StencilLabel text(String text) {
        this.text = text;
        return this;
    }

    public StencilLabel at(Point2D point) {
        return at(point.getX(), point.getY());
    }

    public StencilLabel at(double x, double y) {
        this.x = x;
        this.y = y;
        return this;
    }

    public StencilLabel tack(Tack tack) {
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
