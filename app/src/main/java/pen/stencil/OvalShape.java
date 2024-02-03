package pen.stencil;

import javafx.geometry.Bounds;

/**
 * A simple oval or circle.
 */
public class OvalShape
    extends BoundedShape<OvalShape>
    implements Drawable
{
    //-------------------------------------------------------------------------
    // Constructor

    public OvalShape() {
        // Nothing to do
    }

    //-------------------------------------------------------------------------
    // DSL

    /**
     * Sets the size for a circle with the given radius.
     * @param pixels The radius in pixels
     * @return The shape
     */
    public OvalShape radius(double pixels) {
        return size(2*pixels, 2*pixels);
    }

    /**
     * Sets the size for a circle with the given diameter
     * @param pixels The diameter in pixels
     * @return The shape
     */
    public OvalShape diameter(double pixels) {
        return size(pixels, pixels);
    }

    //-------------------------------------------------------------------------
    // StencilShape API

    @Override
    public Bounds draw(Stencil stencil) {
        var box = getBounds();
        stencil.pen().save()
            .setFill(getBackground())
            .setStroke(getForeground())
            .setLineWidth(getLineWidth())
            .fillOval(
                box.getMinX(),  box.getMinY(),
                box.getWidth(), box.getHeight())
            .strokeOval(
                box.getMinX(),  box.getMinY(),
                box.getWidth(), box.getHeight())
            .restore();
        return box;
    }
}
