package pen.pen;

import javafx.geometry.Point2D;
import javafx.geometry.Pos;

public class StencilLabel extends StyleBase<StencilLabel> {
    //---------------------------------------------------------------------
    // Instance Variables

    private final Stencil stencil;

    private double x;
    private double y;
    private String text = "";
    private Pos pos = Pos.TOP_LEFT;

    //---------------------------------------------------------------------
    // Constructor

    public StencilLabel(Stencil stencil) {
        this.stencil = stencil;
    }

    //---------------------------------------------------------------------
    // DSL

    public StencilLabel text(String text) {
        this.text = text;
        return this;
    }

    public StencilLabel at(Point2D point) {
        return at(point.getX(), point.getY());
    }

    public StencilLabel at(double x, double y) {
        this.x = x;
        this.y = y;
        return this;
    }

    public StencilLabel pos(Pos pos) {
        this.pos = pos;
        return this;
    }

    public void draw() {
        stencil.pen().save()
            .setFill(getTextColor())
            .setFont(getFont())
            .setTextBaseline(pos.getVpos())
            .setTextAlign(Pen.pos2textAlign(pos))
            .fillText(text, x, y)
            .restore();
    }
}
