package pen.stencil;

import javafx.geometry.Bounds;

/**
 * A simple rectangle.
 */
public class RectangleShape
    extends BoundedShape<RectangleShape>
    implements Drawable
{
    //-------------------------------------------------------------------------
    // Constructor

    public RectangleShape() {
        // Nothing to do
    }

    //-------------------------------------------------------------------------
    // DSL

    // None needed

    //-------------------------------------------------------------------------
    // StencilShape API

    @Override
    public Bounds draw(Stencil stencil) {
        var box = getBounds();
        stencil.pen().save()
            .setFill(getBackground())
            .setStroke(getForeground())
            .setLineWidth(getLineWidth())
            .fillRect(
                box.getMinX(),  box.getMinY(),
                box.getWidth(), box.getHeight())
            .strokeRect(
                box.getMinX(),  box.getMinY(),
                box.getWidth(), box.getHeight())
            .restore();
        return box;
    }
}
