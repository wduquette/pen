package pen.pen;

import javafx.scene.canvas.GraphicsContext;

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

    public Pen pen() {
        return pen;
    }

    public Stencil clear() {
        pen.clear();
        return this;
    }

    public static StencilLabel label() {
        return new StencilLabel();
    }

    public static StencilRect rect() {
        return new StencilRect();
    }

    public Stencil draw(StencilShape shape) {
        shape.draw(this);
        return this;
    }

    //-------------------------------------------------------------------------
    // Helpers

}
