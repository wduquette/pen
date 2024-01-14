#task 

Think about how to implement arrowheads.

## Distinct Symbols

Q: Should arrowheads be available as distinct symbols, or only as part of the line drawing code?

 **A: Distinct symbols**

-  Then they can be used with bezier splines and other curves.

## What shall we call them?

What's a good name for these things we're drawing?

- Arrowheads
- More generically, *symbols*
    - A symbol differs from a normal shape in that it has a standard size that's appropriate for most uses, but can be scaled bigger if needed.
    - It's meant to be put at or adjacent to a point, and is meant to be solitary.

## What symbols shall we provide?

- ARROW_SOLID
- ARROW_OPEN
- DOT_SOLID
- DOT_OPEN
- DOT_SOLID_OFFSET
- DOT_OPEN_OFFSET
- Others as needed, e.g., composition diamond, class inheritance triangle, map point, etc.

## Draw unrotated, or with rotation?

Q: Should the basic arrowhead code draw them unrotated, or with rotation? I.e., does the notion of an arrow head include rotation, or should we let the client specify the rotation?

**A: Let the client specify.**
        
- It's the client that knows the desired angle.
- Translation/rotation are part of the `Pen`/`Stencil` API, and can easily be added if need.  
    - Composition is a *good* thing.

## At or Adjacent to the origin

An arrowhead can be drawn centered at the end of the line, or with an origin point (i.e., the point of the arrow) adjacent to the the end of the line.

![[Drawing Arrowhead Position.excalidraw]]

Q: How should we manage this?

**A: Option B**

### Option A: Using Tack plus a flag on line end/start

An arrowhead is like any other shape, in that it should have a `Tack` setting.  If we draw all arrowheads at a consistent orientation, then the client can pick, e.g., `Tack.EAST` or `Tack.CENTER` as needed.

When an arrowhead is added to a line, the client can specify whether it should be adjacent (the default) or centered.

Pros:

- It makes symbols work like other shapes.

Cons:

- Most tacks aren't useful.
    - For arrowheads, WEST, CENTER, and EAST make some kind of sense.  The others do not.
    - Arrowheads are often rotated, which makes WEST, CENTER, and EAST harder to interpret.
- Some symbols can really only be drawn one way.  A Google-style map pin, for example, should always be vertical with its tip at the point of interest.
- Translation is a dance between the symbol and the client.
- Specifying two parameters for an arrowhead on a line is complex and unpleasant.

### Option B: as distinct symbols

Define a distinct symbol for each use case.

- A circle centered at the endpoint is a different use case than a circle offset from the endpoint the way an arrowhead normally is.
- Make them different symbols.

Pros

- Every symbol has a well-defined origin point and orientation.
- We eliminate the `tack` property.

Cons

- Some symbol names get longer.
