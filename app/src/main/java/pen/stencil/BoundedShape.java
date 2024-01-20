package pen.stencil;

import javafx.geometry.Bounds;
import javafx.geometry.Dimension2D;
import javafx.geometry.Point2D;

/**
 * A StencilBoundedShape is a shape that draws itself to fit a specified
 * bounding box (x, y, width, height), as adjusted by a {@link Tack} value.
 * @param <Self> the concrete shape class
 */
@SuppressWarnings("unchecked")
public abstract class BoundedShape<Self extends BoundedShape<Self>>
    extends StyleBase<Self>
    implements Drawable
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

    public BoundedShape() {
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
    // Getters

    /**
     * Gets the shape's origin point
     * @return The point
     */
    public Point2D getAt() {
        return new Point2D(x, y);
    }

    /**
     * Gets the shape's tack position: the location of the origin relative
     * to the bounding box.
     * @return The tack
     */
    public Tack getTack() {
        return tack;
    }

    /**
     * Gets the shape's size.
     * @return The size
     */
    public Dimension2D getSize() {
        return new Dimension2D(w, h);
    }

    //---------------------------------------------------------------------
    // DSL

    /**
     * Sets the shape's origin point
     * @param point The point
     * @return The shape
     */
    public Self at(Point2D point) {
        return at(point.getX(), point.getY());
    }

    /**
     * Sets the shape's origin point
     * @param x The X coordinate
     * @param y The Y coordinate
     * @return The shape
     */
    public Self at(double x, double y) {
        this.x = x;
        this.y = y;
        return (Self)this;
    }

    /**
     * Sets the "tack position": the location of the origin point on the
     * bounding box. Defaults to Tack.NORTHWEST.
     * @param tack The tack
     * @return The shape
     */
    public Self tack(Tack tack) {
        this.tack = tack;
        return (Self)this;
    }

    /**
     * Sets the size of the shape.
     * @param size The size
     * @return The shape
     */
    public Self size(Dimension2D size) {
        return size(size.getWidth(), size.getHeight());
    }

    /**
     * Sets the size of the shape
     * @param w The width
     * @param h The height
     * @return The shape
     */
    public Self size(double w, double h) {
        this.w = w;
        this.h = h;
        return (Self)this;
    }
}
