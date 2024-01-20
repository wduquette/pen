[[Home]] > [[Ideas]] #idea #rejected 

## Shape Nesting

It's a natural idea to let a content shape be composed of other shapes.  This is how JavaFX widgets work, after all.

But Stencil is *not* a retained GUI; shapes are created and are drawn immediately.

Or are they?  A `StencilShape` is an object that can be drawn.  In Java, it can be drawn, modified, and drawn again. A shape can certainly be given another shape, and then the whole thing drawn together.

The tricky bit would be extending this to the Tcl API, which doesn't expose shapes in this way.

- In Tcl, to have identity a shape must have either an ID or be a command.
    - Cleaning up IDs is easy, but complicates the API.
    - Cleaning up commands is a nuisance.

On the whole, I think this is a bridge too far.

- Stencil is meant to be a friendly, convenient, but low-level drawing API: basic shapes, and pixel coordinates.
- Fancy diagrams are meant to be built on top of it in Java, and then exposed via their own APIs.
- Tcl already has a way of composing commands; it's called a `proc`.
- And shapes are graphically composed according to the drawing order.
- If I had a strong use case, maybe, but for now I think not.

## Defining Shapes in Tcl

But suppose I did extend the open shape API to Tcl.  It would necessarily look something like this:

```tcl
stencil draw [line -from 0,0 -to 100,100] ;# OR
stencil draw [shape line -from 0,0 -to 100,100]
```

Suppose:

- We have shape factory commands.
- Each shape factory creates a Java shape, and assigns it an ID.
- The created shapes are retained in the Tcl extension by ID
- The shape factory returns the ID
- The `stencil draw` method accepts the ID and draws the shape.
- The difficulty is cleaning up the shape once it's drawn.
    - Though shapes would be cleaned up on `stencil clear` in any event.
- The script can then modify the shape like this:

```tcl
set id [line -from 0,0 -tox 100]
for {set i 0} {$i < 10} {incr i} {
    set y [expr {$i*10}]
    line $id -from 0,$y
    stencil draw $id
}
```
This would also let the script build up a complex nested shape out of bits, and then draw it all at once.

Ways to do cleanup:

**Option A:** `stencil clear` only.

**Option B:** `stencil draw $id ?-keep?`

By default, `stencil draw` consumes the shape; if `-keep` is given, it doesn't.  

- We could also have two distinct drawing commands instead of an option.
- Difficulties
    - How to clean up nested shapes in a compound shape?
    - The Tcl API would need to keep track of the IDs of all such shapes, so that they can be consumes with the outermost shape.

**Option C:** `line -keep ...`

- All shape factories have a `-keep` option
- A created shape is marked `kept` if `-keep` is provided.
- If not `kept`, the shape ID is consumed when the shape is drawn or added to a compound shape.

