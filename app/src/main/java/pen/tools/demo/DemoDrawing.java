package pen.tools.demo;

import pen.stencil.StencilDrawing;

/**
 * A named StencilDrawing
 * @param name The name
 * @param drawing The drawing
 */
public record DemoDrawing(
    String name,
    StencilDrawing drawing
) {
    public String toString() {
        return "Drawing[" + name + "]";
    }
}
