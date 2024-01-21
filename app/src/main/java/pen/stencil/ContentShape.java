package pen.stencil;

import javafx.geometry.Bounds;
import javafx.geometry.Dimension2D;
import javafx.geometry.Point2D;

/**
 * A ContentShape is a shape that sizes itself to fit its content.
 * @param <Self> the concrete shape class
 */
@SuppressWarnings("unchecked")
public abstract class ContentShape<Self extends ContentShape<Self>>
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

    // The minimum size of the shape
    protected double minWidth = 1.0;
    protected double minHeight = 1.0;

    //---------------------------------------------------------------------
    // Constructor

    public ContentShape() {
        // Nothing to do
    }

    //---------------------------------------------------------------------
    // ContentShape API

    /**
     * Gets the actual size of the shape given its minimum size and content.
     * @return the size
     */
    public abstract Dimension2D getSize();

    /**
     * Gets the bounds of the shape given its origin, tack, and size.
     * @return The bounds
     */
    protected Bounds getBounds() {
        var size = getSize();
        return Pen.tack2bounds(tack, x, y, size.getWidth(), size.getHeight());
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

    public Dimension2D getMinSize() {
        return new Dimension2D(minWidth, minHeight);
    }

    /**
     * Gets the shape's tack position: the location of the origin relative
     * to the bounding box.
     * @return The tack
     */
    public Tack getTack() {
        return tack;
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
     * Sets the minimum size of the shape.
     * @param size The size
     * @return The shape
     */
    public Self minSize(Dimension2D size) {
        return minSize(size.getWidth(), size.getHeight());
    }

    /**
     * Sets the minimum size of the shape
     * @param minWidth The minimum width
     * @param minHeight The minimum height
     * @return The shape
     */
    public Self minSize(double minWidth, double minHeight) {
        this.minWidth = minWidth;
        this.minHeight = minHeight;
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
}
