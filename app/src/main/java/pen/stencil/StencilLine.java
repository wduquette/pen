package pen.stencil;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;

import java.util.ArrayList;
import java.util.List;

public class StencilLine
    extends StyleBase<StencilLine>
    implements StencilShape
{
    //---------------------------------------------------------------------
    // Instance Variables

    private final List<Point2D> points = new ArrayList<>();

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

    public Bounds draw(Stencil stencil) {
        stencil.pen().save()
            .setStroke(getForeground())
            .setLineWidth(getLineWidth())
            .strokePolyline(points)
            .restore();

        return Pen.boundsOf(points);
    }

    private Point2D last() {
        return points.isEmpty()
            ? Point2D.ZERO
            : points.get(points.size() - 1);
    }
}
