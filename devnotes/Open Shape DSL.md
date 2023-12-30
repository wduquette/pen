#idea #implemented

At time of writing, I expect `Stencil` to have a DSL method to create each kind of shape.  An obvious alternative would be to have an open API allowing the definition of new kinds of shape, any of which could be used without modifying `Stencil`.

That is, instead of this:

```
sten.rect().at(5,5).size(10,10).draw();
```

Use something more like this:

```
sten.draw(rect().at(5,5).size(10,10));
```

From the `Stencil`'s point of view, all it needs to be able to do is ask the shape to draw itself, passing the `Stencil` to allow the drawing to happen.

## Pros

- Eliminates the possibility of forgetting the `draw()` method.
- Decouples shapes from the `Stencil` class.
    - Makes it easier for diagram classes to define their own shapes.
- Allows a series of shapes to be drawn as a single statement.
- Allows for composition of shapes, e.g., groups.
    - Though if I do that I'm getting into JavaFX's "Shape" territory.

## Cons

- Slightly more verbose
- Does not give the shapes access to the `Stencil` at definition time.
    - Does not allow shapes to initialize themselves from a central style map owned by the `Stencil`
        - But we weren't going to do that anyway.
        - The Java code doesn't need it. (Specific diagram types can do things differently.)
        - The Tcl API will want a style map, but can handle it explicitly.
- Irrelevant to the Tcl API. 
    - We probably will not have a Tcl API for defining new shapes.
    - Of course, the entire DSL is fairly irrelevant to the Tcl API....

## Bottom Line

- Might be a good idea just to decrease coupling.
- Will not be a major aid to the Tcl API, at least in the near term.
- However, especially at home, KISS.