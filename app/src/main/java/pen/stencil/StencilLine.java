package pen.stencil;

import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Dimension2D;
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

    public Bounds draw(Stencil stencil) {
        stencil.pen().save()
            .setStroke(getForeground())
            .setLineWidth(getLineWidth())
            .strokePolyline(points)
            .restore();

        return Pen.boundsOf(points);
    }
}
