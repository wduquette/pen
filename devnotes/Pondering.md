General thoughts on what it is I'm doing here, before I go to far down the current road.

## Observations

A *stencil* defines *shapes* you can draw with a *pen* (or pencil, etc.).

- It occurs to me that the `Style` objects I can now define are in fact *pens*.

My `Style` objects are treated as prototypes.

- A `Stencil.Rect` is initialized with a default style, and can then be modified.
- This makes a lot of sense.

## Kinds of Shape

It would be nice to have an open API allowing the definition of new kinds of shapes extensibly.

- So more like `FluentFX` than `Mage`.
- A shape has to know how to draw itself at a given point.
    - The `pos` can be a parameter of those shapes that care.
    - JavaFX `Pos` might not be the right model
    - (?) DEFAULT, CENTER, N, S, E, W, NE, NW, SE, SW.
- A shape is built on a `PenBase`, and can be drawn at point.

However, the Tcl API (which is the target) is going to have a predefined set of shapes.

## Shapes as Prototypes?

Perhaps shapes could be treated as prototypes as well.

Drawing a shape requires:

 - A location
     - A `Point2D` and  a `Pos`
 - A *shape*, which has parameters, including nominal size in whatever form is appropriate (width by height, radius)
       -  But diagrams are often going to want content sizing.
 - A *pen*, which has style parameters, including transformations and effects.
 
It seems to me that a *glyph* is a combination of a *shape* and a *pen*, allowing parameters of each to be modified.

- But are shapes useful on their own, without reference to (at least) a default pen?
    - In my experience so far, the shape parameters tend to be unique.
    - It's the style parameters that get shared.

## Catalog of Shapes/Glyphs?

We could have a catalog of named pens, shapes, and glyphs

- Tricky, because shapes are heterogeneous.
    - Need a catalog that handles heterogeneous content.
    - `Stencil::rect()` gets default Stencil.Rect
    - `Stencil::rect(name)` gets named one.

