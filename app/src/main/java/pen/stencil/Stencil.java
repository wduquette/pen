package pen.stencil;

import javafx.geometry.Bounds;
import javafx.geometry.Dimension2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.util.Optional;

@SuppressWarnings("unused")
public class Stencil {
    public static final double DEFAULT_MARGIN = 10;
    public static final double DEFAULT_MIN_WIDTH = 1;
    public static final double DEFAULT_MIN_HEIGHT = 1;

    //-------------------------------------------------------------------------
    // Instance Variables

    // The pen we do the drawing with
    private final Pen pen;

    // The margin for the right and bottom sides.  (The client is responsible
    // for leaving room at the top and left).
    private double margin = DEFAULT_MARGIN;

    // The background color.
    private Paint background = Color.WHITE;

    // The minimum size for an image produced using this Stencil
    private double minWidth = DEFAULT_MIN_WIDTH;
    private double minHeight = DEFAULT_MIN_HEIGHT;

    // The coordinate bounds of what we've drawn.
    private Bounds drawingBounds = null;

    //-------------------------------------------------------------------------
    // Constructor

    public Stencil(GraphicsContext context) {
        this.pen = new Pen(context);
    }

    //-------------------------------------------------------------------------
    // Getters


    public double getMargin() {
        return margin;
    }

    public Paint getBackground() {
        return background;
    }

    public double getMinWidth() {
        return minWidth;
    }

    public double getMinHeight() {
        return minHeight;
    }

    /**
     * Gets the accumulated bounding box of all drawing done so far.  Clients
     * can use this as desired, e.g., to set the size of the canvas before
     * drawing a finished image.
     * @return The bounding box, if any.
     */
    public Optional<Bounds> getDrawingBounds() {
        return Optional.ofNullable(drawingBounds);
    }

    /**
     * Gets the size of the finished image: from (0,0) to the maximum drawing
     * bounds, plus the margin on the right and bottom--but no less than the
     * minimum width and height. This will be the dimensions of the image file
     * saved by `StencilBuffer`
     * .
     * @return The size
     */
    public Dimension2D getImageSize() {
        var xMax = drawingBounds != null ? drawingBounds.getMaxX() : 0;
        var yMax = drawingBounds != null ? drawingBounds.getMaxY() : 0;

        var w = Math.max(minWidth, xMax + margin);
        var h = Math.max(minHeight, yMax + margin);

        return new Dimension2D(w, h);
    }

    //-------------------------------------------------------------------------
    // DSL

    public Pen pen() {
        return pen;
    }

    /**
     * Sets the background color.
     * @param color The color
     * @return The stencil
     */
    public Stencil background(Paint color) {
        this.background = color;
        return this;
    }

    /**
     * Clears the drawing using the current background color, along with all
     * computed bounds.
     * @return The stencil
     */
    public Stencil clear() {
        pen.clear();
        pen.clear(background);
        clearDrawingBounds();
        return this;
    }

    /**
     * Sets the background color and clears the drawing, along with all
     * computed bounds.
     * @param background The new background color
     * @return The stencil
     */
    public Stencil clear(Paint background) {
        background(background);
        pen.clear();
        pen.clear(background);
        clearDrawingBounds();
        return this;
    }

    public Stencil clearDrawingBounds() {
        drawingBounds = null;
        return this;
    }

    public Stencil addBounds(Bounds bounds) {
        // FIRST, apply current transformation
        bounds = pen.getTransform().transform(bounds);

        // NEXT, combine with the drawing bounds
        if (drawingBounds == null) {
            drawingBounds = bounds;
        } else {
            drawingBounds = Pen.boundsOf(drawingBounds, bounds);
        }
        return this;
    }

    /**
     * Draws a shape on the stencil, extending the drawing bounds accordingly.
     * @param shape The shape
     * @return The stencil
     */
    public Stencil draw(Drawable shape) {
        try {
            pen.save();
            var bounds = shape.draw(this);

            if (bounds !=  null) {
                addBounds(bounds);
            }
        } finally {
            pen.restore();
        }
        return this;
    }

    /**
     * Draws the drawing on the Stencil.  A drawing is a function that
     * draws using a stencil.  The Stencil's pen's drawing parameters are
     * saved before the drawing is done, and restored afterward.
     * @param drawing The drawing
     * @return The stencil
     */
    public Stencil draw(Drawing drawing) {
        try {
            pen.save();
            drawing.draw(this);
        } finally {
            pen.restore();
        }
        return this;
    }

    public Stencil margin(double value) {
        this.margin = value;
        return this;
    }

    public Stencil minHeight(double value) {
        this.minHeight = value;
        return this;
    }

    public Stencil minWidth(double value) {
        this.minWidth = value;
        return this;
    }

    //-------------------------------------------------------------------------
    // Transforms and Pen State Management

    /**
     * Saves the pen's drawing parameter settings onto the pen's stack.
     * @return The stencil
     */
    public Stencil savePen() {
        pen.save();
        return this;
    }

    /**
     * Restores the pen's saved drawing parameter settings.
     * @return The stencil
     */
    public Stencil restorePen() {
        pen.restore();
        return this;
    }

    /**
     * Resets the pen's drawing parameter settings to their initial values.
     * @return The stencil
     */
    public Stencil resetPen() {
        pen.reset();
        return this;
    }

    public Stencil translate(double x, double y) {
        pen.translate(x, y);
        return this;
    }

    public Stencil rotate(double degrees) {
        pen.rotate(degrees);
        return this;
    }

    public Stencil scale(double xFactor, double yFactor) {
        pen.scale(xFactor, yFactor);
        return this;
    }

    //-------------------------------------------------------------------------
    // Standard Shape Factories

    public static BoxedTextShape boxedText() {
        return new BoxedTextShape();
    }

    public static LineShape line() {
        return new LineShape();
    }

    public static RectangleShape rectangle() {
        return new RectangleShape();
    }

    public static SymbolShape symbol() {
        return new SymbolShape();
    }

    public static TextShape text() {
        return new TextShape();
    }


    //-------------------------------------------------------------------------
    // Helpers

}
