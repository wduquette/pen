package pen.stencil;

import javafx.geometry.Bounds;

/**
 * A simple rectangle.
 */
public class StencilRect
    extends StencilBoundedShape<StencilRect>
    implements StencilShape
{
    //-------------------------------------------------------------------------
    // Constructor

    public StencilRect() {
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
