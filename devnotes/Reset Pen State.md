[[Home]] > [[Ideas]] #idea   See also: [[Managing Pen State in Stencil]]

`GraphicsContext` provides no easy means of resetting its state to its defaults.  And yet, we'd like to be able to do that.  What are the possibilities?

## Option A: Recreate the Canvas

Each new `Canvas` gets a new `GraphicsContext` with default state.  Thus, one can virtually reset the state by removing the old `Canvas` and creating a new one.

Cons:

- `Stencil` is designed to work with an arbitrary `Canvas` owned by the application.
- The app can create a new `Canvas` if it likes, but we can't.

Thus, this is a non-starter so far as `Pen` is concerned.

## Option B: Copy all parameters

`GraphicsContext` has a stable API.  `Pen` could make copies of all the state parameters on creation via the `GraphicsContext::get*` methods and restore them on demand via the `GraphicsContext::set*` methods.

Pros:

- It definitely restores the parameters to a known state.

Cons:

- It doesn't address the saved `path` (though this is probably not a big deal)
- It doesn't clear previously saved states; looks like a memory leak to me.

## Option C: Maintain a count

`Pen` maintains a save/restore counter.

- Each `save` increments it.
- Each `restore` decrements it.
- A `reset` could simply restore until the counter is 0.
- If `restore` does nothing if the counter is 0, we've got the same semantics as `GraphicsContext`, so far as I can tell, with the ability to reset.
