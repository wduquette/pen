package pen.pen;

import javafx.geometry.Bounds;
import javafx.scene.canvas.GraphicsContext;

import java.util.Optional;

@SuppressWarnings("unused")
public class Stencil {
    //-------------------------------------------------------------------------
    // Instance Variables

    // The pen we do the drawing with
    private final Pen pen;

    // The coordinate bounds of what we've drawn.
    private Bounds drawingBounds = null;

    //-------------------------------------------------------------------------
    // Constructor

    public Stencil(GraphicsContext context) {
        this.pen = new Pen(context);
    }

    //-------------------------------------------------------------------------
    // Getters

    /**
     * Gets the accumulated bounding box of all drawing done so far.  Clients
     * can use this as desired, e.g., to set the size of the canvas before
     * drawing a finished image.
     * @return The bounding box, if any.
     */
    public Optional<Bounds> getDrawingBounds() {
        return Optional.ofNullable(drawingBounds);
    }

    //-------------------------------------------------------------------------
    // DSL

    public Pen pen() {
        return pen;
    }

    /**
     * Clears the drawing, and all computed bounds.
     * @return The stencil
     */
    public Stencil clear() {
        pen.clear();
        clearDrawingBounds();
        return this;
    }

    public Stencil clearDrawingBounds() {
        drawingBounds = null;
        return this;
    }

    public static StencilLabel label() {
        return new StencilLabel();
    }

    public static StencilRect rect() {
        return new StencilRect();
    }

    public Stencil draw(StencilShape shape) {
        var bounds = shape.draw(this);

        if (drawingBounds == null) {
            drawingBounds = bounds;
        } else {
            drawingBounds = Pen.boundsOf(drawingBounds, bounds);
        }
        return this;
    }

    //-------------------------------------------------------------------------
    // Helpers

}
