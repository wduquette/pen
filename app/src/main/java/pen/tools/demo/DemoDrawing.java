package pen.tools.demo;

import pen.stencil.Drawing;

/**
 * A named StencilDrawing
 * @param name The name
 * @param drawing The drawing
 */
public record DemoDrawing(
    String name,
    Drawing drawing
) {
    public String toString() {
        return name;
    }
}
