package pen.stencil;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;

import java.util.List;

/**
 * Draws a given symbol. Symbols that imply a direction (i.e., arrowheads)
 * are drawn pointing to the right, i.e., at an angle of 0 degrees.
 */
@SuppressWarnings("unused")
public class SymbolShape
    extends SimpleShape<SymbolShape>
    implements Drawable
{
    //---------------------------------------------------------------------
    // Instance Variables

    // The symbol to draw
    private Symbol symbol = Symbol.NONE;

    // Its origin
    private double x;
    private double y;

    //---------------------------------------------------------------------
    // Constructor

    public SymbolShape() {
        // Nothing to do
    }

    //---------------------------------------------------------------------
    // DSL

    public SymbolShape symbol(Symbol symbol) {
        this.symbol = symbol;
        return this;
    }

    public SymbolShape at(Point2D point) {
        return at(point.getX(), point.getY());
    }

    public SymbolShape at(double x, double y) {
        this.x = x;
        this.y = y;
        return this;
    }

    public Bounds draw(Stencil stencil) {
        return switch (symbol) {
            case NONE -> null;
            case ARROW_SOLID -> drawArrowSolid(stencil);
            case ARROW_OPEN -> drawArrowOpen(stencil);
            case DOT_SOLID -> drawDotSolid(stencil);
            case DOT_SOLID_OFFSET -> drawDotSolidOffset(stencil);
            case DOT_OPEN -> drawDotOpen(stencil);
            case DOT_OPEN_OFFSET -> drawDotOpenOffset(stencil);
        };
    }

    public Bounds drawArrowSolid(Stencil sten) {
        var w = 12;
        var h = 8;
        var box = Pen.tack2bounds(Tack.EAST, x, y, w, h);

        sten.pen()
            .setFill(getForeground())
            .fillPolygon(List.of(
                new Point2D(box.getMinX(), box.getMinY()),
                new Point2D(box.getMaxX(), box.getCenterY()),
                new Point2D(box.getMinX(), box.getMaxY())
            ));
        return box;
    }

    public Bounds drawArrowOpen(Stencil sten) {
        var w = 12;
        var h = 8;
        var box = Pen.tack2bounds(Tack.EAST, x, y, w, h);

        sten.pen()
            .setStroke(getForeground())
            .strokePolyline(List.of(
                new Point2D(box.getMinX(), box.getMinY()),
                new Point2D(box.getMaxX(), box.getCenterY()),
                new Point2D(box.getMinX(), box.getMaxY())
            ));
        return box;
    }

    public Bounds drawDotSolid(Stencil sten) {
        var w = 6;
        var h = 6;
        var box = Pen.tack2bounds(Tack.CENTER, x, y, w, h);

        sten.pen()
            .setFill(getForeground())
            .setStroke(getForeground())
            .fillOval(box)
            .strokeOval(box)
        ;

        return box;
    }

    public Bounds drawDotSolidOffset(Stencil sten) {
        var w = 6;
        var h = 6;
        var box = Pen.tack2bounds(Tack.EAST, x, y, w, h);

        sten.pen()
            .setFill(getForeground())
            .setStroke(getForeground())
            .fillOval(box)
            .strokeOval(box)
        ;

        return box;
    }

    public Bounds drawDotOpen(Stencil sten) {
        var w = 6;
        var h = 6;
        var box = Pen.tack2bounds(Tack.CENTER, x, y, w, h);

        sten.pen()
            .setFill(getBackground())
            .setStroke(getForeground())
            .fillOval(box)
            .strokeOval(box);

        return box;
    }

    public Bounds drawDotOpenOffset(Stencil sten) {
        var w = 6;
        var h = 6;
        var box = Pen.tack2bounds(Tack.EAST, x, y, w, h);

        sten.pen()
            .setFill(getBackground())
            .setStroke(getForeground())
            .fillOval(box)
            .strokeOval(box);

        return box;
    }
}
