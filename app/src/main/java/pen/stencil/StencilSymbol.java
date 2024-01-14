package pen.stencil;

import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.HPos;
import javafx.geometry.Point2D;

import java.util.List;

/**
 * Draws a given symbol. Symbols that imply a direction (i.e., arrowheads)
 * are drawing pointing left.
 */
public class StencilSymbol
    extends StyleBase<StencilSymbol>
    implements StencilShape
{
    //---------------------------------------------------------------------
    // Instance Variables

    // The symbol to draw
    private Symbol symbol = Symbol.SOLID_ARROW;

    // Its origin
    private double x;
    private double y;

    // Its unrotated position relative to its origin
    private HPos hpos = HPos.LEFT;
    private Tack tack = Tack.WEST;

    //---------------------------------------------------------------------
    // Constructor

    public StencilSymbol() {
        // Nothing to do
    }

    //---------------------------------------------------------------------
    // DSL

    public StencilSymbol symbol(Symbol symbol) {
        this.symbol = symbol;
        return this;
    }

    public StencilSymbol at(Point2D point) {
        return at(point.getX(), point.getY());
    }

    public StencilSymbol at(double x, double y) {
        this.x = x;
        this.y = y;
        return this;
    }

    public StencilSymbol hpos(HPos hpos) {
        this.hpos = hpos;
        return this;
    }

    public StencilSymbol tack(Tack tack) {
        this.tack = tack;
        return this;
    }

    public Bounds draw(Stencil stencil) {
        return switch (symbol) {
            case SOLID_ARROW -> drawSolidArrow(stencil);
            default -> throw new IllegalStateException(
                "Unexpected symbol: " + symbol);
        };
    }

    public Bounds drawSolidArrow(Stencil sten) {
        var w = 12;
        var h = 8;
        var box = Pen.tack2bounds(tack, x, y, w, h);

        sten.pen()
            .setFill(getForeground())
            .fillPolygon(List.of(
                new Point2D(box.getMinX(), box.getCenterY()),
                new Point2D(box.getMaxX(), box.getMinY()),
                new Point2D(box.getMaxX(), box.getMaxY())
            ));
        return box;
    }
}
