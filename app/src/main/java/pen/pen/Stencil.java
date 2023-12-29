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

    public void clear() {
        pen.clear();
    }

    public Pen pen() {
        return pen;
    }

    public StencilLabel label() {
        return new StencilLabel(this);
    }

    public StencilRect rect() {
        return new StencilRect(this);
    }

    public Stencil draw(StencilShape shape) {
        shape.draw(this);
        return this;
    }

    //-------------------------------------------------------------------------
    // Helpers

}
