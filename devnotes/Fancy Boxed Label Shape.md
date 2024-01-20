#idea #pondering

## To Do?

- [ ] Rename `StencilLabel` to `StencilText`, and maybe give it some aids for doing multiline text.
    - `StencilText` better reflects its low-level nature.
- [ ] Define `StencilContentShape` as a base class for shapes that size themselves to fit their content.
    - `minWidth`, `minHeight`
    - `pad`
    - Better name, if possible
- [ ] Define `StencilBox` as a `StencilContentShape` that displays text in a rectangular box. 
    - `text`
- Ponder:
    - Support multiple shapes in one element via a shape parameter?
    - Support multiple content types?
        - Text
        - Image
        - Drawing (?)
    - If I do this right, these could be used in a row/column grid diagram as-is. 

## Overview

Drawings often include labeled boxes even without using a full-fledged block diagram tool.  It would be convenient to allow `StencilLabel` to have the following:

- A `pad`, in pixels, that defaults to 0.
- A `foreground` color that defaults to `TRANSPARENT`
- A `background` color that defaults to `TRANSPARENT`

Then, it would be easy to draw boxed labels just by setting the style. 

## Analysis

There's an advantage to having a shape that draws a text string so that it fills a box; it's useful in prototyping diagrams and in creating block diagrams at the pixel level.  But `StencilLabel` is meant to be a low-level drawing primitive, and providing the box use case and the low-level text use case in the same shape is confusing.

The thing that makes it confusing is having the `foreground` and `background` both be `TRANSPARENT` by default, when those aren't the usual settings for those styles.
