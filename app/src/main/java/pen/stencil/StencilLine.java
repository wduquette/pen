package pen.stencil;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class StencilLine
    extends StyleBase<StencilLine>
    implements StencilShape
{
    //---------------------------------------------------------------------
    // Instance Variables

    // The points on the line
    private final List<Point2D> points = new ArrayList<>();

    // The symbols at the start and end of the line.
    private Symbol startSymbol = Symbol.NONE;
    private Symbol endSymbol = Symbol.NONE;

    //---------------------------------------------------------------------
    // Constructor

    public StencilLine() {
        // Nothing to do
    }

    //---------------------------------------------------------------------
    // DSL

    public StencilLine to(Point2D point) {
        points.add(point);
        return this;
    }

    public StencilLine to(double x, double y) {
        return to(new Point2D(x,y));
    }

    public StencilLine toX(double x) {
        return to(new Point2D(x,last().getY()));
    }

    public StencilLine toY(double y) {
        return to(new Point2D(last().getX(),y));
    }

    public StencilLine points(List<Point2D> points) {
        this.points.addAll(points);
        return this;
    }

    public StencilLine start(Symbol symbol) {
        this.startSymbol = Objects.requireNonNull(symbol);
        return this;
    }

    public StencilLine end(Symbol symbol) {
        this.endSymbol = Objects.requireNonNull(symbol);
        return this;
    }

    public Bounds draw(Stencil stencil) {
        stencil.pen().save()
            .setStroke(getForeground())
            .setLineWidth(getLineWidth())
            .strokePolyline(points)
            .restore();

        if (points.size() > 1 && startSymbol != Symbol.NONE) {
            drawSymbol(stencil, startSymbol, points.get(1), points.get(0));
        }

        if (points.size() > 1 && endSymbol != Symbol.NONE) {
            var n = points.size() - 1;
            drawSymbol(stencil, endSymbol, points.get(n-1), points.get(n));
        }

        return Pen.boundsOf(points);
    }

    private void drawSymbol(
        Stencil sten,
        Symbol symbol,
        Point2D from,
        Point2D to
    ) {
        var angle = Pen.angleOf(from, to);
        sten.savePen()
            .translate(to.getX(), to.getY())
            .rotate(angle)
            .draw(Stencil.symbol()
                .symbol(symbol)
                .at(0, 0))
            .restorePen();
    }

    private Point2D last() {
        return points.isEmpty()
            ? Point2D.ZERO
            : points.getLast();
    }
}
