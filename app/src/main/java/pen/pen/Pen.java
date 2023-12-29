package pen.pen;

import javafx.geometry.Dimension2D;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

/**
 * A wrapper for the JavaFX GraphicsContext
 */
@SuppressWarnings("unused")
public class Pen {
    /**
     * Default font.
     */
    public static final Font DEFAULT_FONT = Font.font("sans-serif", 12);

    //-------------------------------------------------------------------------
    //  Instance Variables

    private final GraphicsContext gc;

    //-------------------------------------------------------------------------
    // Constructor

    public Pen(GraphicsContext gc) {
        this.gc = gc;
        gc.setFont(DEFAULT_FONT);
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

    //-------------------------------------------------------------------------
    // DSL: save and restore

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

    public Pen setFill(Paint color) {
        gc.setFill(color);
        return this;
    }

    public Pen setFont(Font font) {
        gc.setFont(font);
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

    public static Dimension2D getTextSize(Font font, String text) {
        var node = new Text();
        node.setText(text);
        node.setFont(font);
        var bounds = node.getLayoutBounds();
        return new Dimension2D(bounds.getWidth(), bounds.getHeight());
    }

    public static double getTextWidth(Font font, String text) {
        var node = new Text();
        node.setText(text);
        node.setFont(font);
        var bounds = node.getLayoutBounds();
        return bounds.getWidth();
    }

    public static double getTextHeight(Font font, String text) {
        var node = new Text();
        node.setText(text);
        node.setFont(font);
        var bounds = node.getLayoutBounds();
        return bounds.getHeight();
    }

    public static TextAlignment pos2textAlign(Pos pos) {
        return pos2textAlign(pos.getHpos());
    }

    public static TextAlignment pos2textAlign(HPos hpos) {
        return switch (hpos) {
            case CENTER -> TextAlignment.CENTER;
            case LEFT -> TextAlignment.LEFT;
            case RIGHT -> TextAlignment.RIGHT;
        };
    }
}
