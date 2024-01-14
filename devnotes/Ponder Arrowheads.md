#task 

Think about how to implement arrowheads.

## Distinct Symbols

Q: Should arrowheads be available as distinct symbols, or only as part of the line drawing code?

 **A: Distinct symbols**

-  Then they can be used with bezier splines and other curves.

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

**A: Using Tack.**

An arrowhead is like any other shape, in that it should have a `Tack` setting.  If we draw all arrowheads at a consistent orientation, then the client can pick, e.g., `Tack.EAST` or `Tack.CENTER` as needed.

**A: As a flag on lines.**

When an arrowhead is added to a line, the client can specify whether it should be adjacent (the default) or centered.

## What shall we call them?

What's a good name for these things we're drawing?

- Arrowheads
- More generically, *symbols*
    - A symbol differs from a normal shape in that it has a standard size that's appropriate for most uses, but can be scaled bigger if needed.
    - It's meant to be put at or adjacent to a point, and is meant to be solitary.

## What symbols shall we provide?

- Solid arrowhead
- Open arrowhead
- Solid circle
- Open circle
- Others as needed, e.g., composition diamond, class inheritance triangle, map point, etc.


