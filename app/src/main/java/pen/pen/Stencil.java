package pen.pen;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

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

    public Rect rect() {
        return new Rect();
    }

    //-------------------------------------------------------------------------
    // Helpers

    public class Rect {
        double x;
        double y;
        double w;
        double h;

        public Rect() {
            // Nothing to do yet
        }

        public Rect at(double x, double y) {
            this.x = x;
            this.y = y;
            return this;
        }

        public Rect size(double w, double h) {
            this.w = w;
            this.h = h;
            return this;
        }

        public void draw() {
            pen.save()
                .setLineWidth(1)
                .setStroke(Color.BLACK)
                .setFill(Color.WHITE)
                .fillRect(x, y, w, h)
                .strokeRect(x, y, w, h)
                .restore();
        }
    }
}
