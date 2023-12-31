package pen.stencil;

import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Dimension2D;
import javafx.geometry.Point2D;

public class StencilRect
    extends StyleBase<StencilRect>
    implements StencilShape
{
    //---------------------------------------------------------------------
    // Instance Variables

    private double x;
    private double y;
    private double w;
    private double h;

    //---------------------------------------------------------------------
    // Constructor

    public StencilRect() {
        // Nothing to do
    }

    //---------------------------------------------------------------------
    // DSL

    public StencilRect at(Point2D point) {
        return at(point.getX(), point.getY());
    }

    public StencilRect at(double x, double y) {
        this.x = x;
        this.y = y;
        return this;
    }

    public StencilRect size(Dimension2D size) {
        return size(size.getWidth(), size.getHeight());
    }

    public StencilRect size(double w, double h) {
        this.w = w;
        this.h = h;
        return this;
    }

    public Bounds draw(Stencil stencil) {
        stencil.pen().save()
            .setFill(getBackground())
            .setStroke(getForeground())
            .setLineWidth(getLineWidth())
            .fillRect(x, y, w, h)
            .strokeRect(x, y, w, h)
            .restore();
        return new BoundingBox(x, y, w, h);
    }
}
