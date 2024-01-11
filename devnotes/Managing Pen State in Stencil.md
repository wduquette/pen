[[Home]] > [[Ideas]] #idea 

## To Do

- [ ] Make `Stencil::draw(StencilDrawing)` save and restore the pen state.
- [ ] Add `Pen`'s `save`, `restore`, `translate`, `rotate`, and `scale` API to `Stencil`
    - Ponder the names.
- [ ] Ponder [[Reset Pen State]]

## The Problem

`Pen`, like `GraphicsContext`, allows the client to push the current state onto a stack and pop it off again later.  This allows a drawing routine to make arbitrary changes without affecting later drawing.

```java
pen.save();
// Do some drawing, with state changes
pen.restore();
```

The problem is that the stack is hidden from the user.  There's no way to track changes to it, or to pinpoint a missing `restore()`.  Moreover, it really ought to be done like this:

```java
pen.save();
try {
    // Do some drawing, with state changes
} finally {
    pen.restore();
}
```

I would like to make this pattern easier when using `Stencil`.

There are several cases.

- Using `Stencil::pen` directly
- `StencilShape` state changes
- `Stencil` transformations

### Using Stencil::pen Directly

A drawing can access the `Stencil`'s `Pen` directly, if so desired:

```java
stencil.pen().setLineWidth(5);
```

`Pen` is meant to be a value-added `GraphicsContext`, so we aren't going to add a different kind of state-management to `Pen`.  

Therefore, if a drawing accesses the `Pen` directly, it's responsible for managing state changes.

### StencilShape State Changes

`Stencil::draw(StencilShape)` takes care of this automatically.  The state is saved before the shape draws itself, and is restored afterwards.  Thus, a shape can make any state changes it likes, without worry.

This does cover 90% of the issue.

### Stencil Transformations

There's little reason to set line colors and widths, etc., at the drawing level.  A drawing can use styles to set defaults; and this is much safer than trying to set defaults on the `Stencil::pen`.

But it is often desirable to set transformations for all or part of a drawing, above the individual shape level.  For example, one might put several drawings on one canvas by setting the translation before each.

## Potential Solutions

### Option A: It's the Client's Problem

At the drawing level, it's the client's problem.

- `Stencil` simply delegates the `save`, `restore`, `translate`, `rotate`, and `scale` methods to its `Pen`.
- It's up to the client to use them wisely.

Pros:

- Simple.
- Same discipline as using a `GraphicsContext`

Cons:

- `GraphicsContext` provides no way to restore the settings to their defaults.
    - See [[Reset Pen State]]
- One could add `Pen::reset` and then delegate to that as well.
- The `save` and `restore` terminology makes it unclear that there's a stack.

### Option B: Use better names

Use better names for `save`, `restore`, and `reset`

- E.g., `pushPen`, `popPen`, `resetPen`.

Cons

- I've not come up with any method names that I like.
- It adds an artificial difference between `Pen` and `Stencil`.

Pros

- In theory, this could be clearer.

### Option C: Stencil::with

Provide the API in Option A or Option B, and then define a method that takes a lambda and does this when called:

- Save
- Call lambda
- Restore safely

Pros

- Easy to use; handles the problem automatically.

Cons

- If you remember to use it.
- All variables used in the lambda must be effectively final, which becomes a nuisance very quickly.
    - Or you have to break up your drawing into multiple methods.

### Option D: Stencil::draw(StencilDrawing)

There's no reason why `Stencil::draw(StencilDrawing)` can't automatically save and restore; this might handle the other 10% of the cases.

There's no downside here; this should certainly be done.

### Option E: StencilGroup

A `StencilGroup` would be a `StencilShape` that accumulates `StencilShapes` and transforms and can be drawn as a unit.  As a shape, it would save and restore automatically when drawn by the `Stencil`.

This is initially appealing, but thinking about it is seems like a construct that should exist at the layer above, i.e., the diagram layer, not at the `Stencil` layer.  `Stencil` is an immediate drawing tool; trying to add retained features to it strikes me as a big mistake.

### Option F: Transform Tokens

Add a `Stencil::transform` object.  Creating it applies the desired transform; ending it removes it.

```java
var xform = stencil.transform().translate(x,y).rotate(degrees);
// Drawing...
xform.end();
```

Again, this is initially appealing; but given the reality of how `GraphicsContext` manages its state, it's really no different than this:

```
stencil.pen().save();
// Drawing...
stencil.pen().restore();
```

I.e., either Option A or Option B gets at the same thing.

## Analysis

- Option D makes all kinds of sense; we should do it.
- Then, the only other options that make sense are Options A and B
    - Including the  "reset" option.
    - B if I can think of good names, and A otherwise.
