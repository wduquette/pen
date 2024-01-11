package pen.stencil;

import javafx.geometry.*;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.transform.Affine;

import java.util.List;

/**
 * A wrapper for the JavaFX GraphicsContext
 */
@SuppressWarnings("unused")
public class Pen {
    /**
     * Default font.
     */
    public static final PenFont DEFAULT_FONT = PenFont.SANS12;

    //-------------------------------------------------------------------------
    //  Instance Variables

    // The wrapped GraphicsContext
    private final GraphicsContext gc;

    // Height of the save/restore stack
    private int stackHeight = 0;

    //-------------------------------------------------------------------------
    // Constructor

    public Pen(GraphicsContext gc) {
        this.gc = gc;
        gc.setFont(DEFAULT_FONT.getRealFont());
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

    public Pen clear() {
        gc.clearRect(0, 0,
            gc.getCanvas().getWidth(),
            gc.getCanvas().getHeight());
        return this;
    }

    public Pen clear(Paint color) {
        gc.clearRect(0, 0,
            gc.getCanvas().getWidth(),
            gc.getCanvas().getHeight());
        gc.save();
        gc.setFill(color);
        gc.fillRect(0, 0,
            gc.getCanvas().getWidth(),
            gc.getCanvas().getHeight());
        gc.restore();
        return this;
    }

    //-------------------------------------------------------------------------
    // DSL: save and restore

    /**
     * Saves the drawing state.
     * @return The pen
     */
    public Pen save() {
        stackHeight++;
        gc.save();
        return this;
    }

    /**
     * Restores the previous drawing state.
     * @return The pen
     */
    public Pen restore() {
        if (stackHeight > 0) {
            stackHeight--;
            gc.restore();
        }
        return this;
    }

    /**
     * Resets the drawing state to its condition when the Pen was created.
     * @return The pen
     */
    public Pen reset() {
        while (stackHeight > 0) {
            restore();
        }

        return this;
    }

    /**
     * Gets the height of the save/restore stack.  If 0, the state of the Pen
     * is as it was when the Pen was created.  If greater than 1, then its value
     * is the number of times {@code save()} has been called without a matching
     * {@code restore()}.
     * @return The height.
     */
    public int getSaveStackHeight() {
        return stackHeight;
    }

    //-------------------------------------------------------------------------
    // DSL: Transforms
    //
    // Translations are applied in the order defined, and apply to all future
    // drawing.  Hence, one usually does this:
    //
    // - Save the pen state using pen.save().
    // - Translates to the desired point of rotation
    // - Scales by the desired amount in the X and Y directions.
    // - Rotates by the desired number of degrees
    // - Does the desired drawing.
    // - Restores the pen state.

    /**
     * Rotates future drawing by the given number of degrees,
     * rotating counter-clockwise.
     * @param degrees The number of degrees.
     * @return The pen
     */
    public Pen rotate(double degrees) {
        gc.rotate(degrees);
        return this;
    }

    /**
     * Scales future drawing by the given X and Y factors.
     * @param xFactor The X scaling
     * @param yFactor The Y scaling
     * @return The pen
     */
    public Pen scale(double xFactor, double yFactor) {
        gc.scale(xFactor, yFactor);
        return this;
    }

    /**
     * Translates future drawing by the given X and Y values in pixels
     * @param x The X delta
     * @param y The Y delta
     * @return The pen
     */
    public Pen translate(double x, double y) {
        gc.translate(x, y);
        return this;
    }

    /**
     * Gets the current aggregated transform.
     * @return The transform
     */
    public Affine getTransform() {
        return gc.getTransform();
    }


    //-------------------------------------------------------------------------
    // DSL: Style

    public Pen setFill(Paint color) {
        gc.setFill(color);
        return this;
    }

    public Pen setFont(Font font) {
        gc.setFont(font);
        return this;
    }

    public Pen setFont(PenFont font) {
        gc.setFont(font.getRealFont());
        return this;
    }

    public Pen setLineWidth(double pixels) {
        gc.setLineWidth(pixels);
        return this;
    }

    public Pen setTextAlign(TextAlignment align) {
        gc.setTextAlign(align);
        return this;
    }

    public Pen setTextBaseline(VPos baseline) {
        gc.setTextBaseline(baseline);
        return this;
    }

    public Pen setStroke(Paint color) {
        gc.setStroke(color);
        return this;
    }


    //-------------------------------------------------------------------------
    // Public API: Drawing

    //
    // Text
    //

    public Pen fillText(String text, double x, double y) {
        gc.fillText(text, x, y);
        return this;
    }

    public Pen strokeText(String text, double x, double y) {
        gc.strokeText(text, x, y);
        return this;
    }

    //
    // Lines
    //

    public Pen strokePolyline(double[] xPoints, double[] yPoints, int nPoints) {
        gc.strokePolyline(xPoints, yPoints, nPoints);
        return this;
    }

    public Pen strokePolyline(List<Point2D> points) {
        double[] xPoints = new double[points.size()];
        double[] yPoints = new double[points.size()];

        for (int i = 0; i < points.size(); i++) {
            xPoints[i] = points.get(i).getX();
            yPoints[i] = points.get(i).getY();
        }
        return strokePolyline(xPoints, yPoints, points.size());
    }

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

    //-------------------------------------------------------------------------
    // Static Helpers

    public static Dimension2D getTextSize(PenFont font, String text) {
        var node = new Text();
        node.setText(text);
        node.setFont(font.getRealFont());
        var bounds = node.getLayoutBounds();
        return new Dimension2D(bounds.getWidth(), bounds.getHeight());
    }

    public static double getTextWidth(PenFont font, String text) {
        var node = new Text();
        node.setText(text);
        node.setFont(font.getRealFont());
        var bounds = node.getLayoutBounds();
        return bounds.getWidth();
    }

    public static double getTextHeight(PenFont font, String text) {
        var node = new Text();
        node.setText(text);
        node.setFont(font.getRealFont());
        var bounds = node.getLayoutBounds();
        return bounds.getHeight();
    }

    /**
     * Given an origin point and tack value for a region of a given size,
     * return the actual bounding box.  Note:
     * @param tack  The tack, e.g., NORTHWEST
     * @param x  The origin point's X coordinate
     * @param y  The origin point's Y coordinate
     * @param w  The width of the region
     * @param h  The height of the region
     * @return The bounds
     */
    public static Bounds tack2bounds(
        Tack tack,
        double x, double y,
        double w, double h
    ) {
        var x0 = switch (tack.hpos()) {
            case LEFT -> x;
            case CENTER -> x - w/2.0;
            case RIGHT -> x - w;
        };

        // Note: tack.vpos() will never actually return BASELINE
        var y0 = switch (tack.vpos()) {
            case TOP -> y;
            case CENTER -> y - h/2.0;
            case BASELINE, BOTTOM -> y - h;
        };
        return new BoundingBox(x0, y0, w, h);
    }

    /**
     * Computes a bounding box just large enough to contain both boxes.
     * @param a The first box
     * @param b The second box
     * @return The combined bounds
     */
    public static Bounds boundsOf(Bounds a, Bounds b) {
        var x0 = Math.floor(Math.min(a.getMinX(), b.getMinX()));
        var x1 = Math.ceil(Math.max(a.getMaxX(), b.getMaxX()));
        var y0 = Math.floor(Math.min(a.getMinY(), b.getMinY()));
        var y1 = Math.ceil(Math.max(a.getMaxY(), b.getMaxY()));

        return new BoundingBox(x0, y0, x1 - x0, y1 - y0);
    }

    /**
     * Computes a bounding box just large enough to contain all the points.
     * @param points The points
     * @return The bounds, or null if there are no points
     */
    public static Bounds boundsOf(List<Point2D> points) {
        if (points.isEmpty()) {
            return null;
        }

        var x0 = points.getFirst().getX();
        var x1 = points.getFirst().getX();
        var y0 = points.getFirst().getY();
        var y1 = points.getFirst().getY();

        for (var p : points) {
            x0 = Math.min(x0, p.getX());
            x1 = Math.max(x1, p.getX());
            y0 = Math.min(y0, p.getY());
            y1 = Math.max(y1, p.getY());
        }

        return new BoundingBox(x0, y0, x1 - x0, y1 - y0);
    }

    public static Bounds addMargin(Bounds bounds, double margin) {
        var x = bounds.getMinX() - margin;
        var y = bounds.getMinY() - margin;
        var w = bounds.getWidth() + 2*margin;
        var h = bounds.getHeight() + 2*margin;
        return new BoundingBox(x, y, w, h);
    }
}
