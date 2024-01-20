[[Home]] > [[Ideas]] #idea 

I'm having naming problems; how to resolve?  A better naming convention!  Not everything needs to begin with "Stencil"!

## To Do

- [ ] Implement the [[#Class Hierarchy]] shown below.

## Goals

- Clarity
- Simplicity
- Avoid collisions with JavaFX widget names
    - E.g., avoid `Shape`, `Label`, `Line`, etc.

## Principles

- All abstract base classes are subclasses of StencilStyleBase
    - There's no need to go crazy with the class hierarchy; it just adds to maintenance headaches as the classes evolve.
- Add a new base class only when we've got a pattern we expect to have three or more members.
- Do not implement composition of shapes beyond what already exists.
    - *Ad hoc* `StencilShapes` are just lambdas.
    - A new shape class can reuse other shapes already, and implement the DSL.
## Class Hierarchy

- `Drawing` (interface, does not return `Bounds`, represents an entire image)
- `Drawable` (interface, returns `Bounds`), drawn as part of a `Drawing`
    - Base classes, representing parameter patterns
        - SimpleShape
            - Adds nothing; just gives all simple shapes a type.
        - BoundedShape
            - Adds `at`, `tack`, `size`.
            - The shape is drawn to fill its size.
        - ContentShape
            - Adds `at`, `tack`, `minSize`, content parameters (usually a label)
            - The shape is drawn to fit its content, but no smaller than `minSize`.
    - Concrete Shape Classes
        - SimpleShapes
            - LineShape (`from`, `to...`)
            - SymbolShape (`at`, `symbol`)
            - TextShape (`at`, `tack`, `text`)
        - BoundedShapes (`at`, `tack`, `size`)
            - RectangleShape ()
            - Ultimately, many others
        - ContentShapes (`at`,`tack`,`minSize`)
            - BoxedTextShape (`text`)
            - Diagrams would likely define many of these

## Ponderings

### Things that can be drawn

| Current | Better? | Note |
| ---- | ---- | ---- |
| StencilDrawing | StencilDrawing | Interface, good as is |
| StencilShape | StencilShape | Interface |
| > StencilBoundedShape | > BoundedShape | Abstract Base Class |
| >> StencilRect | >> RectShape | Drawable shape |
| > StencilContentShape | > ContentShape | Abstract Base Class |
| >> StencilBoxedLabel | >> BoxedLabelShape |  |
| >> StencilLabel | >> LabelShape | Under content shape? |
| StencilLine | > LineShape, PolyLineShape |  |
| StencilSymbol | > SymbolShape |  |
### Observations

- The two interfaces can reasonably use `Stencil` in their names; it differentiates them from the shapes.
- Need the difference between `BoundedShape` and `ContentShape` be explicit to the client?
    - Some shapes have `at` and `size`; some have `at` and `minSize` and some content.
- At present, `BoxedLabelShape` can only contain text.  Do I want to allow it to contain any shape?
    - No.  See [[Creating Custom Shapes in Tcl]].
    - The point of `BoxedLabelShape`, or whatever I call it, is as a convenience for simple diagrams, when a more complex diagram API isn't needed.
    - And if it *is* needed, we'll add it.


