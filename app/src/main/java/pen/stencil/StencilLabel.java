package pen.stencil;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;

public class StencilLabel
    extends StyleBase<StencilLabel>
    implements StencilShape
{
    //---------------------------------------------------------------------
    // Instance Variables

    private double x;
    private double y;
    private String text = "";
    private Pos pos = Pos.TOP_LEFT;

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

    public StencilLabel pos(Pos pos) {
        this.pos = pos;
        return this;
    }

    public Bounds draw(Stencil stencil) {
        // FIRST, draw the text
        stencil.pen().save()
            .setFill(getTextColor())
            .setFont(getFont().getRealFont())
            .setTextBaseline(pos.getVpos())
            .setTextAlign(Pen.pos2textAlign(pos))
            .fillText(text, x, y)
            .restore();

        // NEXT, compute the bounds.
        var size = Pen.getTextSize(getFont().getRealFont(), text);
        return Pen.pos2bounds(pos, x, y, size.getWidth(), size.getHeight());
    }
}
