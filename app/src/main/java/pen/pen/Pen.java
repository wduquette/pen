package pen.pen;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Paint;

/**
 * A wrapper for the JavaFX GraphicsContext
 */
@SuppressWarnings("unused")
public class Pen {
    //-------------------------------------------------------------------------
    //  Instance Variables

    private final GraphicsContext gc;

    //-------------------------------------------------------------------------
    // Constructor

    public Pen(GraphicsContext context) {
        this.gc = context;
    }

    //-------------------------------------------------------------------------
    // Public API: General

    /**
     * Gets the underlying GraphicsContext as an escape hatch.
     * @return The context
     */
    public GraphicsContext gc() {
        return gc;
    }

    //-------------------------------------------------------------------------
    // Public API: save and restore

    public Pen save() {
        gc.save();
        return this;
    }

    public Pen restore() {
        gc.restore();
        return this;
    }

    //-------------------------------------------------------------------------
    // Public API: Style

    public Pen setLineWidth(double pixels) {
        gc.setLineWidth(pixels);
        return this;
    }

    public Pen setStroke(Paint color) {
        gc.setStroke(color);
        return this;
    }

    public Pen setFill(Paint color) {
        gc.setFill(color);
        return this;
    }

    //-------------------------------------------------------------------------
    // Public API: Drawing

    //
    // Rectangles
    //

    public Pen fillRect(double x, double y, double w, double h) {
        gc.fillRect(x, y, w, h);
        return this;
    }

    public Pen strokeRect(double x, double y, double w, double h) {
        gc.strokeRect(x, y, w, h);
        return this;
    }

    public Pen strokeLine(double x1, double y1, double x2, double y2) {
        gc.strokeLine(x1, y1, x2, y2);
        return this;
    }

}
