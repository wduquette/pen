package pen.stencil;

import javafx.geometry.Bounds;

/**
 * A StencilShape is an object that knows how to draw itself on a Stencil.
 */
public interface StencilShape {
    Bounds draw(Stencil sten);
}
