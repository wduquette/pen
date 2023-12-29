package pen.pen;

import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.canvas.GraphicsContext;

import java.awt.geom.Dimension2D;

@SuppressWarnings("unused")
public class Stencil {
    //-------------------------------------------------------------------------
    // Instance Variables

    private final Pen pen;

    //-------------------------------------------------------------------------
    // Constructor

    public Stencil(GraphicsContext context) {
        this.pen = new Pen(context);
    }

    //-------------------------------------------------------------------------
    // DSL

    public void clear() {
        pen.clear();
    }

    public Pen pen() {
        return pen;
    }

    public Label label() {
        return new Label();
    }

    public Rect rect() {
        return new Rect();
    }


    //-------------------------------------------------------------------------
    // Helpers

    public class Label extends StyleBase<Label> {
        //---------------------------------------------------------------------
        // Instance Variables

        double x;
        double y;
        String text = "";
        Pos pos = Pos.TOP_LEFT;

        //---------------------------------------------------------------------
        // Constructor

        public Label() {  }

        //---------------------------------------------------------------------
        // DSL

        public Label text(String text) {
            this.text = text;
            return this;
        }

        public Label at(Point2D point) {
            return at(point.getX(), point.getY());
        }

        public Label at(double x, double y) {
            this.x = x;
            this.y = y;
            return this;
        }

        public Label pos(Pos pos) {
            this.pos = pos;
            return this;
        }

        public void draw() {
            pen.save()
                .setFill(getTextColor())
                .setFont(getFont())
                .setTextBaseline(pos.getVpos())
                .setTextAlign(Pen.pos2textAlign(pos))
                .fillText(text, x, y)
                .restore();
        }
    }

    public class Rect extends StyleBase<Rect> {
        //---------------------------------------------------------------------
        // Instance Variables

        double x;
        double y;
        double w;
        double h;

        //---------------------------------------------------------------------
        // Constructor

        public Rect() {
            // Nothing to do yet
        }

        //---------------------------------------------------------------------
        // DSL

        public Rect at(Point2D point) {
            return at(point.getX(), point.getY());
        }

        public Rect at(double x, double y) {
            this.x = x;
            this.y = y;
            return this;
        }

        public Rect size(Dimension2D size) {
            return size(size.getWidth(), size.getHeight());
        }

        public Rect size(double w, double h) {
            this.w = w;
            this.h = h;
            return this;
        }

        public void draw() {
            pen.save()
                .setFill(getBackground())
                .setStroke(getForeground())
                .setLineWidth(getLineWidth())
                .fillRect(x, y, w, h)
                .strokeRect(x, y, w, h)
                .restore();
        }
    }

}
