package pen.stencil;

import javafx.geometry.Bounds;
import javafx.geometry.Dimension2D;
import javafx.geometry.Point2D;

/**
 * A StencilBoundedShape is a shape that draws itself to fit a specified
 * bounding box (x, y, width, height), as adjusted by a {@link Tack} value.
 */
@SuppressWarnings("unchecked")
public abstract class StencilBoundedShape<Self extends StencilBoundedShape<Self>>
    extends StyleBase<Self>
    implements StencilShape
{
    //---------------------------------------------------------------------
    // Instance Variables

    // The shape's origin point (x,y)
    protected double x = 0.0;
    protected double y = 0.0;

    // The location of the shape's origin point relative to the bounding
    // box, i.e, where the tack is.
    protected Tack tack = Tack.NORTHWEST;

    // The size of the shape
    protected double w = 1.0;
    protected double h = 1.0;

    //---------------------------------------------------------------------
    // Constructor

    public StencilBoundedShape() {
        // Nothing to do
    }

    //---------------------------------------------------------------------
    // Protected API

    /**
     * Gets the bounds of the shape given its origin, tack, and size.
     * @return The bounds
     */
    protected Bounds getBounds() {
        return Pen.tack2bounds(tack, x, y, w, h);
    }

    //---------------------------------------------------------------------
    // DSL

    public Self at(Point2D point) {
        return at(point.getX(), point.getY());
    }

    public Self at(double x, double y) {
        this.x = x;
        this.y = y;
        return (Self)this;
    }

    public Self tack(Tack tack) {
        this.tack = tack;
        return (Self)this;
    }

    public Self size(Dimension2D size) {
        return size(size.getWidth(), size.getHeight());
    }

    public Self size(double w, double h) {
        this.w = w;
        this.h = h;
        return (Self)this;
    }
}
