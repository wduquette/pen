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

    // Rotation around the tack point
    private double rotationDegrees;

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

    public StencilSymbol rotate(double degrees) {
        this.rotationDegrees = degrees;
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

        // This is not right, I think.  Getting confused.
        sten.pen()
            .save()
            .translate(x, y)
            .rotate(rotationDegrees)
            .setFill(getForeground());

        double offset = switch (hpos) {
            case HPos.LEFT -> w;
            case HPos.CENTER -> w/2;
            case HPos.RIGHT -> 0;
        };
        double y0 = y - h/2;
        double x0 = x - offset;

//        sten.pen()
//            .fillPolygon(List.of(
//                new Point2D(offset - w, 0),
//                new Point2D(offset, -h / 2),
//                new Point2D(offset, h / 2)
//        )).restore();

        sten.pen()
            .translate(offset, 0)
            .fillPolygon(List.of(
                new Point2D(-w, 0),
                new Point2D(0, -h / 2),
                new Point2D(0, h / 2)
            )).restore();

        return new BoundingBox(x0, y0, w, h);
    }
}
