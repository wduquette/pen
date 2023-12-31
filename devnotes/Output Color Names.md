#idea 

The `stencil style cget` command always returns colors as hex strings, since that's what `Color::toString` does.

- Implement a conversion from `Color` to string name for at least some colors.
    - Note: JavaFX is no help.