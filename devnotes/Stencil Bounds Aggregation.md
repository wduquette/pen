#idea #implemented 

As we draw on the canvas, keep track of the bounding box of all drawn content.  This would allow us to automatically size PNG files given the content and a margin.

**Note:** The `Canvas` already needs to be big enough for the drawing before you save all or part of it as a PNG.  Thus, to use this feature you'd usually need
to draw the drawing twice: once to get the bounds, and a second time to get a canvas of the desired size.

## Challenges

Computing the bounds for objects subject to transformations.

- It's easy enough to make each shape compute its nominal bounds.
- The question is how, how to transfer those into the actual bounds given the current transformation.
- It's likely that JavaFX already has a way to do it; check the Javadoc.
- Yes! The following should do it.

```
var newBounds = gc.getTransform().transform(oldBounds);
```

Neither `Bounds` nor `BoundingBox` have a method to find the hull of two `Bounds`, but it's easy enough to compute.  We can add a static method to `Pen`.