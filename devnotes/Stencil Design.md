[[Home]] > [[Design Notes]]

## Overview

- A `Stencil` is used to draw on a `Canvas`
    - A `Drawable` is an object that can be drawn on the canvas, and returns its bounds.
    - A `Drawing` is an entire image to be drawn on a canvas
- A `StencilBuffer` converts `Drawings` into PNG files.
- `StencilStyleBase` defines the style parameters used by `Drawables`
    - `StencilStyle` represents a named style in the `StencilStyleMap`.
    - `Drawables` usually extend `StencilStyleBase`

## Implementing Drawables

`Stencil` defines a number of `Drawables`, known collectively as *shapes*. (Diagram classes may implement their own `Drawables`.)

- All shapes extend `StencilStyleBase` by way of an abstract shape class.
- Every abstract shape class directly subclasses `StencilStyleBase`.
    - No deep hierarchy.
- Each abstract shape class implements a pattern we expect to be used by at least three concrete shape classes.

At present there are two abstract shape classes, with a third one planned.

- `SimpleShape` adds no parameters; it simply provides a type for shapes that don't follow a distinct pattern.
- `BoundedShape` is for shapes that are drawn given an `at`, `tack`, and `size`, and fill the size they are given.
- `ContentShape` *will be* for shapes that are drawn given an `at`, `tack`, and `minSize`, plus some content (often a string) and expand to fit their content or `minSize`, which ever is bigger.

All concrete `Stencil` shapes are currently going to belong  to one of these types.
