#idea #completed

I don't want users to have to figure in the margin while they are drawing.  But even if you're just displaying the canvas, you're usually going to want a margin.

**Final**: Chose and implemented Option A.

## Option A: StencilBuffer Margin, Right/Bottom

This is the current implementation.  The user is responsible for determining the top and left margin by where they draw; the upper-left corner of the image is always at 0,0 as the client sees it.  But the user can specify right and bottom margins, which will be added to the drawing's maxX/maxY to determine the finished image size.

This is at least easy to explain.

The bottom/right margin should then be a Stencil parameter, not a StencilBuffer parameter, used to compute `Stencil::getImageSize()`; then, the Tcl API can easily expose it.

The `Stencil::getDrawingBounds()` method can continue to return the actual bounds of the non-background pixels.

In addition, Stencil should have `minWidth` and `minHeight` parameters that also affect the bounds, for the same reason.

## Option B: Magic Stencil Margin 

Suppose we do this:

- Stencil has a defined margin; it can be zero.
- On reset, Stencil does an automatic translate (-margin, -margin), so that 0,0 is the corner of the actual drawing area.
- On clear, Stencil clears the entire area.
- When the drawing bounds are requested, Stencil returns the bounds including the right and left margin.

Cons:

- Tricky, and not obvious.

Pros: Very cool if it can work automatically.
